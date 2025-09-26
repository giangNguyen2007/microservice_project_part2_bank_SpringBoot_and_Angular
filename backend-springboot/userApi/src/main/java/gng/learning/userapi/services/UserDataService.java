package gng.learning.userapi.services;


import gng.learning.userapi.data.IUserRepository;
import gng.learning.userapi.data.UserModel;
import gng.learning.userapi.security.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserDataService implements UserDetailsService {

    private IUserRepository _userRepository;

    @Autowired
    public UserDataService(IUserRepository userRepository) {
        _userRepository = userRepository;
    }

    public void createUser( UserModel newUser){
        try {
            _userRepository.save(newUser);

        } catch (Exception e){
            throw e;
        }
    }

    public List<UserModel> getAllUser(){
        return _userRepository.findAll();
    }

    public Optional<UserModel> findByEmail(String email){

        return _userRepository.findByEmail(email);
    }

    public Optional<UserModel> findByName(String name){
        return _userRepository.findByName(name);
    }

    public UserModel getUserById(UUID userId){
        UserModel myUser = _userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );

        return myUser;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserModel user = _userRepository.findByName(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found with username: " + username)
        );

        List<GrantedAuthority> authorities = new ArrayList();

        authorities.add(new SimpleGrantedAuthority(user.getRole().name()));

        CustomUser customUser = new CustomUser(
                user.getName(),
                user.getPassword(),
                authorities
        );

        customUser.setUserId(user.getUserId());

        return customUser;

    }
}
