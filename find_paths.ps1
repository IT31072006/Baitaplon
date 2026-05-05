# Tìm PHP & MySQL installation paths
# Chạy các lệnh này để tìm nơi PHP & MySQL được cài đặt

# ===== TÌM PHP =====
Write-Host "Searching for PHP installation..." -ForegroundColor Yellow

$phpPaths = @(
    "C:\xampp\php",
    "C:\wamp\bin\php",
    "C:\wamp64\bin\php",
    "C:\laragon\bin\php",
    "C:\laragon\bin\php\php-*",
    "C:\Program Files\PHP",
    "C:\Program Files (x86)\PHP"
)

foreach ($path in $phpPaths) {
    if (Test-Path $path) {
        Write-Host "✅ Found PHP at: $path" -ForegroundColor Green
    }
}

# TÌM php.exe
Write-Host "`nSearching for php.exe..." -ForegroundColor Yellow
$php = Get-ChildItem -Path "C:\" -Recurse -Include "php.exe" -ErrorAction SilentlyContinue | Select-Object -First 5
if ($php) {
    Write-Host "✅ Found php.exe:" -ForegroundColor Green
    $php | ForEach-Object { Write-Host $_.FullName }
} else {
    Write-Host "❌ php.exe not found" -ForegroundColor Red
}

# ===== TÌM MYSQL =====
Write-Host "`nSearching for MySQL installation..." -ForegroundColor Yellow

$mysqlPaths = @(
    "C:\xampp\mysql",
    "C:\xampp\mariadb",
    "C:\wamp\bin\mysql",
    "C:\wamp64\bin\mysql",
    "C:\wamp64\bin\mariadb",
    "C:\laragon\bin\mysql",
    "C:\laragon\bin\mariadb",
    "C:\Program Files\MySQL",
    "C:\Program Files (x86)\MySQL"
)

foreach ($path in $mysqlPaths) {
    if (Test-Path $path) {
        Write-Host "✅ Found MySQL at: $path" -ForegroundColor Green
    }
}

# TÌM mysql.exe
Write-Host "`nSearching for mysql.exe..." -ForegroundColor Yellow
$mysql = Get-ChildItem -Path "C:\" -Recurse -Include "mysql.exe" -ErrorAction SilentlyContinue | Select-Object -First 5
if ($mysql) {
    Write-Host "✅ Found mysql.exe:" -ForegroundColor Green
    $mysql | ForEach-Object { Write-Host $_.FullName }
} else {
    Write-Host "❌ mysql.exe not found" -ForegroundColor Red
}

Write-Host "`n" -ForegroundColor Cyan
Write-Host "📝 Copy the paths above and update your PATH environment variable" -ForegroundColor Cyan
Write-Host "   OR use them in the setup script" -ForegroundColor Cyan

