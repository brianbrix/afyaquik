#!/bin/bash

# start.sh — Run the AfyaQuik application
#
# Usage:
#   ./start.sh           Build images and start all services via Docker Compose
#   ./start.sh --down    Stop and remove Docker Compose services
#   ./start.sh --dev     Run frontends + backend locally without Docker
#   ./start.sh --help    Show this help message

set -e

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
FRONTEND_DIR="$ROOT_DIR/afyaquik-frontend"

# ─── Colours ─────────────────────────────────────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
info()    { echo -e "${CYAN}[INFO]${NC}  $*"; }
success() { echo -e "${GREEN}[OK]${NC}    $*"; }
warn()    { echo -e "${YELLOW}[WARN]${NC}  $*"; }
error()   { echo -e "${RED}[ERROR]${NC} $*" >&2; exit 1; }

# ─── Help ─────────────────────────────────────────────────────────────────────
if [[ "$1" == "--help" ]]; then
  grep '^#' "$0" | sed 's/^# //' | sed 's/^#//'
  exit 0
fi

# ─── Docker mode (default) ────────────────────────────────────────────────────
run_docker_mode() {
  command -v docker >/dev/null 2>&1 || error "Docker not found. Install Docker Desktop."

  info "Building and starting all services with Docker Compose..."
  docker compose up --build

  echo ""
  echo -e "${GREEN}  Application available at:${NC}"
  echo "    http://localhost:8080/client/auth"
  echo "    http://localhost:8080/client/admin"
  echo "    http://localhost:8080/client/doctor"
  echo "    http://localhost:8080/client/receptionist"
  echo "    http://localhost:8080/client/pharmacy"
  echo "    http://localhost:8080/client/nurse"
  echo "    http://localhost:8080/client/reports"
}

# ─── Docker down ─────────────────────────────────────────────────────────────
run_docker_down() {
  command -v docker >/dev/null 2>&1 || error "Docker not found."
  info "Stopping Docker Compose services..."
  docker compose down
  success "Services stopped."
}

# ─── Dev mode (local, no Docker) ─────────────────────────────────────────────
DEV_PIDS=()

cleanup_dev() {
  echo ""
  info "Shutting down dev servers..."
  for pid in "${DEV_PIDS[@]}"; do
    kill "$pid" 2>/dev/null || true
  done
  info "Stopping PostgreSQL container..."
  docker compose stop postgres 2>/dev/null || true
  success "All processes stopped."
}

check_dev_prereqs() {
  info "Checking prerequisites..."
  command -v java   >/dev/null 2>&1 || error "Java not found. Install JDK 17+."
  command -v node   >/dev/null 2>&1 || error "Node.js not found. Install Node 18+."
  command -v npm    >/dev/null 2>&1 || error "npm not found."
  command -v docker >/dev/null 2>&1 || error "Docker not found. Install Docker Desktop."
  success "Prerequisites OK"
}

start_postgres() {
  info "Starting PostgreSQL container..."
  docker compose up -d postgres
  info "Waiting for PostgreSQL to be ready..."
  local retries=20
  until docker compose exec -T postgres pg_isready -U postgres -q 2>/dev/null; do
    retries=$((retries - 1))
    [[ $retries -le 0 ]] && error "PostgreSQL did not become ready in time."
    sleep 2
  done
  success "PostgreSQL is ready on localhost:5433"
}

start_frontend_dev() {
  local name=$1 port=$2
  local dir="$FRONTEND_DIR/$name"
  [[ -d "$dir" ]] || { warn "Frontend '$name' not found — skipping."; return; }
  info "Starting $name dev server on http://localhost:$port ..."
  BROWSER=none PORT=$port npm start --prefix "$dir" > "$ROOT_DIR/logs/${name}.log" 2>&1 &
  DEV_PIDS+=($!)
}

run_dev_mode() {
  check_dev_prereqs
  trap cleanup_dev EXIT INT TERM

  start_postgres

  info "Installing frontend dependencies..."
  cd "$FRONTEND_DIR" && npm install --legacy-peer-deps && cd "$ROOT_DIR"
  success "Dependencies installed"

  # Build shared first so all modules get the latest compiled dist/
  info "Building @afyaquik/shared..."
  cd "$FRONTEND_DIR/shared" && npm run build && cd "$ROOT_DIR"
  success "@afyaquik/shared built"

  # Keep shared watched so source changes are recompiled automatically
  info "Starting @afyaquik/shared watcher..."
  cd "$FRONTEND_DIR/shared" && npm run watch > "$ROOT_DIR/logs/shared.log" 2>&1 &
  DEV_PIDS+=($!)
  cd "$ROOT_DIR"

  mkdir -p "$ROOT_DIR/logs"

  start_frontend_dev auth         3000
  start_frontend_dev admin        3001
  start_frontend_dev doctor       3003
  start_frontend_dev receptionist 3004
  start_frontend_dev pharmacy     3006
  start_frontend_dev nurse        3007
  start_frontend_dev reports      3005

  echo ""
  echo -e "${GREEN}╔══════════════════════════════════════════════════════╗${NC}"
  echo -e "${GREEN}║           AfyaQuik — Dev Mode                       ║${NC}"
  echo -e "${GREEN}╠══════════════════════════════════════════════════════╣${NC}"
  echo -e "${GREEN}║  Entry point (auth / module hub):                   ║${NC}"
  echo -e "${GREEN}║    http://localhost:3000                             ║${NC}"
  echo -e "${GREEN}╠══════════════════════════════════════════════════════╣${NC}"
  echo -e "${GREEN}║  Module dev servers:                                 ║${NC}"
  echo    "║    admin         → http://localhost:3001             ║"
  echo    "║    doctor        → http://localhost:3003             ║"
  echo    "║    receptionist  → http://localhost:3004             ║"
  echo    "║    reports       → http://localhost:3005             ║"
  echo    "║    pharmacy      → http://localhost:3006             ║"
  echo    "║    nurse         → http://localhost:3007             ║"
  echo -e "${GREEN}╠══════════════════════════════════════════════════════╣${NC}"
  echo -e "${GREEN}║  Backend API:  http://localhost:8080                 ║${NC}"
  echo -e "${YELLOW}║  Note: cross-module navigation in dev opens the      ║${NC}"
  echo -e "${YELLOW}║  module directly at its own port above.              ║${NC}"
  echo -e "${YELLOW}║  Use ./start.sh for the fully integrated experience. ║${NC}"
  echo -e "${GREEN}╚══════════════════════════════════════════════════════╝${NC}"
  echo ""

  info "Opening entry point in browser..."
  open "http://localhost:3000" 2>/dev/null || true

  info "Starting Spring Boot backend on http://localhost:8080 ..."
  ./mvnw spring-boot:run
}

# ─── Main ─────────────────────────────────────────────────────────────────────
case "${1:-}" in
  --down) run_docker_down ;;
  --dev)  run_dev_mode    ;;
  "")     run_docker_mode ;;
  *)      error "Unknown option: $1  (use --help)" ;;
esac
