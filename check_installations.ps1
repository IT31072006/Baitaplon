# 🔍 Kiểm tra PHP & MySQL Installation (Optimized)

Write-Host "Checking installations..." -ForegroundColor Cyan
Write-Host ""

# ===== Check PHP =====
Write-Host "1️⃣ Checking PHP..." -ForegroundColor Yellow

$phpPaths = @(
    "C:\xampp\php\php.exe",
    "C:\wamp\bin\php\php*.exe",
    "C:\wamp64\bin\php\php*.exe",
    "C:\laragon\bin\php\php.exe",
    "C:\Program Files\PHP\php.exe",
    "C:\Program Files (x86)\PHP\php.exe"
)

$php = $null
foreach ($path in $phpPaths) {
    $found = Get-Item $path -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($found) {
        $php = $found
        break
    }
}

if ($php) {
    Write-Host "✅ PHP found!" -ForegroundColor Green
    Write-Host "   Path: $($php.FullName)"
    Write-Host ""

    # Test PHP
    Write-Host "   Testing PHP..." -ForegroundColor Cyan
    & $php.FullName -v
    Write-Host ""
} else {
    Write-Host "❌ PHP not found" -ForegroundColor Red
    Write-Host "   Checked locations:" -ForegroundColor Yellow
    $phpPaths | ForEach-Object { Write-Host "      $_" -ForegroundColor Gray }
    Write-Host "   Install from: https://windows.php.net/download/" -ForegroundColor Yellow
    Write-Host ""
}

# ===== Check MySQL =====
Write-Host "2️⃣ Checking MySQL..." -ForegroundColor Yellow

$mysqlPaths = @(
    "C:\xampp\mysql\bin\mysql.exe",
    "C:\xampp\mariadb\bin\mysql.exe",
    "C:\wamp\bin\mysql\mysql*.exe",
    "C:\wamp64\bin\mysql\mysql*.exe",
    "C:\wamp64\bin\mariadb\bin\mysql.exe",
    "C:\laragon\bin\mysql\bin\mysql.exe",
    "C:\laragon\bin\mariadb\bin\mysql.exe",
    "C:\Program Files\MySQL\MySQL Server*\bin\mysql.exe"
)

$mysql = $null
foreach ($path in $mysqlPaths) {
    $found = Get-Item $path -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($found) {
        $mysql = $found
        break
    }
}

if ($mysql) {
    Write-Host "✅ MySQL found!" -ForegroundColor Green
    Write-Host "   Path: $($mysql.FullName)"
    Write-Host ""

    # Test MySQL
    Write-Host "   Testing MySQL connection..." -ForegroundColor Cyan
    & $mysql.FullName -u root -e "SELECT VERSION();" 2>$null | Write-Host -ForegroundColor Green
    Write-Host ""
} else {
    Write-Host "❌ MySQL not found" -ForegroundColor Red
    Write-Host "   Checked locations:" -ForegroundColor Yellow
    $mysqlPaths | ForEach-Object { Write-Host "      $_" -ForegroundColor Gray }
    Write-Host "   Install from: https://dev.mysql.com/downloads/mysql/" -ForegroundColor Yellow
    Write-Host ""
}

# Summary
Write-Host "=" -ForegroundColor Cyan -NoNewline
Write-Host "=" -ForegroundColor Cyan -NoNewline
Write-Host "=" -ForegroundColor Cyan -NoNewline
Write-Host "=" -ForegroundColor Cyan -NoNewline
Write-Host "=" -ForegroundColor Cyan -NoNewline
Write-Host "=" -ForegroundColor Cyan -NoNewline
Write-Host "=" -ForegroundColor Cyan

if ($php -and $mysql) {
    Write-Host "✅ Both PHP and MySQL are installed!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Ready to run: .\setup-smart.ps1" -ForegroundColor Green
} else {
    Write-Host "❌ Some components missing" -ForegroundColor Red
    if (-not $php) { Write-Host "   - Install PHP" -ForegroundColor Yellow }
    if (-not $mysql) { Write-Host "   - Install MySQL" -ForegroundColor Yellow }
}

Write-Host ""

