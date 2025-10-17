<#
Simple PowerShell helper to build the Docker image and run the container.
Usage:
  .\run-container.ps1            # builds image and runs container with .env values
  .\run-container.ps1 -Compose   # uses docker-compose to build and run backend + mongo
#>
param(
    [switch]$Compose
)

$root = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location $root

if ($Compose) {
    Write-Host "Running with docker-compose (backend + mongo)"
    docker-compose up --build
    exit $LASTEXITCODE
}

# Build image
$imageName = 'lms-backend:local'
Write-Host "Building Docker image: $imageName"
docker build -t $imageName .
if ($LASTEXITCODE -ne 0) { throw "Docker build failed" }

# Load env from .env (optional)
$envFile = Join-Path $root '.env'
if (Test-Path $envFile) {
    Write-Host "Loading environment variables from .env"
    Get-Content $envFile | ForEach-Object {
        if ($_ -and -not $_.StartsWith('#')) {
            $pair = $_ -split '=', 2
            if ($pair.Length -eq 2) { $name = $pair[0].Trim(); $value = $pair[1].Trim(); $env:$name = $value }
        }
    }
}

# Run container
$jwt = $env:JWT_SECRET
if (-not $jwt) { Write-Warning "JWT_SECRET not set in environment or .env; the app may fail if jwt.secret is required." }

Write-Host "Starting container..."
docker run --rm -p 8080:8080 `
    -e JWT_SECRET="$($env:JWT_SECRET)" `
    -e STRIPE_SECRET_KEY="$($env:STRIPE_SECRET_KEY)" `
    -e AZURE_STORAGE_CONNECTION_STRING="$($env:AZURE_STORAGE_CONNECTION_STRING)" `
    -e SPRING_DATA_MONGODB_URI="mongodb://host.docker.internal:27017/lms" `
    --name lms-backend-local $imageName
