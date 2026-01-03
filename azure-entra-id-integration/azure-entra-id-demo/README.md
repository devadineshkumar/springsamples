# Azure Entra ID Demo (Spring Boot)

This small demo shows two ways to integrate with Azure Entra ID (Azure AD):

- Server-side sign-in via Spring Security + spring-boot-starter-oauth2-client (OpenID Connect / Authorization Code flow).
- Optional client-side sample page using MSAL.js (SPA) in `src/main/resources/static/index.html`.

Files changed/added
- `pom.xml` — added `spring-boot-starter-security` and `spring-boot-starter-oauth2-client`.
- `src/main/java/com/azure/demo/SecurityConfig.java` — security configuration (permit / and /welcome; enable oauth2Login).
- `src/main/java/com/azure/demo/controller/WelcomeController.java` — expose authenticated principal name to template.
- `src/main/resources/application.properties` — placeholders for Azure AD settings (replace with your values).
- `src/main/resources/templates/welcome.html` — adds Sign in / Sign out links and shows username when signed in.
- `src/main/resources/static/index.html` — client-side MSAL.js example (popup / redirect flows).

Azure App Registration (required)
1. Go to the Azure portal -> Azure Active Directory -> App registrations -> New registration.
2. Name: `demo-app` (or your own name).
3. Supported account types: choose Single tenant (My org only) or Multi-tenant as needed.
4. Redirect URI (optional here, but set it before testing):
   - For server-side Spring app (recommended): **Platform: Web**
     - Add: `http://localhost:8080/login/oauth2/code/azure`
   - For client-side SPA (MSAL.js): **Platform: Single-page application**
     - Add: `http://localhost:8080/index.html` (or `http://localhost:8080`)
5. Register the app.
6. Go to Certificates & secrets -> Client secrets -> New client secret. Copy the secret value (you'll need it).
7. (Optional) Under Authentication, ensure ID tokens (used for implicit flow) are enabled if you need them for SPA; for server-side Authorization Code flow you only need the redirect URI and client secret.

Configuration (`src/main/resources/application.properties`)
Replace the placeholders with values from your app registration:

spring.security.oauth2.client.registration.azure.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.azure.client-secret=YOUR_CLIENT_SECRET
spring.security.oauth2.client.registration.azure.scope=openid,profile,email
spring.security.oauth2.client.registration.azure.authorization-grant-type=authorization_code
spring.security.oauth2.client.provider.azure.issuer-uri=https://login.microsoftonline.com/YOUR_TENANT_ID/v2.0

Notes:
- `YOUR_TENANT_ID` can be your tenant GUID, or `common` for multi-tenant scenarios.
- If you choose not to use a client secret (public client using PKCE) you can omit the secret and configure Azure accordingly; Spring Boot supports PKCE for public clients when the client is not confidential.

Client-side MSAL (`src/main/resources/static/index.html`)
- Edit the two constants CLIENT_ID and TENANT_ID in the file to match your app registration.
- Register the SPA redirect URI in Azure to match `http://localhost:8080/index.html` (or your site).

Run the demo locally
- Build and run with Maven:

```powershell
mvn -DskipTests package; java -jar target/demo-0.0.1-SNAPSHOT.jar
```

or

```powershell
mvn spring-boot:run
```

- Open http://localhost:8080/ — the welcome page will show a "Sign in with Azure Entra ID" link if not signed in. Clicking it will redirect you to Microsoft's sign-in page. After sign-in you'll be returned to the app and will see the signed-in username.

Testing the SPA index page
- Open http://localhost:8080/index.html and use the MSAL buttons (remember to replace CLIENT_ID and TENANT_ID in the file). The SPA uses popup and redirect login flows.

Next steps / security
- In production, don't store client secrets in plain `application.properties`; use environment variables or Azure Key Vault.
- If you want the app to call Microsoft Graph, configure OAuth2 client registration scopes (e.g., offline_access, User.Read) and request an access token using Spring's OAuth2AuthorizedClient.
- Optionally add logout that calls Azure end-session endpoint to sign the user out from Azure AD as well.

If you'd like, I can:
- Wire client-secret through environment variables and update the README with exact export commands.
- Add a Graph call example to display user profile fields on the welcome page.
- Add unit tests for security config.

