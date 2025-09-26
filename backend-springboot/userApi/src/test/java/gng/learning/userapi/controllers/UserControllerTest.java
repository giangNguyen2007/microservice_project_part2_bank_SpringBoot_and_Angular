package gng.learning.userapi.controllers;

import gng.learning.userapi.data.IUserRepository;
import gng.learning.userapi.data.Role;
import gng.learning.userapi.data.UserModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private IUserRepository _userRepository;

    @Autowired
    private UserController _userController;

    @BeforeEach
    void setUp() {

        UserModel sampleUser = new UserModel(null, "testuser", "test@example.com", "password", Role.USER);

        // add this user as base before any test
        _userRepository.save(sampleUser);
    }

    @AfterEach
    void tearDown() {

        // delete all to have a clean db
        _userRepository.deleteAll();
    }

//    @AfterAll
//    void tearDownAll() {
//
//        // delete all to have a clean db
//        _userRepository.deleteAll();
//    }

    @Test
    void getAllUsers() {

        List<UserModel> myUsers = _userController.getAllUsers();
        assertNotNull(myUsers);
        assertEquals(1,myUsers.size());

    }

    @Test
    void createNewUser() {

        ResponseEntity<?> response = _userController.registerNewUser(
                new PostUserDto("giang", "myEmail", "myPassword") );

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        List<UserModel> myUsers = _userController.getAllUsers();
        assertNotNull(myUsers);
        assertEquals(2,myUsers.size());
    }

//    @Test
//    void getUserByEmail_ValidEmail() {
//
//        ResponseEntity<?> response = _userController.getUserByEmail(
//                "test@example.com" );
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        //assertEquals(HttpStatus.OK, response.getBody());
//
//    }

//    @Test
//    void getUserByEmail_InValidEmail() {
//        ResponseEntity<?> response = _userController.getUserByEmail(
//                "invalid@example.com" );
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//
//    }
}