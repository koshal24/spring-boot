# Create initial ADMIN user

This document shows a few simple ways to create an initial `ADMIN` user for the LMS backend.

Important: registration does NOT allow assigning the `ADMIN` role. Use one of the steps below to create the first admin.

1) Generate a bcrypt password hash

Choose any of the options below to produce a bcrypt hash of the password you want to use. Replace `<BCRYPT_HASH>` in the Mongo command below with the generated hash.

- Option A — Node.js (recommended if you have node/npm installed):

```powershell
# install bcrypt globally once (or run in a project)
npm install -g bcrypt
# generate hash (wait for it to print)
node -e "const bcrypt=require('bcrypt'); bcrypt.hash('AdminPassword123',10,(e,h)=>console.log(h));"
```

- Option B — Python (requires `bcrypt` package):

```powershell
pip install bcrypt
python - <<'PY'
import bcrypt
print(bcrypt.hashpw(b'AdminPassword123', bcrypt.gensalt()).decode())
PY
```

- Option C — Use an online bcrypt generator (only for development) or any other tool you trust.

2) Insert admin record into MongoDB

Open `mongosh` or MongoDB Compass and run the insert (replace DB name if required and replace `<BCRYPT_HASH>`):

```javascript
use lms
db.users.insertOne({
  name: 'Initial Admin',
  email: 'admin@example.com',
  password: '<BCRYPT_HASH>',
  role: 'ADMIN',
  verified: true,
  uploadedCourses: [],
  purchasedCourses: []
});
```

Notes:
- `verified: true` allows logging in immediately.
- The collection name is `users` (matches the `User` model annotation `@Document(collection = "users")`). Adjust the DB name if your app uses a different database.

3) Verify by logging in

Use the login endpoint to obtain a JWT:

```powershell
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"email":"admin@example.com","password":"AdminPassword123"}'
```

You should receive a JSON response containing `token` and `role: "ADMIN"`.

4) (Optional) Promote an existing user to ADMIN via API

If another admin already exists, you can use the admin endpoint (requires admin JWT):

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/admin/users/<userId>/role?role=ADMIN" -Headers @{ Authorization = "Bearer <admin_jwt>" }
```

Security recommendation
- Use this approach only for initial bootstrap. After you've created the initial admin, remove or securely store any bootstrap secrets.
- Do NOT expose a public registration option that allows assigning `ADMIN`.

If you want, I can also add a small helper script under `tools/scripts` that uses Node.js to generate a bcrypt hash and prints a ready-to-run `mongosh` command. Tell me if you'd like that and whether Node is available on your machine.
# Create initial admin

Two safe options to create the initial `ADMIN` user:

1) Direct MongoDB insert (developer / one-time):

Use the mongo shell or `mongosh` and run the following (replace passwordHash with a bcrypt hash):

```js
db.users.insertOne({
  name: 'Admin',
  email: 'admin@example.com',
  password: '<bcrypt-hash-of-password>',
  role: 'ADMIN',
  verified: true
});
```

You can generate a bcrypt hash using a small Node script, Java utility, or by temporarily creating a user through the app and reading the hash from the DB.

2) Use the admin promotion endpoint (recommended when you already have one admin):

If you already have one admin, call the admin API to promote another user:

PowerShell example (replace values):

```powershell
$adminJwt = 'ey...'
$userId = '...'
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/admin/users/$userId/role?role=ADMIN" -Headers @{ Authorization = "Bearer $adminJwt" }
```

Notes:
- After creating the initial admin, remove any one-time secrets from configuration.
- Protect creation steps and don't expose them in public CI logs.
