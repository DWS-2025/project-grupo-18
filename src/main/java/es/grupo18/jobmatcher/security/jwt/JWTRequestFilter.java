package es.grupo18.jobmatcher.security.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JWTRequestFilter.class);

    private final UserDetailsService userDetailsService;

    private final JWTTokenProvider jwtTokenProvider;

    public JWTRequestFilter(UserDetailsService userDetailsService, JWTTokenProvider jwtTokenProvider) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            var claims = jwtTokenProvider.validateToken(request, true);
            var userDetails = userDetailsService.loadUserByUsername(claims.getSubject());

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception ex) {
            // Avoid logging when no token is found
            if (!ex.getMessage().equals("No access token cookie found in request")) {
                log.error("Exception processing JWT Token: ", ex);
            }
        }

        filterChain.doFilter(request, response);
    }

}
