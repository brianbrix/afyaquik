# ─── Stage 1: Build all frontend modules ──────────────────────────────────────
FROM node:20-alpine AS frontend-builder
WORKDIR /app

# Copy workspace root manifests
COPY afyaquik-frontend/package*.json ./

# Copy every module (including nurse & reports)
COPY afyaquik-frontend/shared       ./shared
COPY afyaquik-frontend/auth         ./auth
COPY afyaquik-frontend/admin        ./admin
COPY afyaquik-frontend/receptionist ./receptionist
COPY afyaquik-frontend/doctor       ./doctor
COPY afyaquik-frontend/pharmacy     ./pharmacy
COPY afyaquik-frontend/nurse        ./nurse
COPY afyaquik-frontend/reports      ./reports

RUN npm install --legacy-peer-deps

# Build workspace modules (shared, auth, admin, receptionist, doctor, pharmacy)
RUN npm run build:all

# Build non-workspace modules that still depend on @afyaquik/shared
RUN cd nurse   && npm run build
RUN cd reports && npm run build

# ─── Stage 2: Build Spring Boot application ────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS backend-builder
WORKDIR /app

# Cache Maven dependencies before copying source
COPY .mvn   .mvn
COPY mvnw   .
COPY pom.xml .
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q

# Application source
COPY src ./src

# Embed built frontends into static resources
COPY --from=frontend-builder /app/auth/build         ./src/main/resources/static/client/auth
COPY --from=frontend-builder /app/admin/build        ./src/main/resources/static/client/admin
COPY --from=frontend-builder /app/doctor/build       ./src/main/resources/static/client/doctor
COPY --from=frontend-builder /app/receptionist/build ./src/main/resources/static/client/receptionist
COPY --from=frontend-builder /app/pharmacy/build     ./src/main/resources/static/client/pharmacy
COPY --from=frontend-builder /app/nurse/build        ./src/main/resources/static/client/nurse
COPY --from=frontend-builder /app/reports/build      ./src/main/resources/static/client/reports

RUN ./mvnw package -DskipTests -q

# ─── Stage 3: Runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=backend-builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
