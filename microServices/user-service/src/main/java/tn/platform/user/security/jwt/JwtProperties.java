package tn.platform.user.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds {@code app.jwt.*} from the environment. Java defaults apply when a key is absent,
 * avoiding {@code Could not resolve placeholder} if {@code application.properties} is not loaded.
 */
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /**
     * Must be overridden in production via {@code app.jwt.secret} or env {@code APP_JWT_SECRET}.
     */
    private String secret = "Z8sYk1vFq2N6x9C4JtP0Lm7D5aR3Wb8HcE6UuTnQpXyVfK2s9M4G1hL0oR5zC7D";

    private long expirationMs = 86400000L;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public void setExpirationMs(long expirationMs) {
        this.expirationMs = expirationMs;
    }
}
