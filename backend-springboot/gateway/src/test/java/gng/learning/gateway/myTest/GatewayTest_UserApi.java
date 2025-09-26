package gng.learning.gateway.myTest;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class GatewayTest_UserApi {

    private final String BASE_URL = "http://localhost:8085/user";


    private PostUserDto _postUserDto; // DTO for seeding one user at setup

    private RestTemplate restTemplate = new RestTemplate();
    private String jwtToken;


    @BeforeEach
    void setUp() {

        // login to get a JWT token
        String url = BASE_URL + "/login";

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

    @AfterEach
    void tearDown() {

    }


    // create test with valid user authentication in header
    @Test
    void getAllUsers_withValidJwt() {

        String url = BASE_URL;

        HttpHeaders headers = new HttpHeaders();
        //headers.setBasicAuth(_postUserDto.name, _postUserDto.password);
        headers.setBearerAuth(jwtToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        System.out.println("Response: " + response.getBody());

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    void getAllUsers_withoutValidJwt() {

        String url = BASE_URL;

        try {
            // Make a GET request without authentication

            HttpHeaders headers = new HttpHeaders();
            //headers.setBearerAuth(jwtToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            fail("Expected unauthorized exception");
        } catch (org.springframework.web.client.HttpClientErrorException ex) {
            // Assert that the response status is 401 Unauthorized
            assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        }

    }

//  ======= test on path "/search" ======

    @Test
    void searchByEmail_withValidJwtToken() {

        String url = BASE_URL + "/search?email={email}"; ;

        // Make a GET request without authentication
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, _postUserDto.email);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Expected status code 200");

    }

}
