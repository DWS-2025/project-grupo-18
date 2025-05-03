package es.grupo18.jobmatcher.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import es.grupo18.jobmatcher.security.jwt.JWTRequestFilter;
import es.grupo18.jobmatcher.security.jwt.UnauthorizedHandlerJWT;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private JWTRequestFilter jwtRequestFilter;

        @Autowired
        private UnauthorizedHandlerJWT unauthorizedHandlerJwt;

        @Autowired
        private UserDetailsService userDetailsService;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
                return authConfig.getAuthenticationManager();
        }

        // API REST
        @Bean
        @Order(1)
        public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
                http.authenticationProvider(authenticationProvider());

                http.securityMatcher("/api/**")
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .exceptionHandling(
                                                handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/auth/**").permitAll() // login, register API
                                                .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/companies/**").permitAll()
                                                .requestMatchers("/api/users/me").hasRole("USER")
                                                .requestMatchers("/api/users/**").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.POST, "/api/posts/**").hasRole("USER")
                                                .requestMatchers(HttpMethod.PUT, "/api/posts/**").hasRole("USER") // debes
                                                                                                                  // validar
                                                                                                                  // propiedad
                                                                                                                  // en
                                                                                                                  // servicio
                                                .requestMatchers(HttpMethod.DELETE, "/api/posts/**")
                                                .hasAnyRole("USER", "ADMIN")
                                                .anyRequest().authenticated());

                http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        // Web
        @Bean
        @Order(2)
        public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
            http.authenticationProvider(authenticationProvider());
        
            http
                .authorizeHttpRequests(auth -> auth
                    // ALL
                    .requestMatchers("/", "/main", "/error", "/blog/posts", "/blog/posts/{postId}", "/blog/posts/{postId}/reviews/{reviewId}", "/login", "/loginerror").permitAll()
        
                    // GUEST or USER
                    .requestMatchers("/register").permitAll()
        
                    // MATCH (only USER)
                    .requestMatchers("/matches/**", "/company/**").hasRole("USER")
        
                    // PROFILE
                    .requestMatchers("/profile/**").hasRole("USER")
        
                    // USERS (only ADMIN)
                    .requestMatchers("/users/**").hasRole("ADMIN")
        
                    // BLOG
                    .requestMatchers(
                        "/blog/posts/new",
                        "/blog/posts/{postId}/edit",
                        "/blog/posts/{postId}/delete",
                        "/blog/posts/{id}/image",
                        "/blog/posts/{postId}/reviews/new",
                        "/blog/posts/{postId}/reviews/{reviewId}/edit",
                        "/blog/posts/{postId}/reviews/{reviewId}/delete"
                    ).hasAnyRole("USER", "ADMIN")
        
                    // COMPANIES
                    .requestMatchers("/companies/**").hasRole("ADMIN")
        
                    // Any other request
                    .anyRequest().authenticated()
                )
                .formLogin(form -> form
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/main", true)
                    .failureUrl("/loginerror")
                    .permitAll()
                )
                .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .permitAll()
                )
                .exceptionHandling(ex -> ex.accessDeniedPage("/403"))
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
        
            return http.build();
        }   

}
