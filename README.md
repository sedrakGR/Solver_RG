# SSRGT Solver — Docker Usage

Single-container image with embedded MongoDB 3.4 and Tomcat running the built WAR. No external Mongo required.

## Prerequisites
- Docker 20.10+ installed and running

## Build the Image
```bash
docker build -t solver-allinone .
```

## Run the Container
```bash
# Persist MongoDB data in a named volume
docker run --rm \
  -p 8080:8080 \
  -v solver_db:/data/db \
  --name solver \
  solver-allinone
```

## Access the App
- Browser: http://localhost:8080

## Configuration
- `MONGO_URL`: defaults to `mongodb://127.0.0.1:27017/yourdb` (points to the in-container MongoDB). Override if you want a different DB name:
  ```bash
  docker run --rm -p 8080:8080 -v solver_db:/data/db \
    -e MONGO_URL='mongodb://127.0.0.1:27017/mydb' \
    solver-allinone
  ```

## Data Persistence
- Data lives in Docker volume `solver_db` mounted at `/data/db`.
- Remove data (destructive):
  ```bash
  docker volume rm solver_db
  ```

## Logs and Control
- Tail logs: `docker logs -f solver`
- Stop: `Ctrl+C` if running in foreground, or `docker stop solver` if detached.

## Optional: docker-compose (single service)
If you prefer compose, create a minimal `docker-compose.yml`:
```yaml
version: '3.9'
services:
  app:
    build: .
    ports: ["8080:8080"]
    volumes: ["solver_db:/data/db"]
    environment:
      MONGO_URL: mongodb://127.0.0.1:27017/yourdb
volumes:
  solver_db:
```
Then run: `docker compose up --build` and open http://localhost:8080.

## Files
- `Dockerfile`: builds WAR via Gradle, bundles MongoDB 3.4 + Tomcat.
- `docker-entrypoint.sh`: starts `mongod` then Tomcat inside the container.

