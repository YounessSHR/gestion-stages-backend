# ⚠️ Remove Sensitive Files from Git History

## Current Situation

The file `application.properties` is currently tracked by Git and contains sensitive information:
- JWT secret key
- Database password
- Email credentials

## Action Required

### Step 1: Remove from Git Tracking

```bash
# Remove from git tracking (keeps the file locally)
git rm --cached src/main/resources/application.properties

# Commit the removal
git commit -m "chore: Remove application.properties from version control for security"
```

### Step 2: Verify .gitignore

The `.gitignore` file should already contain:
```
src/main/resources/application.properties
```

If not, add it.

### Step 3: Create Your Local Configuration

```bash
# Copy the example file
cp src/main/resources/application.properties.example src/main/resources/application.properties

# Edit with your actual values
# (Use your preferred editor)
```

### Step 4: Generate New Secrets

**IMPORTANT**: Since the old secrets were exposed, you MUST:

1. **Generate a new JWT secret:**
   ```bash
   openssl rand -base64 64
   ```
   Or use: https://www.allkeysgenerator.com/Random/Security-Encryption-Key-Generator.aspx

2. **Update in `application.properties`:**
   ```properties
   jwt.secret=YOUR_NEW_STRONG_SECRET_HERE
   ```

3. **Change database password** (if it was exposed)

4. **Change email credentials** (if they were real)

### Step 5: Force All Users to Re-login

Since you're changing the JWT secret, all existing tokens will become invalid. Users will need to login again.

### Optional: Remove from Git History (Advanced)

If you want to completely remove the file from Git history (recommended for production):

```bash
# WARNING: This rewrites Git history!
# Only do this if you haven't pushed to a shared repository yet
# Or coordinate with your team first

git filter-branch --force --index-filter \
  "git rm --cached --ignore-unmatch src/main/resources/application.properties" \
  --prune-empty --tag-name-filter cat -- --all

# Force push (ONLY if working alone or with team coordination)
# git push origin --force --all
```

**⚠️ WARNING**: Rewriting Git history can cause issues for other team members. Only do this if:
- You're working alone, OR
- You've coordinated with your team, OR
- The repository hasn't been shared yet

## After Removal

✅ `application.properties` will be ignored in future commits
✅ `application.properties.example` will be committed (template only)
✅ Your local `application.properties` will remain (not tracked)

## Verification

```bash
# Check if file is ignored
git status

# Should NOT show application.properties
```

