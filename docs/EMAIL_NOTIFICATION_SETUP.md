# Email Notification Setup Guide

## Overview
InternVerse includes built-in email notification functionality for sending certificates to interns. The system automatically sends emails when certificates are issued, and admins/mentors can manually resend certificates via API endpoints.

## Features
- **Automatic Certificate Emails**: When a certificate is issued, an email is automatically sent to the intern
- **Manual Resend**: Admins/Mentors can resend certificate emails using API endpoints
- **Custom Messages**: Support for adding custom messages to certificate emails
- **Graceful Fallback**: If email is not configured, notifications are logged to console instead of silently failing

## Configuration

### Step 1: Choose Email Provider

#### Option A: Gmail SMTP (Recommended for Testing)
1. Enable 2-Factor Authentication on your Gmail account
2. Generate an App Password at: https://myaccount.google.com/apppasswords
3. Update `application.yml`:

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true
      mail.smtp.starttls.required: true

internverse:
  mail:
    enabled: true
```

#### Option B: Office 365/Outlook
```yaml
spring:
  mail:
    host: smtp.office365.com
    port: 587
    username: your-email@outlook.com
    password: your-password
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true
```

#### Option C: SendGrid
```yaml
spring:
  mail:
    host: smtp.sendgrid.net
    port: 587
    username: apikey
    password: SG.xxxxxxxxxxxxxx
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true
```

#### Option D: AWS SES
```yaml
spring:
  mail:
    host: email-smtp.region.amazonaws.com
    port: 587
    username: your-ses-username
    password: your-ses-password
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true
```

### Step 2: Enable Email in Configuration
Edit `src/main/resources/application.yml` and set:
```yaml
internverse:
  mail:
    enabled: true
```

### Step 3: Rebuild and Restart
```bash
cd internverse-backend
mvn clean package
mvn spring-boot:run
```

## API Endpoints

### 1. Send Certificate Email (Simple)
Resend a certificate email to an intern by certificate ID.

**Endpoint**: `POST /api/certificates/{id}/send-email`

**Authorization**: ADMIN or MENTOR

**Example**:
```bash
curl -X POST http://localhost:8080/api/certificates/1/send-email \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response**:
```json
{
  "message": "Certificate email sent successfully to the intern"
}
```

### 2. Send Certificate Email (With Custom Message)
Resend a certificate email with a custom message.

**Endpoint**: `POST /api/certificates/send`

**Authorization**: ADMIN or MENTOR

**Request Body**:
```json
{
  "certificateId": 1,
  "customMessage": "Thank you for your outstanding work on this project!"
}
```

**Example**:
```bash
curl -X POST http://localhost:8080/api/certificates/send \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "certificateId": 1,
    "customMessage": "Thank you for your dedication!"
  }'
```

**Response**:
```json
{
  "message": "Certificate email sent successfully"
}
```

## Email Template

When a certificate email is sent, it includes:
- Student's name
- Congratulations message
- Program title (if available)
- Issue date
- Verification code
- Download link to certificate PDF
- Custom message (if provided)
- Professional closing

**Example Email Content**:
```
Hello John Doe,

Congratulations! Your certificate has been issued for successfully completing your internship program.

Program: Web Development Internship
Issue Date: 2026-04-26
Verification Code: a1b2c3d4e5f6g7h8

You can download your certificate here:
http://localhost:8080/api/files/certificates/cert-123-456.pdf

Thank you for your dedication and hard work!
Best regards,
The InternVerse Team
```

## Testing Without Email Configuration

If you haven't configured email yet, the system will:
1. Log notification requests to the console/logs
2. Display: `[email disabled] To: email@example.com | Subject | Message`
3. Allow you to verify the email functionality works without actual SMTP

Example log output:
```
[email disabled] To: intern@example.com | InternVerse: Your Certificate is Ready | Hello John Doe, Congratulations!...
```

## Troubleshooting

### Issue: "Failed to send email"
- **Solution**: Check username and password in `application.yml`
- Verify SMTP host and port are correct
- Check firewall/network access to SMTP server

### Issue: "530 Authentication Failed"
- **Solution**: For Gmail, verify you're using an App Password, not your regular password
- Check that 2FA is enabled
- Generate a new App Password

### Issue: "554 Message rejected"
- **Solution**: Verify `spring.mail.username` matches the email sender identity
- Some providers require the sender email to match the account

### Issue: Email disabled message in logs
- **Solution**: Set `internverse.mail.enabled: true` in `application.yml`
- Verify email configuration is properly filled in

## Environment Variables (Optional)
For production, use environment variables instead of hardcoding credentials:

```yaml
spring:
  mail:
    host: ${MAIL_HOST:smtp.gmail.com}
    port: ${MAIL_PORT:587}
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
```

Then set environment variables:
```bash
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
```

## Automatic Certificate Issuance
Certificates are automatically emailed when:
1. An evaluation is completed marking a submission as "COMPLETED"
2. The CertificateService generates and stores the certificate PDF
3. The EmailNotificationService sends the email to the intern

No additional configuration needed for automatic emails!
