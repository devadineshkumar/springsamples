# JWT Token Management API Guide

This guide explains how to extract and manage JWT tokens (access token, ID token, refresh token) from the MSAL session.

## 📋 Available Token Endpoints

### 1. Get All Tokens
**Endpoint:** `GET /msal/tokens`

Returns all JWT tokens from the MSAL session including access token, ID token, refresh token, expiry time, and account information.

**Usage:**
```bash
curl http://localhost:8080/msal/tokens
```

**Response:**
```json
{
  "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGc...",
  "idToken": "eyJ0eXAiOiJKV1QiLCJub25jZSI...",
  "refreshToken": "0.AX4AV37A4tccu0SKqT...",
  "expiresOn": "2026-01-05T12:30:00Z",
  "tokenType": "Bearer",
  "account": {
    "username": "user@example.com",
    "homeAccountId": "...",
    "environment": "login.microsoftonline.com"
  }
}
```

---

### 2. Get Access Token Only
**Endpoint:** `GET /msal/access-token`

Returns only the access token (useful for API calls).

**Usage:**
```bash
curl http://localhost:8080/msal/access-token
```

**Response:**
```json
{
  "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGc...",
  "expiresOn": "2026-01-05T12:30:00Z",
  "tokenType": "Bearer"
}
```

**Use the access token to call Microsoft Graph:**
```bash
# Extract the token
ACCESS_TOKEN=$(curl -s http://localhost:8080/msal/access-token | jq -r .accessToken)

# Call Microsoft Graph API
curl -H "Authorization: Bearer $ACCESS_TOKEN" https://graph.microsoft.com/v1.0/me
```

---

### 3. Refresh Access Token
**Endpoint:** `POST /msal/refresh`

Refreshes the access token using the refresh token without requiring the user to sign in again.

**Usage:**
```bash
curl -X POST http://localhost:8080/msal/refresh
```

**Response (Success):**
```json
{
  "message": "Token refreshed successfully",
  "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGc...",
  "expiresOn": "2026-01-05T13:30:00Z",
  "username": "user@example.com"
}
```

**Response (Error):**
```json
{
  "error": "No refresh token available. Ensure 'offline_access' scope is requested in msal.scopes property."
}
```

---

### 4. Logout (Clear Session)
**Endpoint:** `POST /msal/logout`

Clears the session and invalidates tokens locally. Note: This does not revoke the refresh token on Azure's side.

**Usage:**
```bash
curl -X POST http://localhost:8080/msal/logout
```

---

## 🔐 Understanding the Tokens

### Access Token (JWT)
- **Purpose:** Used to call Microsoft Graph API or your protected APIs
- **Lifetime:** Typically 1 hour
- **Format:** JWT (JSON Web Token)
- **Usage:** Include in `Authorization: Bearer {accessToken}` header

**Decode the access token** at https://jwt.io to see claims like:
- `aud` (audience): Which API the token is for
- `iss` (issuer): Azure AD tenant
- `exp` (expiration): Unix timestamp
- `scp` (scopes): Permissions granted
- `roles`: User roles (if configured)

### ID Token (JWT)
- **Purpose:** Contains user identity claims
- **Claims include:**
  - `name`: User's display name
  - `email`: User's email
  - `preferred_username`: Username
  - `sub`: Subject (unique user ID)
  - `oid`: Object ID in Azure AD

### Refresh Token
- **Purpose:** Get a new access token without re-prompting the user
- **Lifetime:** Long-lived (weeks to months)
- **Scope required:** `offline_access` (already included in `msal.scopes`)
- **Security:** Treat as highly sensitive (equivalent to password)

---

## 🚀 Quick Start Guide

### 1. Sign In
Visit `http://localhost:8080/msal/login` and complete the Azure sign-in.

### 2. Get Tokens
```bash
# Get all tokens
curl http://localhost:8080/msal/tokens | jq

# Get just the access token
curl http://localhost:8080/msal/access-token | jq
```

### 3. Use Access Token
```bash
# Store token in variable (bash/PowerShell)
ACCESS_TOKEN=$(curl -s http://localhost:8080/msal/access-token | jq -r .accessToken)

# PowerShell
$ACCESS_TOKEN = (curl http://localhost:8080/msal/access-token | ConvertFrom-Json).accessToken

# Call Microsoft Graph
curl -H "Authorization: Bearer $ACCESS_TOKEN" https://graph.microsoft.com/v1.0/me
```

### 4. Refresh Token (when expired)
```bash
curl -X POST http://localhost:8080/msal/refresh | jq
```

---

## 📊 Token Expiry and Refresh Strategy

### When to Refresh
- Access tokens expire after ~1 hour
- Refresh **before** the token expires to avoid disruption
- Check the `expiresOn` timestamp

### Best Practices
1. **Proactive refresh:** Refresh 5-10 minutes before expiry
2. **Handle errors:** If refresh fails, redirect to `/msal/login`
3. **Secure storage:** Never expose refresh tokens to client-side JavaScript
4. **Use HTTPS:** Always use HTTPS in production

### Example: Auto-refresh Logic (pseudocode)
```java
if (accessToken.expiresOn < now + 5 minutes) {
    try {
        newToken = msalService.refreshToken(refreshToken);
        session.setAttribute("msal_auth_result", newToken);
    } catch (Exception e) {
        // Refresh failed, redirect to login
        return "redirect:/msal/login";
    }
}
```

