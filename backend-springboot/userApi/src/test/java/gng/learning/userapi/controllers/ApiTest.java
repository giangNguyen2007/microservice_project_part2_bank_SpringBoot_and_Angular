package gng.learning.userapi.controllers;


import gng.learning.userapi.data.IUserRepository;
import gng.learning.userapi.data.Role;
import gng.learning.userapi.data.UserModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,

        properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
        }

)
public class ApiTest {

    private final String BASE_URL = "http://localhost:8081/user";

    @Autowired
    IUserRepository _userRepository;

    @Autowired
    private UserController _userController;

    private PostUserDto _postUserDto; // DTO for seeding one user at setup

    private RestTemplate restTemplate = new RestTemplate();
    private String jwtToken;


    @BeforeEach
    void setUp() {

        _postUserDto = new PostUserDto( "testuser", "test@example.com", "password");
        _userController.registerNewUser(_postUserDto);

        //UserModel sampleUser = new UserModel(null, "testuser", "test@example.com", "password", Role.USER);

        // add this user as base before any test
        //_userRepository.save(sampleUser);

        // login to get a JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PostUserDto> entity = new HttpEntity<>(_postUserDto, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(BASE_URL + "/login", entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        jwtToken = response.getBody();
        
        
    }

    @AfterEach
    void tearDown() {
        // delete all to have a clean db
        _userRepository.deleteAll();

    }

    @Test
    void testUserExistence() {

        Optional<UserModel> myUsers = _userRepository.findByName("testuser");
        assertTrue(myUsers.isPresent());
        assertEquals( "test@example.com" , myUsers.get().getEmail());
        assertEquals( Role.USER , myUsers.get().getRole());
    }


    @Test
    void registerUser_valid() {

        String url = BASE_URL + "/register"; // endpoint does not need authentication

        PostUserDto postUserDto = new PostUserDto("giang", "myEmail", "myPassword");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PostUserDto> entity = new HttpEntity<>(postUserDto, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        System.out.println("Response: " + response.getBody());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("User created successfully"));

        // ensure that the user is created in the database
        Optional<UserModel> createdUser = _userRepository.findByEmail(postUserDto.email);
        assertTrue(createdUser.isPresent());
    }


    @Test
    void registerUser_withDuplicateEmail() {

        String url = BASE_URL + "/register";  // endpoint does not need authentication

        PostUserDto postUserDto = new PostUserDto("duplicateUser", _postUserDto.email, "password");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PostUserDto> entity = new HttpEntity<>(postUserDto, headers);

        try {
            // Make a GET request without authentication
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            fail("Expected unauthorized exception");
        } catch (org.springframework.web.client.HttpClientErrorException ex) {
            assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
            assertTrue(ex.getResponseBodyAsString().contains("User already exists"));
        }

    }



    @Test
    void getAllUsers_withInvalidJwtToken() {

        String url = BASE_URL ;

        try {
            // Make a GET request without authentication
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            fail("Expected unauthorized exception");
        } catch (org.springframework.web.client.HttpClientErrorException ex) {
            assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        }

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

//  ======= test on path "/search" ======

    @Test
    void searchByEmail_withInvalidJwtToken() {

        String url = BASE_URL + "/search?email={email}"; ;

        try {
            // Make a GET request without authentication
            HttpHeaders headers = new HttpHeaders();
            //headers.setBearerAuth(jwtToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, _postUserDto.email);

            fail("Expected unauthorized exception");
        } catch (org.springframework.web.client.HttpClientErrorException ex) {
            assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        }

    }

    @Test
    void searchByEmail_withValidJwtToken() {

        String url = BASE_URL + "/search?email={email}"; ;

        // ensure that the user is created in the database
            // Make a GET request without authentication
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<UserModel> response = restTemplate.exchange(url, HttpMethod.GET, entity, UserModel.class, _postUserDto.email);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals( _postUserDto.name, response.getBody().getName());

    }

}
