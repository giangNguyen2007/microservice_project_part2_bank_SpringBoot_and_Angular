package gng.learning.userapi.config;

import gng.learning.userapi.security.JwtFilter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import gng.learning.userapi.services.UserDataService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private JwtFilter jwtFilter;




    public SpringSecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain mySecurityFilterChain(HttpSecurity http) throws Exception {
        http
//            .cors(cors -> cors.configurationSource(request -> {
//                CorsConfiguration corsConfig = new CorsConfiguration();
//                corsConfig.setAllowedOriginPatterns(List.of("*")); // allow all origins
//                corsConfig.setAllowedMethods(List.of("*"));        // allow all methods
//                corsConfig.setAllowedHeaders(List.of("*"));        // allow all headers
//                corsConfig.setExposedHeaders(List.of("*"));        // optional, expose all response headers
//                corsConfig.setAllowCredentials(true);              // set to false if you don't need cookies/Auth
//                return corsConfig;
//            }))


            .csrf( AbstractHttpConfigurer::disable) // disable CSRF protection to allow POST requests from clients
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) )// use stateless session management=> appropriate for REST APIs
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/user/register", "/user/login").permitAll()
                    .requestMatchers("/user/**").permitAll()  // for testing gateway only
                    //.requestMatchers("/user/**").hasAuthority("USER") // only users with USER authority can access /user/** endpoints
                    .anyRequest().authenticated()   // any other request requires authentication
            )
            .formLogin( form -> form.disable())  // provide default form-based login/logout support
            .httpBasic(withDefaults())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // add JWT filter before UsernamePasswordAuthenticationFilter


        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public AuthenticationManager authenticationManager(
//            HttpSecurity http,
//            BCryptPasswordEncoder bCryptPasswordEncoder,
//            UserDataService userDataService) throws Exception {
//
//        AuthenticationManagerBuilder authenticationManagerBuilder =
//                http.getSharedObject(AuthenticationManagerBuilder.class);
//
//        authenticationManagerBuilder.userDetailsService(userDataService)
//                                    .passwordEncoder(bCryptPasswordEncoder);
//
//        return http.getSharedObject(AuthenticationManager.class);
//    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        // Spring builds the AuthenticationManager from the registered providers (e.g., DaoAuthenticationProvider)
        return authConfig.getAuthenticationManager();
    }


//    @Bean
//    public CorsConfigurationSource config_coreConfigurationSource() {
//
//        var cors = new CorsConfiguration();
//
//        cors.setAllowedOrigins(List.of("*"));
//        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        cors.setAllowedHeaders(List.of("*")); // allow all headers
//        cors.setAllowCredentials(true);
//        cors.setMaxAge(3600L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", cors);
//        return source;
//    };



}


