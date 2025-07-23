#!/bin/bash

set -e

TARGET_BASE="../src/main/resources/static/client"

copy_module() {
  MODULE=$1
  echo "Copying $MODULE..."
  rm -rf "$TARGET_BASE/$MODULE"
  mkdir -p "$TARGET_BASE/$MODULE"
  cp -r "$MODULE/build/"* "$TARGET_BASE/$MODULE/"
}

copy_module admin
copy_module auth
copy_module doctor
copy_module receptionist
copy_module reports
copy_module nurse
copy_module pharmacy

echo "All modules copied successfully."
