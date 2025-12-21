# Backend Health Check Script
Write-Host "Testing FileCreator Backend Connection..." -ForegroundColor Green

# Test 1: Check if server is responding
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/health" -Method GET
    Write-Host "✓ Backend Health Check: $response" -ForegroundColor Green
} catch {
    Write-Host "✗ Backend Health Check Failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Make sure Spring Boot application is running with: mvn spring-boot:run" -ForegroundColor Yellow
    exit 1
}

# Test 2: Check if file upload endpoint exists
try {
    # This will fail but should give us a 400 (bad request) not 404 (not found)
    Invoke-RestMethod -Uri "http://localhost:8080/api/upload" -Method POST -ErrorAction Stop
} catch {
    if ($_.Exception.Response.StatusCode -eq "BadRequest") {
        Write-Host "✓ Upload endpoint exists (returned 400 as expected without file)" -ForegroundColor Green
    } elseif ($_.Exception.Response.StatusCode -eq "NotFound") {
        Write-Host "✗ Upload endpoint not found (404)" -ForegroundColor Red
    } else {
        Write-Host "✓ Upload endpoint responding (status: $($_.Exception.Response.StatusCode))" -ForegroundColor Green
    }
}

# Test 3: Test with actual file upload (if Rough_csv.csv exists)
if (Test-Path "Rough_csv.csv") {
    Write-Host "Testing file upload with Rough_csv.csv..." -ForegroundColor Yellow
    try {
        $fileBytes = [System.IO.File]::ReadAllBytes("Rough_csv.csv")
        $boundary = [System.Guid]::NewGuid().ToString()
        $bodyLines = @(
            "--$boundary",
            'Content-Disposition: form-data; name="file"; filename="Rough_csv.csv"',
            'Content-Type: text/csv',
            '',
            [System.Text.Encoding]::UTF8.GetString($fileBytes),
            "--$boundary--"
        )
        $body = $bodyLines -join "`r`n"
        
        $response = Invoke-RestMethod -Uri "http://localhost:8080/api/upload" -Method POST -Body $body -ContentType "multipart/form-data; boundary=$boundary"
        Write-Host "✓ File upload test successful" -ForegroundColor Green
    } catch {
        Write-Host "✗ File upload test failed: $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "⚠ Rough_csv.csv not found, skipping file upload test" -ForegroundColor Yellow
}

Write-Host "`nBackend testing complete!" -ForegroundColor Green