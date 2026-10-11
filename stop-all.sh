#!/bin/bash
# stops everything running on ports 8080 to 8087 (Mac)
for p in 8080 8081 8082 8083 8084 8085 8086 8087; do
  pid=$(lsof -ti tcp:$p)
  if [ -n "$pid" ]; then
    kill $pid
    echo "Stopped port $p"
  fi
done
