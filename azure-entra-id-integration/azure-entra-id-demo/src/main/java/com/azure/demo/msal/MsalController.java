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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
}
