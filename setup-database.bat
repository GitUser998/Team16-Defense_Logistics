@echo off
rem creates all 7 databases (Windows). PostgreSQL must be running, user postgres, password admin
cd /d "%~dp0"
set PGPASSWORD=admin

set "PSQL="
where psql >nul 2>&1 && set "PSQL=psql"
if not defined PSQL (
  for /d %%V in ("C:\Program Files\PostgreSQL\*") do (
    if exist "%%V\bin\psql.exe" set "PSQL=%%V\bin\psql.exe"
  )
)
if not defined PSQL (
  echo psql not found. Use the pgAdmin way given in README.md
  exit /b 1
)

"%PSQL%" -h localhost -p 5432 -U postgres -d postgres -f database/setup-all.sql
