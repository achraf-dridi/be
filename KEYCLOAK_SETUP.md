# API Communication Training - Backend

## Keycloak Setup Instructions

### 1. Start Keycloak (Docker)
```bash
docker run -p 8080:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:latest start-dev
```

### 2. Configure Keycloak
1. Access Keycloak Admin Console: http://localhost:8080
2. Login with `admin/admin`
3. Create a new realm (e.g., `your-realm`)
4. Create a client:
   - Client ID: `frontend-app`
   - Client Protocol: `openid-connect`
   - Access Type: `public`
   - Valid Redirect URIs: `http://localhost:3000/*` or `http://localhost:4200/*`
   - Web Origins: `http://localhost:3000` or `http://localhost:4200`
5. Create demo users in Keycloak with credentials

### 3. Update application.properties
Replace `your-realm` with your actual realm name in:
```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/your-realm
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8080/realms/your-realm/protocol/openid-connect/certs
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

## API Endpoints

### Public Endpoints (No Authentication)
- `GET /api/public/health` - Health check

### Protected Endpoints (Requires JWT Token)
- `GET /api/users` - Get all users
- `GET /api/users/me` - Get current authenticated user
- `POST /api/users` - Create new user

## Testing with Frontend
Your frontend needs to:
1. Authenticate with Keycloak
2. Get JWT access token
3. Include token in Authorization header: `Bearer <token>`

## H2 Database Console
Access at: http://localhost:8081/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)
