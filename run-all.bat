@echo off
setlocal enabledelayedexpansion
rem runs the whole project on Windows: databases, build, all 8 apps, then opens the browser
rem needs: Java 17+, PostgreSQL running (user postgres, password admin), internet for first time

cd /d "%~dp0"
if not exist logs mkdir logs

set SERVICES=personnel-service:8081 equipment-service:8082 vehicle-service:8083 inventory-service:8084 request-service:8085 maintenance-service:8086 deployment-service:8087 logistics-ui:8080

rem 1. check java
where java >nul 2>&1
if errorlevel 1 (
  echo Java not found. Install JDK 17 or higher and run again.
  pause & exit /b 1
)
set JAVA_MAJOR=0
for /f tokens^=3 %%V in ('java -version 2^>^&1 ^| findstr /i "version"') do set JV=%%~V
for /f "delims=. tokens=1" %%M in ("!JV!") do set JAVA_MAJOR=%%M
if !JAVA_MAJOR! LSS 17 (
  echo Java !JAVA_MAJOR! found, but 17 or higher is needed.
  pause & exit /b 1
)
echo [1/6] Java !JAVA_MAJOR! is ok

rem 2. databases
echo [2/6] Creating databases ...
call setup-database.bat > logs\database-setup.log 2>&1
findstr /C:"DONE - all 7 databases are ready" logs\database-setup.log >nul
if errorlevel 1 (
  echo Database setup failed. See logs\database-setup.log
  echo Check that PostgreSQL is running and the postgres password is admin.
  pause & exit /b 1
)
echo       databases are ready

rem 3. stop old copies and build one by one
call stop-all.bat >nul 2>&1
echo [3/6] Building (first time it downloads libraries, can take a few minutes) ...
for %%S in (%SERVICES%) do (
  for /f "delims=: tokens=1" %%D in ("%%S") do (
    echo       %%D
    pushd %%D
    call mvnw.cmd -q -DskipTests compile > ..\logs\%%D-build.log 2>&1
    set BUILD_RC=!errorlevel!
    popd
    if not "!BUILD_RC!"=="0" (
      echo Build failed for %%D. See logs\%%D-build.log
      pause & exit /b 1
    )
  )
)

rem 4. start all (each in its own minimized window)
echo [4/6] Starting the 8 applications ...
for %%S in (%SERVICES%) do (
  for /f "delims=: tokens=1" %%D in ("%%S") do (
    start "%%D" /min cmd /c "cd /d %~dp0%%D && mvnw.cmd -q spring-boot:run > ..\logs\%%D.log 2>&1"
  )
)

rem 5. wait till all ports are open (about 3 minutes at most)
echo [5/6] Waiting for the applications ...
for /l %%i in (1,1,90) do (
  set READY=0
  for %%S in (%SERVICES%) do (
    for /f "delims=: tokens=2" %%P in ("%%S") do (
      netstat -ano | findstr /R /C:":%%P .*LISTENING" >nul && set /a READY+=1
    )
  )
  if "!READY!"=="8" goto :ready
  timeout /t 2 /nobreak >nul
)
:ready

echo.
for %%S in (%SERVICES%) do (
  for /f "delims=: tokens=1,2" %%D in ("%%S") do (
    netstat -ano | findstr /R /C:":%%E .*LISTENING" >nul
    if errorlevel 1 (
      echo   DOWN  %%D ^(port %%E^) -^> see logs\%%D.log
    ) else (
      echo   UP    %%D ^(port %%E^)
    )
  )
)

rem 6. browser
echo.
echo [6/6] Opening http://localhost:8080
start http://localhost:8080
echo To stop everything run stop-all.bat
pause
