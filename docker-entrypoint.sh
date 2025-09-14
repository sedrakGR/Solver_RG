#!/usr/bin/env bash
set -euo pipefail

# Trap TERM/INT to stop both processes cleanly
term_handler() {
  echo "[entrypoint] Stopping Tomcat and MongoDB..."
  if [[ -n "${CATALINA_PID:-}" && -f "$CATALINA_PID" ]]; then
    kill -TERM "$(cat "$CATALINA_PID")" 2>/dev/null || true
  else
    pkill -TERM -f 'org.apache.catalina.startup.Bootstrap' 2>/dev/null || true
  fi
  pkill -TERM mongod 2>/dev/null || true
  wait || true
}
trap term_handler TERM INT

# Ensure MongoDB data dir exists
mkdir -p /data/db

echo "[entrypoint] Starting MongoDB 3.4..."
mongod --dbpath /data/db --bind_ip 127.0.0.1 --port 27017 --nojournal --quiet &
MONGO_PID=$!

# Optionally wait for mongod to accept connections
for i in $(seq 1 30); do
  if mongo --quiet --host 127.0.0.1 --port 27017 --eval 'db.adminCommand("ping").ok' | grep -q '^1$'; then
    echo "[entrypoint] MongoDB is ready."
    break
  fi
  sleep 1
done

echo "[entrypoint] Starting Tomcat..."
export CATALINA_PID=/tmp/catalina.pid
exec catalina.sh run

