#!/usr/bin/env bash
# Start the Solver webapp for local use and sync the web assets the baked
# image is missing. Safe to re-run; it does not touch the database.
set -e
cd "$(dirname "$0")"
COMPOSE="docker compose -f docker-compose.yml -f docker-compose.dev.yml"
APP=solver_rg-app-1

echo "==> starting containers"
$COMPOSE up -d --no-build

echo -n "==> waiting for Tomcat "
for i in $(seq 1 40); do
  code=$(curl -s -o /dev/null -w "%{http_code}" -m 2 http://localhost:8080/home.action || true)
  [ "$code" = "200" ] && { echo " ready"; break; }
  echo -n "."; sleep 2
done
[ "$code" = "200" ] || { echo " FAILED (last HTTP $code)"; docker logs --tail 30 $APP; exit 1; }

# The image's WAR predates several UI fixes (notably the jQuery 1.5.2
# compatible handler for the image-upload button). Push the current files in.
echo "==> syncing web assets from WebContent/"
sync_web() {
  for f in static/js/chessSituation.js static/js/nucConcept.js static/js/primConcept.js \
           windows/chess.html windows/nucContent.html; do
    docker cp "WebContent/$f" "$APP:/usr/local/tomcat/webapps/ROOT/$f"
  done
}
sync_web

# The baked WAR also predates the chess-convention fix in Situation /
# SituationManager (Bishop=2, Knight=3). Recompile those two against the
# container's own classpath and push them in if they differ.
if command -v javac >/dev/null 2>&1; then
  echo "==> checking Java classes"
  W=$(mktemp -d); trap 'rm -rf "$W"' EXIT
  docker cp "$APP:/usr/local/tomcat/webapps/ROOT/WEB-INF/lib" "$W/lib" >/dev/null
  docker cp "$APP:/usr/local/tomcat/webapps/ROOT/WEB-INF/classes" "$W/classes" >/dev/null
  mkdir -p "$W/out"
  if javac -nowarn -cp "$W/classes:$W/lib/*" -d "$W/out" \
       src/org/ppit/core/percept/Situation.java \
       src/org/ppit/core/percept/SituationManager.java 2>"$W/javac.log"; then
    CHANGED=0
    for c in org/ppit/core/percept/Situation.class org/ppit/core/percept/SituationManager.class; do
      if ! cmp -s "$W/out/$c" "$W/classes/$c"; then
        docker cp "$W/out/$c" "$APP:/usr/local/tomcat/webapps/ROOT/WEB-INF/classes/$c"
        CHANGED=1
      fi
    done
    if [ "$CHANGED" = "1" ]; then
      echo "==> classes updated, restarting Tomcat"
      docker restart "$APP" >/dev/null
      for i in $(seq 1 40); do
        code=$(curl -s -o /dev/null -w "%{http_code}" -m 2 http://localhost:8080/home.action || true)
        [ "$code" = "200" ] && break; sleep 2
      done
      sync_web
    else
      echo "    classes already current"
    fi
  else
    echo "    WARN: javac failed, container keeps its baked classes:"; sed -n '1,5p' "$W/javac.log"
  fi
else
  echo "==> javac not found - skipping Java class sync (Bishop/Knight fix may be missing)"
fi

echo "==> registered NN classifiers:"
curl -s -m 5 http://localhost:8080/listClassifiers.action \
  | python3 -c "import json,sys;[print('    %-24s %-12s %s'%(c['classifierId'],c['kind'],c['classNames'])) for c in json.load(sys.stdin)['json']['classifiers']]" 2>/dev/null || true

echo
echo "Solver is running:  http://localhost:8080/"
