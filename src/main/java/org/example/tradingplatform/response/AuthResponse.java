package org.example.tradingplatform.response;

public record AuthResponse (String jwt,
                            boolean status,
                            String message,
                            boolean isTwoFactorAuthEnabled,
                            String session) {
    public AuthResponse(String jwt, boolean status, String registerSuccess) {
        this(jwt, status, registerSuccess, false, null);
    }

    public AuthResponse(String jwt, boolean status, String registerSuccess, boolean isTwoFactorAuthEnabled) {
        this(jwt, status, registerSuccess, isTwoFactorAuthEnabled, null);
    }

    public AuthResponse(String jwt, boolean status, String message, boolean isTwoFactorAuthEnabled, String session) {
        this.jwt = jwt;
        this.status = status;
        this.message = message;
        this.isTwoFactorAuthEnabled = isTwoFactorAuthEnabled;
        this.session = session;
    }

    public AuthResponse(String session, String message) {
        this("", false, message, false, session);
    }
}
