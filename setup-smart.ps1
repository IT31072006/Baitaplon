# ============================================
# 🔧 BaiTapLon - Smart Setup Script
# ============================================
# Auto-detect PHP & MySQL paths

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "🔧 BaiTapLon App - Smart Setup" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# ===== AUTO-DETECT PHP =====
Write-Host "🔍 Detecting PHP installation..." -ForegroundColor Yellow

$phpExe = $null

# Common PHP paths
$commonPhpPaths = @(
    "C:\xampp\php\php.exe",
    "C:\wamp\bin\php\php*.exe",
    "C:\wamp64\bin\php\php*.exe",
    "C:\laragon\bin\php\php.exe",
    "C:\Program Files\PHP\php.exe"
)

foreach ($path in $commonPhpPaths) {
    $found = Get-Item $path -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($found) {
        $phpExe = $found.FullName
        Write-Host "✅ PHP found at: $phpExe" -ForegroundColor Green
        break
    }
}

if ($null -eq $phpExe) {
    Write-Host "❌ PHP not found in common locations" -ForegroundColor Red
    Write-Host "Please run 'find_paths.ps1' to locate PHP, then update line 20 of setup-smart.ps1" -ForegroundColor Yellow
    exit
}

# ===== AUTO-DETECT MYSQL =====
Write-Host "`n🔍 Detecting MySQL installation..." -ForegroundColor Yellow

$mysqlExe = $null

# Common MySQL paths
$commonMysqlPaths = @(
    "C:\xampp\mysql\bin\mysql.exe",
    "C:\xampp\mariadb\bin\mysql.exe",
    "C:\wamp\bin\mysql\mysql*.exe",
    "C:\wamp64\bin\mysql\mysql*.exe",
    "C:\wamp64\bin\mariadb\bin\mysql.exe",
    "C:\laragon\bin\mysql\bin\mysql.exe",
    "C:\laragon\bin\mariadb\bin\mysql.exe",
    "C:\Program Files\MySQL\MySQL Server*\bin\mysql.exe"
)

foreach ($path in $commonMysqlPaths) {
    $found = Get-Item $path -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($found) {
        $mysqlExe = $found.FullName
        Write-Host "✅ MySQL found at: $mysqlExe" -ForegroundColor Green
        break
    }
}

if ($null -eq $mysqlExe) {
    Write-Host "❌ MySQL not found in common locations" -ForegroundColor Red
    Write-Host "Please run 'find_paths.ps1' to locate MySQL, then update line 38 of setup-smart.ps1" -ForegroundColor Yellow
    exit
}

# ===== SETUP DATABASE =====
Write-Host "`n📋 Creating/Checking Database..." -ForegroundColor Yellow

try {
    # Create database
    & $mysqlExe -u root -e "CREATE DATABASE IF NOT EXISTS baitaplon;" 2>$null
    Write-Host "✅ Database ready" -ForegroundColor Green

    # Import schema
    Write-Host "   Importing schema..." -ForegroundColor Cyan
    $schemaPath = "$PSScriptRoot\api\baitaplon.sql"
    Get-Content $schemaPath | & $mysqlExe -u root baitaplon 2>$null
    Write-Host "✅ Schema imported" -ForegroundColor Green
} catch {
    Write-Host "❌ Database error: $_" -ForegroundColor Red
    exit
}

# ===== CREATE ADMIN =====
Write-Host "`n📋 Creating Admin Account..." -ForegroundColor Yellow

try {
    $adminSql = "USE baitaplon; INSERT IGNORE INTO users (email, password, role, full_name) VALUES ('admin@restaurant.com', '\$2y\$10\$Qi3UKPVJLaXsHNYCEPNNXONEu2eE70F4F/2RKCLTdpJKRsZSuP/i.', 'ADMIN', 'Admin Nhà Hàng');"

    & $mysqlExe -u root -e $adminSql 2>$null
    Write-Host "✅ Admin account ready" -ForegroundColor Green
} catch {
    Write-Host "⚠️  Admin creation warning: $_" -ForegroundColor Yellow
}

# ===== VERIFY =====
Write-Host "`n📋 Verifying Users..." -ForegroundColor Yellow
& $mysqlExe -u root -e "USE baitaplon; SELECT id, email, role FROM users;" 2>$null

# ===== START PHP =====
Write-Host "`n✅ Setup complete!" -ForegroundColor Green
Write-Host "`n📋 Starting PHP Server at http://localhost:8080..." -ForegroundColor Yellow
Write-Host "Press Ctrl+C to stop`n" -ForegroundColor Cyan

Push-Location "$PSScriptRoot\api"
& $phpExe -S localhost:8080
Pop-Location

