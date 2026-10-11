@echo off
rem stops everything running on ports 8080 to 8087 (Windows)
for %%P in (8080 8081 8082 8083 8084 8085 8086 8087) do (
  for /f "tokens=5" %%I in ('netstat -ano ^| findstr /R /C:":%%P .*LISTENING"') do (
    taskkill /PID %%I /F /T >nul 2>&1
    echo Stopped port %%P
  )
)
