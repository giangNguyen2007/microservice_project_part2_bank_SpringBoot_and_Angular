package gng.learning.userapi.data;

import gng.learning.userapi.services.UserDataService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserDataServiceTest   {

    @Autowired
    IUserRepository _userRepository;

    @Autowired
    private UserDataService _userDataService;

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

    @Test
    void createUser() {

        UserModel sampleUser2 = new UserModel(null, "rairacer", "rairacer@example.com", "password", Role.USER);
        _userDataService.createUser(sampleUser2);

        List<UserModel> myUsers = _userDataService.getAllUser();
        assertNotNull(myUsers);
        assertEquals(2, myUsers.size());
    }

    @Test
    void getAllUser() {

        List<UserModel> myUsers = _userDataService.getAllUser();
        assertNotNull(myUsers);
        assertEquals(1, myUsers.size());
    }

    @Test
    void findByEmail_validEmail() {

        Optional<UserModel> myUsers = _userDataService.findByEmail("test@example.com");
        assertTrue(myUsers.isPresent());
        assertEquals("testuser", myUsers.get().getName());
    }

    @Test
    void findByEmail_invalidEmail() {

        Optional<UserModel> myUsers = _userDataService.findByEmail("myEmail");
        assertFalse(myUsers.isPresent());
    }

    @Test
    void findByName_validName() {

        Optional<UserModel> myUsers = _userDataService.findByName("testuser");
        assertTrue(myUsers.isPresent());
        assertEquals(Role.USER, myUsers.get().getRole());
    }

    @Test
    void findByName_invalidName() {

        Optional<UserModel> myUsers = _userDataService.findByName("nonexistent");
        assertFalse(myUsers.isPresent());
    }


}