---

## 🔧 Configuration

### Required Scope for Refresh Token
Ensure `offline_access` is included in `application.properties`:

```properties
msal.scopes=openid,profile,email,offline_access,User.Read
```

### Environment Variables
```bash
# PowerShell
$env:AZURE_CLIENT_ID = "your-client-id"
$env:AZURE_CLIENT_SECRET = "your-client-secret"
$env:AZURE_TENANT_ID = "your-tenant-id"

# Bash
export AZURE_CLIENT_ID="your-client-id"
export AZURE_CLIENT_SECRET="your-client-secret"
export AZURE_TENANT_ID="your-tenant-id"
```

---

## 🛡️ Security Best Practices

### Token Storage
- ✅ **Server-side session:** Store tokens in HTTP session (current implementation)
- ❌ **Client-side localStorage:** Never store access/refresh tokens in browser storage
- ✅ **Secure cookies:** Use `httpOnly`, `secure`, `sameSite` flags

### Token Transmission
- ✅ Always use HTTPS in production
- ✅ Include tokens only in `Authorization` header
- ❌ Never include tokens in URL query parameters

### Token Lifecycle
- Implement token refresh before expiry
- Clear tokens on logout
- Consider implementing a token revocation endpoint
- Monitor for suspicious token usage

---

## 🧪 Testing with Postman/cURL

### 1. Sign In (Browser)
Open browser: `http://localhost:8080/msal/login`

### 2. Get Session Cookie
After sign-in, copy the `JSESSIONID` cookie from browser DevTools.

### 3. Call Token APIs with Cookie
```bash
# cURL with cookie
curl -b "JSESSIONID=YOUR_SESSION_ID" http://localhost:8080/msal/tokens

# Postman: Add cookie in Headers
Cookie: JSESSIONID=YOUR_SESSION_ID
```

---

## 📈 Common Use Cases

### Use Case 1: Call Microsoft Graph API
```bash
# Get access token
TOKEN=$(curl -s http://localhost:8080/msal/access-token | jq -r .accessToken)

# Get user profile
curl -H "Authorization: Bearer $TOKEN" https://graph.microsoft.com/v1.0/me

# Get user's emails
curl -H "Authorization: Bearer $TOKEN" https://graph.microsoft.com/v1.0/me/messages

# Get user's calendar
curl -H "Authorization: Bearer $TOKEN" https://graph.microsoft.com/v1.0/me/calendar/events
```

### Use Case 2: Decode Token Claims
```bash
# Get ID token
ID_TOKEN=$(curl -s http://localhost:8080/msal/tokens | jq -r .idToken)

# Decode at jwt.io or use jq
echo $ID_TOKEN | cut -d. -f2 | base64 -d | jq
```

### Use Case 3: Implement Auto-Refresh in Frontend
```javascript
async function getAccessToken() {
    const response = await fetch('/msal/access-token');
    const data = await response.json();
    
    if (data.error) {
        // Not authenticated, redirect to login
        window.location.href = '/msal/login';
        return null;
    }
    
    // Check if token expires soon (within 5 minutes)
    const expiresOn = new Date(data.expiresOn);
    const now = new Date();
    const fiveMinutes = 5 * 60 * 1000;
    
    if (expiresOn.getTime() - now.getTime() < fiveMinutes) {
        // Refresh token
        await fetch('/msal/refresh', { method: 'POST' });
        // Retry getting token
        return getAccessToken();
    }
    
    return data.accessToken;
}

// Use in API calls
async function callGraphAPI() {
    const token = await getAccessToken();
    if (!token) return;
    
    const response = await fetch('https://graph.microsoft.com/v1.0/me', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    const user = await response.json();
    console.log(user);
}
```

---

## 🐛 Troubleshooting

### Error: "No session found"
- **Cause:** User is not signed in or session expired
- **Fix:** Sign in via `/msal/login`

### Error: "No refresh token available"
- **Cause:** `offline_access` scope not requested
- **Fix:** Add `offline_access` to `msal.scopes` in `application.properties`

### Error: "Token expired"
- **Cause:** Access token has expired
- **Fix:** Call `/msal/refresh` to get a new access token

### Error: "Refresh failed: AADSTS..."
- **Cause:** Refresh token expired or revoked
- **Fix:** User must sign in again via `/msal/login`

### Access Token doesn't work with Graph API
- **Cause:** Wrong audience or missing scopes
- **Fix:** Ensure `User.Read` or other Graph scopes are in `msal.scopes`

---

## 📚 Related Documentation

- [Microsoft Graph API Reference](https://docs.microsoft.com/en-us/graph/api/overview)
- [MSAL4J Documentation](https://github.com/AzureAD/microsoft-authentication-library-for-java)
- [Azure AD Token Reference](https://docs.microsoft.com/en-us/azure/active-directory/develop/access-tokens)
- [JWT.io - Decode tokens](https://jwt.io)

---

## 🎯 Next Steps

1. ✅ **Test the endpoints** - Sign in and try each endpoint
2. ✅ **Call Microsoft Graph** - Use the access token to fetch user data
3. ✅ **Implement auto-refresh** - Add proactive token refresh logic
4. ✅ **Secure in production** - Use HTTPS, secure cookies, monitor usage
5. ✅ **Add custom scopes** - Request additional Graph API scopes as needed

---

**Need help?** Check the main README.md or review the MsalController.java source code.

