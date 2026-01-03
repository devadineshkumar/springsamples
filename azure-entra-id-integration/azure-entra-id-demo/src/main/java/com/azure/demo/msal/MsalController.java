package com.azure.demo.msal;

import com.microsoft.aad.msal4j.IAuthenticationResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class MsalController {

    private static final Logger log = LoggerFactory.getLogger(MsalController.class);

    @Value("${msal.client-id:}")
    private String clientId;

    @Value("${msal.redirect-uri:http://localhost:8080/msal/callback}")
    private String redirectUri;

    @Value("${msal.scopes:openid}")
    private String scope;

    @Value("${msal.authority:https://login.microsoftonline.com/common}")
    private String issuerUri;

    private final MsalService msalService;

    public MsalController(MsalService msalService) {
        this.msalService = msalService;
    }

    @GetMapping("/msal/login")
    public RedirectView login(HttpServletRequest request) {
        String state = UUID.randomUUID().toString();
        HttpSession session = request.getSession(true);
        session.setAttribute("msal_state", state);

        String authUrl = buildAuthorizeUrl(state);
        log.debug("Redirecting to MSAL authorize URL: {} (sessionId={})", authUrl, session.getId());
        return new RedirectView(authUrl);
    }

    @GetMapping(value = "/msal/config", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String config() {
        // Return the computed authorize URL and effective config so user can inspect for redirect-uri mismatches
        String state = "[test-state]";
        String url = buildAuthorizeUrl(state);
        StringBuilder sb = new StringBuilder();
        sb.append("authorizeUrl=").append(url).append("\n");
        sb.append("clientId=").append(clientId).append("\n");
        sb.append("redirectUri=").append(redirectUri).append("\n");
        sb.append("scopeProperty=").append(scope).append("\n");
        sb.append("issuerProperty=").append(issuerUri).append("\n");
        return sb.toString();
    }

    private String buildAuthorizeUrl(String state) {
        // Ensure issuerUri is usable (fall back to common if placeholder remains)
        String issuer = issuerUri;
        if (issuer == null || issuer.isBlank() || issuer.contains("${")) {
            issuer = "https://login.microsoftonline.com/common";
        }

        // Normalize issuer base: remove any trailing /v2.0 or extra slashes, then append the v2 authorize endpoint
        String issuerBase = issuer.replaceAll("/v2\\.0/*$", "").replaceAll("/+$", "");
        String authorizeBase = issuerBase + "/oauth2/v2.0/authorize";

        // Build scope: property may be comma-separated (from application.properties). Convert to space-separated for v2 endpoint.
        String rawScope = scope == null ? "openid" : scope;
        String spaceSeparatedScope = Arrays.stream(rawScope.replace(',', ' ').trim().split("\\s+"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(" "));

        String encodedRedirect = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
        String encodedScope = URLEncoder.encode(spaceSeparatedScope, StandardCharsets.UTF_8).replace("+", "%20");

        return String.format("%s?client_id=%s&response_type=code&redirect_uri=%s&response_mode=query&scope=%s&state=%s",
                authorizeBase,
                URLEncoder.encode(clientId, StandardCharsets.UTF_8),
                encodedRedirect,
                encodedScope,
                URLEncoder.encode(state, StandardCharsets.UTF_8));
    }

    @GetMapping("/msal/callback")
    public String callback(@RequestParam(required = false) String code,
                           @RequestParam(required = false) String state,
                           @RequestParam(required = false) String error,
                           HttpServletRequest request,
                           Model model) {
        HttpSession session = request.getSession(false);
        String sessionId = session == null ? "null" : session.getId();
        Object savedState = session == null ? null : session.getAttribute("msal_state");

        log.debug("MSAL callback received: sessionId={}, incomingState={}, savedState={}, code={}, error={}",
                sessionId, state, savedState, code, error);

        model.addAttribute("incomingState", state);
        model.addAttribute("sessionId", sessionId);
        model.addAttribute("savedState", savedState == null ? "null" : savedState.toString());
        model.addAttribute("code", code);
        model.addAttribute("errorParam", error);

        if (error != null) {
            model.addAttribute("message", "Authentication failed: " + error);
            return "welcome";
        }
        if (session == null || state == null || !state.equals(session.getAttribute("msal_state"))) {
            model.addAttribute("message", "Invalid state — session or state mismatch. See sessionId/incomingState/savedState above.");
            return "welcome";
        }
        try {
            IAuthenticationResult result = msalService.acquireTokenByAuthCode(code);
            session.setAttribute("msal_auth_result", result);
            model.addAttribute("message", "MSAL login successful for: " + result.account().username());
            model.addAttribute("username", result.account().username());
            return "welcome";
        } catch (Exception e) {
            log.error("Token exchange failed", e);
            model.addAttribute("message", "Token exchange failed: " + e.getMessage());
            return "welcome";
        }
    }

    @GetMapping(value = "/msal/session", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String sessionDump(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return "No session";
        StringBuilder sb = new StringBuilder();
        sb.append("sessionId=").append(session.getId()).append("\n");
        var names = session.getAttributeNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            Object val = session.getAttribute(name);
            sb.append(name).append("=").append(val == null ? "null" : val.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * GET /msal/tokens - Returns all JWT tokens from the MSAL session
     * Returns: JSON with accessToken, idToken, refreshToken, expiresOn, and account info
     */
    @GetMapping(value = "/msal/tokens", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getTokens(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Map.of("error", "No session found. Please sign in first.");
        }

        IAuthenticationResult authResult = (IAuthenticationResult) session.getAttribute("msal_auth_result");
        if (authResult == null) {
            return Map.of("error", "Not authenticated. Please sign in via /msal/login first.");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", authResult.accessToken());
        response.put("idToken", authResult.idToken());

        // Note: MSAL4J doesn't expose refresh token via public API
        // Token refresh is handled via acquireTokenSilently with the account
        response.put("refreshToken", "Managed by MSAL (use /msal/refresh endpoint)");

        response.put("expiresOn", authResult.expiresOnDate().toString());
        response.put("tokenType", "Bearer");

        Map<String, Object> accountInfo = new HashMap<>();
        accountInfo.put("username", authResult.account().username());
        accountInfo.put("homeAccountId", authResult.account().homeAccountId());
        accountInfo.put("environment", authResult.account().environment());
        response.put("account", accountInfo);

        log.info("Tokens retrieved for user: {}", authResult.account().username());
        return response;
    }

    /**
     * GET /msal/access-token - Returns only the access token (JWT)
     * Use this token in Authorization header: Bearer {accessToken}
     */
    @GetMapping(value = "/msal/access-token", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getAccessToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Map.of("error", "No session found");
        }

        IAuthenticationResult authResult = (IAuthenticationResult) session.getAttribute("msal_auth_result");
        if (authResult == null) {
            return Map.of("error", "Not authenticated");
        }

        return Map.of(
            "accessToken", authResult.accessToken(),
            "expiresOn", authResult.expiresOnDate().toString(),
            "tokenType", "Bearer"
        );
    }

    /**
     * POST /msal/refresh - Refresh the access token using the refresh token
     * Returns the new access token and updates the session
     */
    @PostMapping(value = "/msal/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> refreshToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Map.of("error", "No session found");
        }

        IAuthenticationResult oldResult = (IAuthenticationResult) session.getAttribute("msal_auth_result");
        if (oldResult == null) {
            return Map.of("error", "Not authenticated. Please sign in first.");
        }

        try {
            log.info("Refreshing token for user: {}", oldResult.account().username());
            // Use account-based silent token acquisition (MSAL handles the refresh token internally)
            IAuthenticationResult newResult = msalService.refreshTokenSilently(oldResult.account());
            session.setAttribute("msal_auth_result", newResult);

            log.info("Token refreshed successfully for user: {}", newResult.account().username());
            return Map.of(
                "message", "Token refreshed successfully",
                "accessToken", newResult.accessToken(),
                "expiresOn", newResult.expiresOnDate().toString(),
                "username", newResult.account().username()
            );
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            return Map.of("error", "Refresh failed: " + e.getMessage());
        }
    }

    /**
     * POST /msal/logout - Clear session and invalidate tokens locally
     */
    @PostMapping("/msal/logout")
    public String logout(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            log.info("Session invalidated (user logged out)");
        }
        model.addAttribute("message", "Logged out successfully (session cleared)");
        return "welcome";
    }
}
