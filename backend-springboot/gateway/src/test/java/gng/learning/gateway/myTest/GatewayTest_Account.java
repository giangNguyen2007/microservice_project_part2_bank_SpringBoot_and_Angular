package gng.learning.gateway.myTest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

//@SpringBootTest(
//        // run the application on a random port
//        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
//)
class GatewayTest_Account {

    private static final String SECRET_KEY = "your-256-bit-secret-your-256-bit-secret";

    // This is the port that the application will run on during tests
//    @LocalServerPort
//    private int port;
    private static String BASE_URL = "http://localhost:8085/account";


    private RestTemplate restTemplate = new RestTemplate();

    private PostUserDto _postUserDto; // DTO for seeding one user at setup

    private String jwtToken;

    @BeforeEach
    void setUp() {

        // login to get a JWT token
        String url = "http://localhost:8085/user/login";

        _postUserDto = new PostUserDto(  "giangNguyen",  "giangNguyen@gmail.com", "myPassword");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PostUserDto> entity = new HttpEntity<>(_postUserDto, headers);

        ResponseEntity<String> response = restTemplate.postForEntity( url, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        jwtToken = response.getBody();

        //System.out.println("JWT Token: " + jwtToken);

        try {
            // Parse the token
            Claims claims = Utils.ParseJwtToken(jwtToken, "your-256-bit-secret-your-256-bit-secret");

            // Verify the username
            assertEquals(_postUserDto.name, claims.getSubject() , "Username does not match!");


        } catch (Exception e) {
            fail("Failed to parse or verify the token: " + e.getMessage());
        }
    }

    @Test
    void getAllAccount_validToken() {
        // Define the endpoint URL
        String url = BASE_URL ;

        HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken); // Set the JWT token in the Authorization header
        HttpEntity<String> entity = new HttpEntity<>(headers);
        // Send a GET request
        // parse body into String
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        // Assert the response status and body
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody(), "Response body should not be null!");

    }





}