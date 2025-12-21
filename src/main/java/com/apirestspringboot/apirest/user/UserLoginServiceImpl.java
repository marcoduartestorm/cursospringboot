package com.apirestspringboot.apirest.user;

import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.apirestspringboot.apirest.repository.UserLoginRepository;

@Service
public class UserLoginServiceImpl implements UserLoginService {

    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
   
    private UserLoginRepository userLoginRepository;
    
    public UserLoginServiceImpl(UserLoginRepository userLoginRepository) {
    	this.userLoginRepository = userLoginRepository;
    }

    @Override
    public UserLogin create(UserLogin user) {
        Optional<UserLogin> existUser = userLoginRepository.findByUsername(user.getUsername());
        
        if(!existUser.isEmpty()){
            throw new Error("User already exists!");
        }
        
        UserLogin userLogin = new UserLogin();
        
        userLogin.setPassword(passwordEncoder().encode(user.getPassword()));
        userLogin.setUsername(user.getUsername());
        userLogin.setRoles(user.getRoles());
        
        UserLogin createdUser = userLoginRepository.save(userLogin);

        return createdUser;
    }
}
