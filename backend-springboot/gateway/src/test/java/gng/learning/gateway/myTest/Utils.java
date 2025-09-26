package gng.learning.gateway.myTest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

import static org.junit.jupiter.api.Assertions.fail;

public class Utils {


    static Claims ParseJwtToken(String jwtToken, String secretKey) throws JwtException {

        Key signingKey = Keys.hmacShaKeyFor(secretKey.getBytes());

        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(jwtToken)
                .getBody();

        return claims;

    }
}
