#!/bin/bash
# creates all 7 databases (Mac). PostgreSQL must be running, user postgres, password admin
cd "$(dirname "$0")" || exit 1
export PGPASSWORD=admin

PSQL=$(command -v psql)
if [ -z "$PSQL" ]; then
  for p in /Library/PostgreSQL/*/bin/psql /Applications/Postgres.app/Contents/Versions/latest/bin/psql /opt/homebrew/bin/psql /usr/local/bin/psql; do
    if [ -x "$p" ]; then PSQL="$p"; break; fi
  done
fi
if [ -z "$PSQL" ]; then
  echo "psql not found. Use the pgAdmin way given in README.md"
  exit 1
fi

"$PSQL" -h localhost -p 5432 -U postgres -d postgres -f database/setup-all.sql
