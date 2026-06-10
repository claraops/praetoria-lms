package cloud.praetoria.lms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import cloud.praetoria.lms.exceptions.JwtAuthenticationEntryPoint;
import cloud.praetoria.lms.security.CustomUserDetailsService;
import cloud.praetoria.lms.security.JwtAccessDeniedHandler;
import cloud.praetoria.lms.security.JwtAuthenticationFilter;
import cloud.praetoria.lms.security.JwtTokenProvider;
import cloud.praetoria.lms.repositories.UserRepository;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtTokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final UserRepository userRepository;

    public SecurityConfig(JwtTokenProvider tokenProvider,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          JwtAccessDeniedHandler jwtAccessDeniedHandler,
                          UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.userRepository = userRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(tokenProvider, userDetailsService);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:5173",
            "https://praetoria-learning.com"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           UserDetailsService userDetailsService,
                                           JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider(userDetailsService))
            .addFilterBefore(jwtAuthenticationFilter, AuthorizationFilter.class)
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/auth/login", "/auth/register", "/auth/refresh-token",
                                 "/auth/request-password-reset", "/auth/reset-password").permitAll()
                .requestMatchers("/auth/logout").authenticated()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/api/student/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/blocks/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/courses/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/exercises/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/modules/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/quizzes/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                .requestMatchers("/api/blocks/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers("/api/courses/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers("/api/exercises/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers("/api/modules/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers("/api/quizzes/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers("/api/progress/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                .requestMatchers("/api/gamification/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                .requestMatchers("/api/profile/**").authenticated()
                .requestMatchers("/api/teacher/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers("/api/promos/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers("/api/users/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
            );

        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));
        return http.build();
    }
}