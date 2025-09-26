package gng.learning.gateway.config;

import gng.learning.gateway.jwt.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    private JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }


    @Bean
    public SecurityFilterChain mySecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration corsConfig = new CorsConfiguration();
                    corsConfig.setAllowedOriginPatterns(List.of("*")); // allow all origins
                    corsConfig.setAllowedMethods(List.of("*"));        // allow all methods
                    corsConfig.setAllowedHeaders(List.of("*"));        // allow all headers
                    corsConfig.setExposedHeaders(List.of("*"));        // optional, expose all response headers
                    corsConfig.setAllowCredentials(true);              // set to false if don't need cookies/Auth
                    return corsConfig;
                }))

                //.cors(withDefaults()) // enable CORS with default settings
                .csrf( AbstractHttpConfigurer::disable) // disable CSRF protection to allow POST requests from clients
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) )// use stateless session management=> appropriate for REST APIs
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/user/register", "/user/login").permitAll()

                        .requestMatchers("/user/**").authenticated() // for testing gateway only
                        .requestMatchers("/account/**").authenticated()
                        .requestMatchers("/transaction/**").authenticated()
//                        .requestMatchers("/user/**").hasAuthority("USER") // only users with USER authority can access /user/** endpoints

                        .requestMatchers(HttpMethod.POST, "/incoming-transaction").permitAll()
                        .requestMatchers("/incoming-transaction/**").authenticated()
                        .anyRequest().authenticated()   // any other request requires authentication
                )
                .formLogin( form -> form.disable())  // disable form-based login/logout filter
                .httpBasic(AbstractHttpConfigurer::disable)  // disable basic authentication filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // add JWT filter before UsernamePasswordAuthenticationFilter
                .build();

    }

//    @Bean
//    public CorsConfigurationSource config_coreConfigurationSource() {
//
//            var cors = new CorsConfiguration();
//            cors.setAllowedOrigins(List.of("http://localhost:4200"));
//            cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//            cors.setAllowedHeaders(List.of("*")); // allow all headers
//            cors.setAllowCredentials(true);
//            cors.setMaxAge(3600L);
//
//            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//            source.registerCorsConfiguration("/**", cors);
//            return source;
//    };




}
