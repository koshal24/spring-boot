<#
PowerShell script to run the Postman collection requests against a local LMS backend.
- Uses Invoke-RestMethod (PowerShell) rather than curl to parse JSON responses easily.
- Saves returned IDs (courseId, quizId, purchaseId, subscriptionId, userId, jwt) to variables for reuse.

Usage:
  Open PowerShell in the repo root and run:
    .\tools\postman\run_collection.ps1

Adjust $baseUrl and sample payloads below as needed.
#>

$ErrorActionPreference = 'Stop'

# Configuration
$baseUrl = 'http://localhost:8080'

# Variables that will be populated
$global:jwt = ''
$global:courseId = ''
$global:quizId = ''
$global:purchaseId = ''
$global:subscriptionId = ''
$global:userId = ''
$global:educatorId = ''

function Send-Request {
    param(
        [Parameter(Mandatory=$true)][ValidateSet('GET','POST','PUT','DELETE')][string]$Method,
        [Parameter(Mandatory=$true)][string]$Path,
        [Object]$Body = $null,
        [hashtable]$Query = $null,
        [hashtable]$Headers = $null
    )

    $uri = "$baseUrl$Path"
    if ($Query) {
        $q = $Query.GetEnumerator() | ForEach-Object { "{0}={1}" -f $_.Key, [uri]::EscapeDataString($_.Value) } -join '&'
        $uri = "$uri`?$q"
    }

    if (-not $Headers) { $Headers = @{} }
    if ($global:jwt -and -not $Headers.ContainsKey('Authorization')) {
        $Headers['Authorization'] = "Bearer $($global:jwt)"
    }

    Write-Host "--> $Method $uri" -ForegroundColor Cyan
    try {
        switch ($Method) {
            'GET'    { $resp = Invoke-RestMethod -Method Get -Uri $uri -Headers $Headers -ContentType 'application/json' -ErrorAction Stop }
            'POST'   {
                if ($Body -ne $null) { $json = $Body | ConvertTo-Json -Depth 10 } else { $json = '' }
                $resp = Invoke-RestMethod -Method Post -Uri $uri -Headers $Headers -ContentType 'application/json' -Body $json -ErrorAction Stop
            }
            'PUT'    {
                if ($Body -ne $null) { $json = $Body | ConvertTo-Json -Depth 10 } else { $json = '' }
                $resp = Invoke-RestMethod -Method Put -Uri $uri -Headers $Headers -ContentType 'application/json' -Body $json -ErrorAction Stop
            }
            'DELETE' { $resp = Invoke-RestMethod -Method Delete -Uri $uri -Headers $Headers -ContentType 'application/json' -ErrorAction Stop }
        }
        Write-Host "<- Success" -ForegroundColor Green
        return $resp
    } catch {
        Write-Host "<- ERROR: $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
}

# 1) Health check
$health = Send-Request -Method GET -Path '/api/health'
if ($health) { Write-Host "Health: $($health | ConvertTo-Json -Depth 2)" }

# 2) Register (example user)
$registerBody = @{ name = 'Alice'; email = 'alice+local@example.com'; password = 'password123'; role = 'STUDENT' }
$reg = Send-Request -Method POST -Path '/api/auth/register' -Body $registerBody
if ($reg -ne $null) { Write-Host "Registered user: $($reg | ConvertTo-Json -Depth 2)" }

# 3) Login -> saves jwt
$loginBody = @{ email = 'alice+local@example.com'; password = 'password123' }
$login = Send-Request -Method POST -Path '/api/auth/login' -Body $loginBody
if ($login -and $login.token) {
    $global:jwt = $login.token
    Write-Host "Saved jwt (length) = $($global:jwt.Length)"
} else {
    Write-Host "Login did not return token; aborting further auth-required requests" -ForegroundColor Yellow
}

# 4) Create a Course (uses auth header)
$courseBody = @{ title = 'Intro to Postman (PS)'; description = 'Created by run_collection.ps1'; educatorId = 'educator123'; enrolledUserIds = @(); price = 0.0; isPaid = $false }
$course = Send-Request -Method POST -Path '/api/courses' -Body $courseBody
if ($course -and $course.id) { $global:courseId = $course.id; Write-Host "Saved courseId = $courseId" }

# 5) Create a Quiz
$quizBody = @{
    courseId = ($global:courseId -ne '' ? $global:courseId : 'course123');
    title = 'Sample Quiz via PS';
    published = $false;
    questions = @(
        @{ id = 'q1'; text = 'What is 2+2?'; options = @('1','2','3','4'); correctOptionIndex = 3 }
    )
}
$quiz = Send-Request -Method POST -Path '/api/quizzes' -Body $quizBody
if ($quiz -and $quiz.id) { $global:quizId = $quiz.id; Write-Host "Saved quizId = $quizId" }

# 6) Create a Purchase
$purchaseBody = @{ userId = 'user123'; courseId = ($global:courseId -ne '' ? $global:courseId : 'course123'); purchaseDate = (Get-Date).ToString('s'); amount = 49.99 }
$purchase = Send-Request -Method POST -Path '/api/purchases' -Body $purchaseBody
if ($purchase -and $purchase.id) { $global:purchaseId = $purchase.id; Write-Host "Saved purchaseId = $purchaseId" }

# 7) Create a Subscription
$subscriptionBody = @{ userId = 'user123'; planId = 'plan_basic'; startDate = (Get-Date).ToString('s'); endDate = (Get-Date).AddYears(1).ToString('s'); active = $true }
$sub = Send-Request -Method POST -Path '/api/subscriptions' -Body $subscriptionBody
if ($sub -and $sub.id) { $global:subscriptionId = $sub.id; Write-Host "Saved subscriptionId = $subscriptionId" }

# 8) Educator endpoints (upload course, get my courses, enrollments) - educatorId variable
$global:educatorId = 'educator123'
$eduCourseBody = @{ title = 'Educator Uploaded Course'; description = 'Uploaded by educator via script'; price = 0.0; isPaid = $false }
$uploaded = Send-Request -Method POST -Path '/api/educator/courses' -Query @{ educatorId = $global:educatorId } -Body $eduCourseBody
if ($uploaded -and $uploaded.id) { Write-Host "Uploaded course id = $($uploaded.id)"; if (-not $global:courseId) { $global:courseId = $uploaded.id } }

$myCourses = Send-Request -Method GET -Path '/api/educator/courses' -Query @{ educatorId = $global:educatorId }
if ($myCourses) { Write-Host "My courses count: $($myCourses.Count)" }

$enrollments = Send-Request -Method GET -Path '/api/educator/courses/enrollments' -Query @{ educatorId = $global:educatorId }
if ($enrollments) { Write-Host "Enrollments: $($enrollments | ConvertTo-Json -Depth 3)" }

# 9) Users endpoints
# Create user (alternative to register)
$userBody = @{ name = 'Bob'; email = 'bob+local@example.com'; password = 'pass123'; role = 'STUDENT' }
$user = Send-Request -Method POST -Path '/api/users' -Body $userBody
if ($user -and $user.id) { $global:userId = $user.id; Write-Host "Saved userId = $userId" }

$usersList = Send-Request -Method GET -Path '/api/users'
if ($usersList) { Write-Host "Total users: $($usersList.Count)" }

# 10) Fetch lists and individual resources to verify
$allCourses = Send-Request -Method GET -Path '/api/courses'
$courseById = if ($global:courseId) { Send-Request -Method GET -Path "/api/courses/$($global:courseId)" }
$allPurchases = Send-Request -Method GET -Path '/api/purchases'
$allSubs = Send-Request -Method GET -Path '/api/subscriptions'
$quizzesForCourse = if ($global:courseId) { Send-Request -Method GET -Path "/api/quizzes/course/$($global:courseId)" }
$quizById = if ($global:quizId) { Send-Request -Method GET -Path "/api/quizzes/$($global:quizId)" }

# Done — print summary
Write-Host "\n--- Summary ---" -ForegroundColor Magenta
Write-Host "baseUrl: $baseUrl"
Write-Host "jwt: $([string]::Concat($global:jwt.Substring(0, [Math]::Min(10,$global:jwt.Length)), '...'))" -ForegroundColor Yellow
Write-Host "courseId: $global:courseId"
Write-Host "quizId: $global:quizId"
Write-Host "purchaseId: $global:purchaseId"
Write-Host "subscriptionId: $global:subscriptionId"
Write-Host "userId: $global:userId"
Write-Host "educatorId: $global:educatorId"

Write-Host "\nScript finished. You can now use the printed IDs in Postman or run additional scripted requests." -ForegroundColor Green
