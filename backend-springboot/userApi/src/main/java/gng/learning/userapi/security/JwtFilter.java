package gng.learning.userapi.security;

import gng.learning.userapi.services.UserDataService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;
import java.util.logging.Logger;

// JwtFilter.java
// objective: Implement a filter to validate JWT tokens in incoming requests.
@Component
public class JwtFilter extends OncePerRequestFilter {


    private JwtUtil jwtUtil;

    private UserDataService userDataService;

    private final List<String> permittedPaths;

    private static final Logger logger = Logger.getLogger(JwtFilter.class.getName());

    public JwtFilter(JwtUtil jwtUtil, UserDataService userDataService, List<String> permittedPaths) {
        this.jwtUtil = jwtUtil;
        this.userDataService = userDataService;
        this.permittedPaths = permittedPaths;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws java.io.IOException, ServletException {

        logger.info("JWT Filter is processing the request to path: " + request.getRequestURI());

        // Check if the request URI matches any of the permitted paths
        if (permittedPaths.stream().anyMatch(
            path -> request.getRequestURI().startsWith(path)))
        {
            logger.info("Request is for a permitted path: " + request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the JWT from the Authorization header
        String authHeader = request.getHeader("Authorization");

        String jwt = null;
        String username = null;

        if (authHeader == null || !authHeader.startsWith("Bearer "))
        {
            logger.info("Invalid JWT token format in Authorization header");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token format");
            return;
        }

        jwt = authHeader.substring(7);

        try {
            // Validate the JWT token
            Claims claims = jwtUtil.extractAllClaims(jwt);

            if (claims.getExpiration() != null && claims.getExpiration().before(new java.util.Date())) {
                throw new JwtException("JWT token is expired");
            }


            username = claims.getSubject();

            logger.info("JWT token is valid for user: " + username);


            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                logger.info("load user details from " + username);


                CustomUser userDetails =  (CustomUser) userDataService.loadUserByUsername(username);

//                    UserDetails userDetails =   userDataService.loadUserByUsername(username);

                logger.info("User details loaded: " + userDetails.getUsername() + ", User ID: " + userDetails.getUserId());

//                    logger.info("User details loaded: " + userDetails.getUsername() );


                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());


                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.info("default security context stragety = " + SecurityContextHolder.getContextHolderStrategy().getClass().getName());
            }


        } catch (Exception e) {
        // Handle exceptions related to JWT validation
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token: " + e.getMessage());
            logger.info("JWT validation failed: " + e.getMessage());
        }




        filterChain.doFilter(request, response);
    }
}
