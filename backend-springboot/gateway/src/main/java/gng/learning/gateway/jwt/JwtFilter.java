package gng.learning.gateway.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Key;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {


    public String SECRET_KEY = "your-256-bit-secret-your-256-bit-secret";

    private final List<String> permittedPaths;

    public JwtFilter( List<String> permittedPaths) {
        this.permittedPaths = permittedPaths;
    }


    private static final Logger logger = Logger.getLogger(JwtFilter.class.getName());

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        logger.info("JWT Filter is processing the request to path: " + request.getRequestURI());


        // extract only the path from URI (exclude host, port, query params)
        String  requestPath;
        try {
            requestPath =   new URI(request.getRequestURI()).getPath();
            logger.info("Extracted request path: " + requestPath);
            // sample output: Extracted request path: /incoming-transaction/validate => removing host, port & query params
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // Check if the request path is in the list of permitted paths
        for (String permittedPath : permittedPaths) {
            if (requestPath.equals(permittedPath)) {
                logger.info("Request is for a permitted path: " + request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        }

        String jwt = authHeader.substring(7);

        logger.info("Extracted JWT: " + jwt);

        Key signingKey = Keys.hmacShaKeyFor(this.SECRET_KEY.getBytes());

        try {

            JwtParserBuilder b = Jwts.parserBuilder().setSigningKey(this.SECRET_KEY.getBytes());

            Claims claims = b.build().parseClaimsJws(jwt).getBody();

            logger.info("JWT token is valid for user: " + claims.getSubject() +
                    " with userId: " + claims.get("userId", String.class));

//            Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) claims.get("roles", Collection.class);

            List<Map<String, String>> roles = claims.get("roles", List.class);

            Collection<GrantedAuthority> authorities = roles.stream()
                    .map(roleMap -> new SimpleGrantedAuthority(roleMap.get("authority")))
                    .collect(Collectors.toList());

            var authentication = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(), null, authorities);
//             Set the authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomRequestWrapper requestWrapper = new CustomRequestWrapper(request, claims.get("userId", String.class));


            filterChain.doFilter(requestWrapper, response);


        } catch (JwtException | IllegalArgumentException e) {

            logger.warning("JWT token is invalid: " + e.getMessage());
            response.sendError( HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;

        }


    }
}
