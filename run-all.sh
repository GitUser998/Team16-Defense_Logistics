#!/bin/bash
# runs the whole project on Mac: databases, build, all 8 apps, then opens the browser
# needs: Java 17+, PostgreSQL running (user postgres, password admin), internet for first time

cd "$(dirname "$0")" || exit 1
mkdir -p logs
chmod +x */mvnw *.sh 2>/dev/null

SERVICES="personnel-service:8081 equipment-service:8082 vehicle-service:8083 inventory-service:8084 request-service:8085 maintenance-service:8086 deployment-service:8087 logistics-ui:8080"

# 1. check java
if ! command -v java >/dev/null 2>&1; then
  echo "Java not found. Install JDK 17 or higher and run again."
  exit 1
fi
JAVA_MAJOR=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d. -f1)
if [ "$JAVA_MAJOR" -lt 17 ] 2>/dev/null; then
  echo "Java $JAVA_MAJOR found, but 17 or higher is needed."
  exit 1
fi
echo "[1/6] Java $JAVA_MAJOR is ok"

# 2. databases
echo "[2/6] Creating databases ..."
./setup-database.sh > logs/database-setup.log 2>&1
if ! grep -q "DONE - all 7 databases are ready" logs/database-setup.log; then
  echo "Database setup failed. See logs/database-setup.log"
  echo "Check that PostgreSQL is running and the postgres password is admin."
  tail -5 logs/database-setup.log
  exit 1
fi
echo "      databases are ready"

# 3. stop old copies and build one by one
./stop-all.sh >/dev/null 2>&1
echo "[3/6] Building (first time it downloads libraries, can take a few minutes) ..."
for item in $SERVICES; do
  d=${item%%:*}
  echo "      $d"
  (cd "$d" && ./mvnw -q -DskipTests compile > "../logs/$d-build.log" 2>&1)
  if [ $? -ne 0 ]; then
    echo "Build failed for $d. See logs/$d-build.log"
    exit 1
  fi
done

# 4. start all
echo "[4/6] Starting the 8 applications ..."
for item in $SERVICES; do
  d=${item%%:*}
  (cd "$d" && nohup ./mvnw -q spring-boot:run > "../logs/$d.log" 2>&1 &)
done

# 5. wait till all ports are open (about 3 minutes at most)
echo "[5/6] Waiting for the applications ..."
for i in $(seq 1 90); do
  ready=0
  for item in $SERVICES; do
    p=${item##*:}
    if lsof -ti tcp:$p -sTCP:LISTEN >/dev/null 2>&1; then ready=$((ready+1)); fi
  done
  if [ "$ready" -eq 8 ]; then break; fi
  sleep 2
done

echo ""
for item in $SERVICES; do
  d=${item%%:*}; p=${item##*:}
  if lsof -ti tcp:$p -sTCP:LISTEN >/dev/null 2>&1; then
    echo "  UP    $d (port $p)"
  else
    echo "  DOWN  $d (port $p) -> see logs/$d.log"
  fi
done

# 6. browser
echo ""
echo "[6/6] Opening http://localhost:8080"
open http://localhost:8080
echo "To stop everything run ./stop-all.sh"
