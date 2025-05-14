package es.grupo18.jobmatcher.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import es.grupo18.jobmatcher.security.jwt.JWTRequestFilter;
import es.grupo18.jobmatcher.security.jwt.UnauthorizedHandlerJWT;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

        private final JWTRequestFilter jwtRequestFilter;
        private final UnauthorizedHandlerJWT unauthorizedHandlerJWT;
        private final RepositoryUserDetailsService userDetailsService;

        @Autowired
        public SecurityConfig(JWTRequestFilter jwtRequestFilter,
                        UnauthorizedHandlerJWT unauthorizedHandlerJWT,
                        RepositoryUserDetailsService userDetailsService) {
                this.jwtRequestFilter = jwtRequestFilter;
                this.unauthorizedHandlerJWT = unauthorizedHandlerJWT;
                this.userDetailsService = userDetailsService;
        }

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
        public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
                return configuration.getAuthenticationManager();
        }

        @Bean
        @Order(1)
        public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
                http
                                .securityMatcher("/api/**")
                                .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandlerJWT))
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                // Endpoints públicos
                                                .requestMatchers("/api/login", "/api/login/**", "/api/register",
                                                                "/api/register/**")
                                                .permitAll()

                                                // Gestión propia del usuario
                                                .requestMatchers("/api/users/me/**").hasRole("USER")

                                                // Gestión de todos los usuarios (solo ADMIN)
                                                .requestMatchers("/api/users/**").hasRole("ADMIN")

                                                // Posts, Reviews, Company
                                                .requestMatchers("/api/posts/**", "/api/reviews/**")
                                                .hasAnyRole("USER", "ADMIN")
                                                .requestMatchers("/api/companies/**").hasAnyRole("USER", "ADMIN")
                                                .requestMatchers("/api/images/**").hasAnyRole("USER", "ADMIN")

                                                // Todo lo demás, autenticado
                                                .anyRequest().authenticated())

                                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        @Order(2)
        public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf
                                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/main", "/register", "/login", "/loginerror",
                                                                "/error/**", "/css/**", "/js/**", "/img/**")
                                                .permitAll()
                                                .requestMatchers("/blog", "/blog/posts", "/blog/posts/*",
                                                                "/blog/posts/*/image", "/blog/posts/*/reviews/*")
                                                .permitAll()
                                                .requestMatchers("/matches/**").hasRole("USER")
                                                .requestMatchers("/users/**").hasAnyRole("ADMIN")
                                                .requestMatchers("/companies/**").hasRole("ADMIN")
                                                .requestMatchers("/profile/**").hasRole("USER")
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/main", true)
                                                .permitAll());
                return http.build();
        }

}
