package es.grupo18.jobmatcher.security.jwt;

import java.time.Duration;

public enum TokenType {

    ACCESS(Duration.ofMinutes(5), "AuthToken"),
    REFRESH(Duration.ofDays(7), "RefreshToken");

    /**
     * Token lifetime in seconds
     */
    
    public final Duration duration;
    public final String cookieName;

    TokenType(Duration duration, String cookieName) {
        this.duration = duration;
        this.cookieName = cookieName;
    }

}
