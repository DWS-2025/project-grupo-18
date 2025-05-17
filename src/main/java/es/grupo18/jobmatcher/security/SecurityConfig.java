package es.grupo18.jobmatcher.security;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.grupo18.jobmatcher.security.jwt.JWTRequestFilter;
import es.grupo18.jobmatcher.security.jwt.UnauthorizedHandlerJWT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JWTRequestFilter jwtRequestFilter;

    @Autowired
    private UnauthorizedHandlerJWT unauthorizedHandlerJWT;

    @Autowired
    private RepositoryUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());

        http.securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(unauthorizedHandlerJWT)
                        .accessDeniedHandler(forbiddenHandler()))
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .authorizeHttpRequests(authorize -> authorize
                        // Public API endpoints
                        .requestMatchers("/api/login", "/api/login/**", "/api/register", "/api/register/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/companies").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/images/posts/**").permitAll()

                        // Authenticated user API
                        .requestMatchers(HttpMethod.POST, "/api/companies/*/favourites").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/companies/*/favourites").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/companies/*/favourites").hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/posts").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/posts/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**").hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/images/users/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/images/users/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/images/users/**").hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/images/posts/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/images/posts/**").hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/reviews/post/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/reviews/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/reviews/**").hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/users/me").hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/users/me/cv").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/me/cv").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/me/cv").hasAnyRole("USER", "ADMIN")

                        // Admin-only API
                        .requestMatchers(HttpMethod.POST, "/api/users/*/cv").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/*/cv").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/*/cv").hasRole("ADMIN")

                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/companies/**").hasRole("ADMIN")
                        .requestMatchers("/api/users/**").hasRole("ADMIN")

                        .anyRequest().authenticated());

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/main", "/register", "/login", "/loginerror", "/error/**", "/css/**",
                                "/js/**", "/img/**")
                        .permitAll()
                        .requestMatchers("/blog", "/blog/posts", "/blog/posts/*", "/blog/posts/*/image",
                                "/blog/posts/*/reviews/*")
                        .permitAll()
                        .requestMatchers("/matches/**").hasRole("USER")
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        .requestMatchers("/companies/**").hasRole("ADMIN")
                        .requestMatchers("/profile/**").hasRole("USER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureUrl("/loginerror")
                        .defaultSuccessUrl("/main", true)
                        .permitAll())
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            String uri = request.getRequestURI();
                            if (uri.startsWith("/companies") || uri.startsWith("/users")) {
                                response.sendRedirect("/main");
                            } else {
                                response.sendError(403);
                            }
                        })
                        .authenticationEntryPoint((request, response, authException) -> {
                            String uri = request.getRequestURI();
                            if (uri.startsWith("/companies") || uri.startsWith("/users")) {
                                response.sendRedirect("/main");
                            } else {
                                response.sendRedirect("/login");
                            }
                        }));

        return http.build();
    }

    @Bean
    public AccessDeniedHandler forbiddenHandler() {
        return (req, res, ex) -> {
            logger.warn("Access denied: {}", ex.getMessage());
            res.setStatus(HttpStatus.FORBIDDEN.value());
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            Map<String, Object> err = Map.of(
                    "status", 403,
                    "error", "FORBIDDEN",
                    "message", ex.getMessage());
            new ObjectMapper().writeValue(res.getWriter(), err);
        };
    }

}
