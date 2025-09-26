package gng.learning.userapi.security;

import gng.learning.userapi.services.UserDataService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private UserDataService userDataService;
    public static final String SECRET_KEY = "your-256-bit-secret-your-256-bit-secret";
    private static final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds

    private final Key signingKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private Claims claims;


//    public JwtUtil(UserDataService userDataService) {
//        this.userDataService = userDataService;
//    }


    public String generateToken(CustomUser userDetails) {

        logger.debug("Generating JWT token for user: " + userDetails.getUsername());
        logger.debug("userDetails class =: " + userDetails.getClass().getName());

        if (userDetails == null || userDetails.getUsername() == null) {
            throw new IllegalArgumentException("User details or username cannot be null");
        }


        Claims claims = new DefaultClaims();
        // set claims
        claims.put("roles", userDetails.getAuthorities()); // set roles in claims
        claims.put("userId", userDetails.getUserId().toString());
        claims.setSubject(userDetails.getUsername()); // set username in claims
        claims.setIssuedAt(new Date()); // set issued date
        claims.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME));

        return Jwts.builder()
                .setClaims(claims) // set claims
                .signWith(signingKey, SignatureAlgorithm.HS256)   // signing the JWT with the key
                .compact();
    }


    public Claims extractAllClaims(String jwtToken) throws IllegalArgumentException {

        if (jwtToken == null || jwtToken.isEmpty()) {
            throw new IllegalArgumentException("JWT token cannot be null or empty");
        }

        try {
            // Parse the JWT token and extract claims
            this.claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token: " + e.getMessage());
        }

        return claims;

    }

    //for






}
