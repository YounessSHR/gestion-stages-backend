# Setup Instructions

## Initial Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd gestion-stages/backend
```

### 2. Create Configuration File
```bash
# Copy the example file
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

### 3. Configure Application Properties

Edit `src/main/resources/application.properties` and fill in:

- **Database Password**: Replace `YOUR_DATABASE_PASSWORD_HERE` with your MySQL password
- **JWT Secret**: Generate a strong random secret (see below)
- **Email Credentials**: Replace with your Gmail credentials

### 4. Generate JWT Secret

**Option 1: Using OpenSSL**
```bash
openssl rand -base64 64
```

**Option 2: Using PowerShell (Windows)**
```powershell
[Convert]::ToBase64String((1..64 | ForEach-Object {Get-Random -Max 256}))
```

**Option 3: Online Generator**
Visit: https://www.allkeysgenerator.com/Random/Security-Encryption-Key-Generator.aspx
- Select "256-bit" or higher
- Copy the generated key

### 5. Create Upload Directories
```bash
mkdir -p uploads/cv uploads/conventions
```

### 6. Run the Application
```bash
./mvnw spring-boot:run
```

## Important Notes

- ⚠️ **NEVER commit `application.properties`** - it's in `.gitignore`
- ✅ **DO commit `application.properties.example`** - it's a template
- 🔒 Keep your secrets secure and never share them

## Troubleshooting

### Application.properties is tracked by Git

If `application.properties` was already committed before adding it to `.gitignore`:

```bash
# Remove from git tracking (but keep the file locally)
git rm --cached src/main/resources/application.properties

# Commit the removal
git commit -m "chore: Remove application.properties from version control"

# Now it will be ignored in future commits
```

### Database Connection Issues

- Verify MySQL is running (XAMPP Control Panel)
- Check database name matches: `gestion_stages`
- Verify username/password in `application.properties`

### JWT Token Issues

- Ensure JWT secret is at least 64 characters long
- If you change the secret, all existing tokens become invalid (users need to login again)

