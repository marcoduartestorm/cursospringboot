package com.apirestspringboot.apirest.user;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/register")
public class UserLoginController {

    private UserLoginService userService;

    public UserLoginController(UserLoginService userService) {
    	this.userService = userService; 
    }
    
    @PostMapping
    public UserLogin create(@RequestBody UserLogin user){  	
        return userService.create(user);
    }
}
