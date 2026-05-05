# ============================================
# 🔧 BaiTapLon - Setup & Test Script
# ============================================
# Chạy script này bằng PowerShell để setup toàn bộ

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "🔧 BaiTapLon App - Setup & Test" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# ===== STEP 1: Check PHP =====
Write-Host "📋 Step 1: Checking PHP..." -ForegroundColor Yellow
$phpVersion = php -v 2>&1 | Select-Object -First 1
if ($phpVersion) {
    Write-Host "✅ PHP found: $phpVersion" -ForegroundColor Green
} else {
    Write-Host "❌ PHP not found. Please install PHP" -ForegroundColor Red
    exit
}

Write-Host ""

# ===== STEP 2: Check MySQL =====
Write-Host "📋 Step 2: Checking MySQL..." -ForegroundColor Yellow
$mysqlVersion = mysql -u root -e "SELECT VERSION();" 2>&1
if ($mysqlVersion -like "*5.*" -or $mysqlVersion -like "*8.*" -or $mysqlVersion -like "8.*") {
    Write-Host "✅ MySQL found" -ForegroundColor Green
} else {
    Write-Host "⚠️  Could not verify MySQL. Continuing anyway..." -ForegroundColor Yellow
}

Write-Host ""

# ===== STEP 3: Create Database =====
Write-Host "📋 Step 3: Creating/Checking Database..." -ForegroundColor Yellow
try {
    # Create database if not exists
    mysql -u root -e "CREATE DATABASE IF NOT EXISTS baitaplon;" 2>$null
    Write-Host "✅ Database baitaplon ready" -ForegroundColor Green

    # Import schema
    Write-Host "   Importing schema from baitaplon.sql..." -ForegroundColor Cyan
    Get-Content api/baitaplon.sql | mysql -u root baitaplon 2>$null
    Write-Host "✅ Schema imported" -ForegroundColor Green
} catch {
    Write-Host "❌ Database error: $_" -ForegroundColor Red
}

Write-Host ""

# ===== STEP 4: Create Admin Account =====
Write-Host "📋 Step 4: Creating Admin Account..." -ForegroundColor Yellow
try {
    # Check if admin exists
    $adminCheck = mysql -u root -e "USE baitaplon; SELECT COUNT(*) FROM users WHERE role='ADMIN';" 2>$null

    if ($adminCheck -eq 0 -or $null -eq $adminCheck) {
        Write-Host "   Admin not found. Creating..." -ForegroundColor Cyan
        Get-Content api/create_admin.sql | mysql -u root baitaplon 2>$null
        Write-Host "✅ Admin account created" -ForegroundColor Green
    } else {
        Write-Host "✅ Admin account already exists" -ForegroundColor Green
    }
} catch {
    Write-Host "⚠️  Could not verify admin: $_" -ForegroundColor Yellow
}

Write-Host ""

# ===== STEP 5: Verify Users Table =====
Write-Host "📋 Step 5: Verifying Users..." -ForegroundColor Yellow
try {
    $users = mysql -u root -e "USE baitaplon; SELECT id, email, role FROM users;" 2>$null
    Write-Host "   Current users in database:" -ForegroundColor Cyan
    Write-Host $users -ForegroundColor Green
} catch {
    Write-Host "⚠️  Could not query users: $_" -ForegroundColor Yellow
}

Write-Host ""

# ===== STEP 6: Start PHP Server =====
Write-Host "📋 Step 6: Starting PHP Server..." -ForegroundColor Yellow
Write-Host "   Server will run at: http://localhost:8080" -ForegroundColor Cyan
Write-Host "   Press Ctrl+C to stop" -ForegroundColor Cyan
Write-Host ""
Write-Host "✅ Starting PHP server..." -ForegroundColor Green
Write-Host ""

# Change to api folder and start server
cd api
php -S localhost:8080


