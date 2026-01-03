package com.azure.demo.msal;

import com.microsoft.aad.msal4j.AuthorizationCodeParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.ConfidentialClientApplication.Builder;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.SilentParameters;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MsalService {

    @Value("${msal.client-id:}")
    private String clientId;

    @Value("${msal.client-secret:}")
    private String clientSecret;

    @Value("${msal.redirect-uri:http://localhost:8080/msal/callback}")
    private String redirectUri;

    @Value("${msal.scopes:openid}")
    private String scope;

    @Value("${msal.tenant-id:}")
    private String tenantId;

    @Value("${msal.authority:https://login.microsoftonline.com}")
    private String authorityBase;

    private ConfidentialClientApplication buildApp() throws Exception {
        // Normalize authorityBase (remove trailing slashes)
        String base = authorityBase == null ? "https://login.microsoftonline.com" : authorityBase.replaceAll("/+$", "");

        // If authorityBase already contains a tenant segment (e.g. https://login.microsoftonline.com/{tenant}), use it directly
        String resolvedAuthority = null;
        try {
            // Check if the base contains a path segment after the host
            // e.g. https://login.microsoftonline.com/tenantId
            String withoutScheme = base.replaceFirst("https?://", "");
            String[] parts = withoutScheme.split("/");
            if (parts.length >= 2 && parts[1] != null && !parts[1].isBlank()) {
                // authorityBase already includes tenant
                resolvedAuthority = base;
            }
        } catch (Exception ignore) {
            // fall through to build from tenantId
        }

        if (resolvedAuthority == null) {
            String tenant = (tenantId == null || tenantId.isBlank()) ? "" : tenantId.trim();
            if (tenant.isBlank()) {
                throw new IllegalStateException("msal.tenant-id is not set and msal.authority does not include a tenant.\n" +
                        "Set 'msal.tenant-id' (e.g. your tenant GUID or tenant domain) in application.properties or as environment variable AZURE_TENANT_ID.\n" +
                        "Or set 'msal.authority' to include the tenant, e.g. https://login.microsoftonline.com/{your-tenant-id}");
            }
            if ("common".equalsIgnoreCase(tenant)) {
                // Using /common requires the app to be multi-tenant in Azure. Provide a clear error to the developer.
                throw new IllegalStateException("msal.tenant-id is set to 'common'.\n" +
                        "The application appears to be single-tenant.\n" +
                        "Either set 'msal.tenant-id' to your tenant id (GUID or domain) in application.properties or environment (e.g. AZURE_TENANT_ID),\n" +
                        "or configure the Azure App Registration to be multi-tenant (Accounts in any organizational directory).\n" +
                        "Current authorityBase=" + authorityBase + ", tenantId=" + tenantId + "\n");
            }
            resolvedAuthority = base + "/" + tenant;
        }

        Builder builder = ConfidentialClientApplication.builder(clientId, com.microsoft.aad.msal4j.ClientCredentialFactory.createFromSecret(clientSecret));
        builder.authority(resolvedAuthority);
        return builder.build();
    }

    private Set<String> parseScopes(String scopeStr) {
        if (scopeStr == null || scopeStr.isBlank()) return new HashSet<>();
        // support comma or space separated lists
        String normalized = scopeStr.replace(',', ' ');
        String[] parts = normalized.trim().split("\\s+");
        return new HashSet<>(Arrays.asList(parts));
    }

    public IAuthenticationResult acquireTokenByAuthCode(String code) throws Exception {
        ConfidentialClientApplication app = buildApp();
        Set<String> scopes = parseScopes(scope);
        AuthorizationCodeParameters parameters = AuthorizationCodeParameters.builder(code, new URI(redirectUri)).scopes(scopes).build();
        CompletableFuture<IAuthenticationResult> future = app.acquireToken(parameters);
        return future.get();
    }

    /**
     * Refresh the access token using the account (MSAL4J handles refresh token internally)
     * @param account The account from a previous authentication result
     * @return New IAuthenticationResult with refreshed access token
     */
    public IAuthenticationResult refreshTokenSilently(com.microsoft.aad.msal4j.IAccount account) throws Exception {
        ConfidentialClientApplication app = buildApp();
        Set<String> scopes = parseScopes(scope);

        // Use silent token acquisition - MSAL will use the cached refresh token automatically
        SilentParameters parameters = SilentParameters.builder(scopes, account)
                .build();

        CompletableFuture<IAuthenticationResult> future = app.acquireTokenSilently(parameters);
        return future.get();
    }
}
