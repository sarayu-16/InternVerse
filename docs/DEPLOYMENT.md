# Deployment notes

## Backend (JAR)

1. Set environment-specific MySQL URL, username, and password (or use Spring Cloud / secrets manager).
2. Set a strong `internverse.jwt.secret` (long random string; the app hashes it with SHA-256 for HMAC).
3. Set `internverse.public-base-url` to your public API URL so certificate links in PDFs and emails are correct.
4. Configure `internverse.upload-dir` on persistent storage (e.g. `/var/internverse/uploads`).
5. Build: `mvn -f internverse-backend clean package -DskipTests`
6. Run: `java -jar internverse-backend/target/internverse-backend-1.0.0.jar`

### Email

1. Remove `MailSenderAutoConfiguration` exclusion in `InternVerseApplication.java` **or** switch to explicit mail configuration beans.
2. Add `spring.mail.*` properties (SMTP host, port, credentials).
3. Set `internverse.mail.enabled=true`.

## Frontend (static hosting / CDN)

1. Build: `cd internverse-frontend && npm ci && npm run build`
2. Deploy `dist/` to Netlify, Vercel, S3+CloudFront, Nginx, etc.
3. Set `VITE_API_URL` to your public API origin at build time, e.g. `https://api.yourdomain.com`

Example:

```bash
set VITE_API_URL=https://api.yourdomain.com
npm run build
```

## Reverse proxy (Nginx)

- Proxy `/api` to the Spring Boot service.
- Alternatively, serve the SPA on `/` and keep API on a subdomain; ensure CORS on the backend matches the SPA origin.

## TLS

Terminate TLS at the reverse proxy or load balancer; use HTTPS for JWT and certificate links in production.
