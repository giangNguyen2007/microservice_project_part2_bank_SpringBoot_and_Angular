package gng.learning.userapi.controllers;


import gng.learning.sharedLibrary.userController.LoginResponseDto;
import gng.learning.userapi.data.Role;
import gng.learning.userapi.data.UserModel;
import gng.learning.userapi.security.CustomUser;
import gng.learning.userapi.security.JwtUtil;
import gng.learning.userapi.services.UserDataService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "user")
public class UserController {

    private final UserDataService _userService;

    private final AuthenticationManager _authenticationManager;
    private final JwtUtil _jwtUtil ;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private BCryptPasswordEncoder _passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserController(UserDataService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        _userService = userService;
        _authenticationManager = authenticationManager;
        _jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<UserModel> getAllUsers(){
        return _userService.getAllUser();
    }


    // not filtered by JwtFilter
    @PostMapping(path = "/register")
    public ResponseEntity<?> registerNewUser(@Valid @RequestBody PostUserDto postUserDto){

        if (_userService.findByEmail(postUserDto.email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        }

        String hashedPassword = _passwordEncoder.encode(postUserDto.password);


        UserModel newUser = new UserModel(null, postUserDto.name, postUserDto.email, hashedPassword, Role.USER);
        try {
            _userService.createUser(newUser);


            logger.info("registration successful for new user =" + newUser.getName());

            return new ResponseEntity<>(newUser, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {

            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected internal server error occurred ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // not filtered by JwtFilter
    @PostMapping(path = "/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody PostUserDto postUserDto){

        try {

            logger.info("authenticationManagerType = " + _authenticationManager.getClass().getName());

            Authentication authentication = _authenticationManager.authenticate(
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            postUserDto.name, postUserDto.password
                    )
            );

//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            CustomUser userDetails = (CustomUser) authentication.getPrincipal();

            String jwtToken = _jwtUtil.generateToken(userDetails);

            LoginResponseDto loginResponseDto = new LoginResponseDto(jwtToken, userDetails.getUsername(), userDetails.getUserId().toString() , userDetails.getAuthorities().toString());

            logger.info("login successful for user =" + userDetails.getUsername() + ", token =" + jwtToken);

            return new ResponseEntity<>(loginResponseDto, HttpStatus.OK);

        } catch (AuthenticationException e) {

            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred" , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> getUserByEmail(@RequestParam("email") String email, @AuthenticationPrincipal CustomUser userDetails) {



        logger.info("userId =" + userDetails.getUserId() + " is searching for user with email: " + email);


        try {
            Optional<UserModel> myUser =  _userService.findByEmail(email);

            return myUser.isPresent() ?
                    new ResponseEntity<>(myUser.get(), HttpStatus.OK) :
                    new ResponseEntity<>("email not found", HttpStatus.NOT_FOUND);

        } catch (IllegalArgumentException e) {

            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
