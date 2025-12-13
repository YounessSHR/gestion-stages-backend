# Security Guidelines

## ⚠️ Important Security Information

### Configuration Files

**NEVER commit sensitive information to the repository!**

The `application.properties` file contains sensitive data:
- JWT secret key
- Database passwords
- Email credentials
- API keys

### Setup Instructions

1. **Copy the example file:**
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

2. **Fill in your actual values:**
   - Replace `YOUR_DATABASE_PASSWORD_HERE` with your MySQL password
   - Replace `YOUR_STRONG_RANDOM_SECRET_KEY_HERE...` with a strong random secret (minimum 64 characters)
   - Replace `YOUR_EMAIL@gmail.com` and `YOUR_APP_PASSWORD_HERE` with your email credentials

3. **Generate a strong JWT secret:**
   ```bash
   # Using OpenSSL
   openssl rand -base64 64
   
   # Or use an online generator:
   # https://www.allkeysgenerator.com/Random/Security-Encryption-Key-Generator.aspx
   ```

### Environment Variables (Recommended for Production)

For production environments, use environment variables instead of hardcoding values:

```bash
export JWT_SECRET=your_secret_here
export DB_PASSWORD=your_db_password
export EMAIL_PASSWORD=your_email_password
```

Then update `application.properties` to use environment variables:
```properties
jwt.secret=${JWT_SECRET}
spring.datasource.password=${DB_PASSWORD}
spring.mail.password=${EMAIL_PASSWORD}
```

### What is JWT Secret?

The JWT secret is used to:
- **Sign** JWT tokens when they are created (login)
- **Verify** JWT tokens when they are received (authenticated requests)

**Why it's dangerous if exposed:**
- Anyone with the secret can create valid tokens
- Attackers can impersonate any user
- Complete authentication bypass

**Best practices:**
- Use a strong random secret (minimum 64 characters)
- Never commit it to version control
- Use different secrets for development and production
- Rotate secrets periodically

### Current Status

✅ `.gitignore` is configured to exclude `application.properties`
✅ `application.properties.example` is provided as a template
⚠️ If you already committed `application.properties`, you should:
   1. Remove it from git history (if possible)
   2. Change all secrets immediately
   3. Regenerate JWT tokens for all users

