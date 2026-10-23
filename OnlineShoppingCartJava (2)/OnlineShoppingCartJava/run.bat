@echo off
title Cartly Online Shopping Cart
echo ==========================================
echo   Cartly - Online Shopping Cart Website
echo ==========================================
echo.
where java >nul 2>nul
if errorlevel 1 (
  echo Java was not found. Install JDK 17 or newer first.
  pause
  exit /b 1
)
where mvn >nul 2>nul
if errorlevel 1 (
  echo Maven was not found. Install Apache Maven and add it to PATH.
  pause
  exit /b 1
)
echo Starting Java backend...
start "Cartly backend" cmd /k "cd /d %~dp0 && mvn spring-boot:run"
echo Waiting for the server to start...
timeout /t 18 /nobreak >nul
where chrome >nul 2>nul
if not errorlevel 1 (
  start "" chrome http://localhost:8080
) else (
  if exist "%ProgramFiles%\Google\Chrome\Application\chrome.exe" (
    start "" "%ProgramFiles%\Google\Chrome\Application\chrome.exe" http://localhost:8080
  ) else if exist "%ProgramFiles(x86)%\Google\Chrome\Application\chrome.exe" (
    start "" "%ProgramFiles(x86)%\Google\Chrome\Application\chrome.exe" http://localhost:8080
  ) else (
    echo Chrome was not found automatically. Opening your default browser.
    start "" http://localhost:8080
  )
)
echo If the page does not load yet, wait a few seconds and refresh.
pause
