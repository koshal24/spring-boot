// package com.lms.security;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.web.SecurityFilterChain;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {
//     @Autowired
//     private MyUserDetailsService myUserDetailsService;
//     @Autowired
//     private JwtRequestFilter jwtRequestFilter;

//     @Autowired
//     public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//         auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder());
//     }

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     @Bean
//     public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//         return authenticationConfiguration.getAuthenticationManager();
//     }

//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         http.csrf().disable()
//             .authorizeHttpRequests(authorize -> authorize
//                 .requestMatchers("/api/auth/**").permitAll()
//                 .requestMatchers("/api/admin/**", "/api/analytics/**").hasAuthority("ADMIN")
//                 .requestMatchers("/api/educator/**").hasAuthority("EDUCATOR")
//                 .requestMatchers("/api/student/**").hasAuthority("STUDENT")
//                 .requestMatchers("/api/certificates/**").hasAnyAuthority("ADMIN", "EDUCATOR", "STUDENT")
//                 .requestMatchers("/api/quizzes/**", "/api/quiz-attempts/**").hasAnyAuthority("ADMIN", "EDUCATOR", "STUDENT")
//                 .requestMatchers("/api/forums/**").hasAnyAuthority("ADMIN", "EDUCATOR", "STUDENT")
//                 .requestMatchers("/api/notifications/**").hasAnyAuthority("ADMIN", "EDUCATOR", "STUDENT")
//                 .anyRequest().authenticated()
//             )
//             .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//         http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
//         return http.build();
//     }
// }

package com.lms.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final MyUserDetailsService myUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(MyUserDetailsService myUserDetailsService, JwtRequestFilter jwtRequestFilter) {
        this.myUserDetailsService = myUserDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    // ✅ Use AuthenticationConfiguration to provide AuthenticationManager bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**", "/api/analytics/**").hasAuthority("ADMIN")
                .requestMatchers("/api/educator/**").hasAuthority("EDUCATOR")
                .requestMatchers("/api/student/**").hasAuthority("STUDENT")
                .requestMatchers("/api/certificates/**").hasAnyAuthority("ADMIN", "EDUCATOR", "STUDENT")
                .requestMatchers("/api/quizzes/**", "/api/quiz-attempts/**").hasAnyAuthority("ADMIN", "EDUCATOR", "STUDENT")
                .requestMatchers("/api/forums/**").hasAnyAuthority("ADMIN", "EDUCATOR", "STUDENT")
                .requestMatchers("/api/notifications/**").hasAnyAuthority("ADMIN", "EDUCATOR", "STUDENT")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // ✅ Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Value("${cors.allowed-origins:http://localhost:3000,http://127.0.0.1:3000}")
    private String corsAllowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Read allowed origins from application properties (comma-separated)
        String[] origins = Arrays.stream(corsAllowedOrigins.split(","))
                .map(String::trim)
                .toArray(String[]::new);
        configuration.setAllowedOrigins(Arrays.asList(origins));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
