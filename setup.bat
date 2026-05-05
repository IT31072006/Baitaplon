@echo off
REM ============================================
REM BaiTapLon - Setup & Test Script (For CMD)
REM ============================================

color 0B
cls
echo.
echo ========================================
echo BaiTapLon App - Database Setup
echo ========================================
echo.

REM ===== STEP 1: Check PHP =====
echo [Step 1] Checking PHP...
php -v >nul 2>&1
if errorlevel 1 (
    color 0C
    echo [FAILED] PHP not found. Please install PHP.
    pause
    exit /b 1
) else (
    color 0A
    echo [OK] PHP found
    php -v | findstr /R "^PHP"
)
color 0B

echo.
echo [Step 2] Checking MySQL...
mysql -u root -e "SELECT VERSION();" >nul 2>&1
if errorlevel 1 (
    echo [WARNING] MySQL might not be running
) else (
    color 0A
    echo [OK] MySQL connection successful
    color 0B
)

echo.
echo [Step 3] Creating/Checking Database...
mysql -u root -e "CREATE DATABASE IF NOT EXISTS baitaplon;" 2>nul
if errorlevel 1 (
    color 0C
    echo [FAILED] Could not create database
    echo Please ensure MySQL is running and user 'root' has no password
    pause
    exit /b 1
)
color 0A
echo [OK] Database created/exists

echo.
echo [Step 3a] Importing schema...
mysql -u root baitaplon < api\baitaplon.sql 2>nul
if errorlevel 1 (
    color 0C
    echo [WARNING] Schema import may have failed
    color 0B
) else (
    color 0A
    echo [OK] Schema imported
    color 0B
)

echo.
echo [Step 4] Creating Admin Account...
for /f "delims=" %%i in ('mysql -u root -e "USE baitaplon; SELECT COUNT(*) FROM users WHERE role='ADMIN';" 2^>nul') do set ADMIN_COUNT=%%i

if "%ADMIN_COUNT%"=="0" (
    echo [Info] Admin not found. Creating...
    mysql -u root baitaplon < api\create_admin.sql 2>nul
    if errorlevel 1 (
        color 0C
        echo [WARNING] Could not create admin account
        color 0B
    ) else (
        color 0A
        echo [OK] Admin account created
        color 0B
    )
) else (
    color 0A
    echo [OK] Admin account already exists
    color 0B
)

echo.
echo [Step 5] Verifying Users...
echo.
mysql -u root -e "USE baitaplon; SELECT id, email, role FROM users;" 2>nul
echo.

echo.
echo ========================================
echo [Step 6] Starting PHP Server
echo ========================================
echo Server will run at: http://localhost:8080
echo Press Ctrl+C to stop
echo.
color 0A

cd api
php -S localhost:8080

cd ..
pause


