package com.apirestspringboot.apirest.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.apirestspringboot.apirest.dto.CreateUserDto;
import com.apirestspringboot.apirest.dto.UserDto;
import com.apirestspringboot.apirest.model.UserModel;
import com.apirestspringboot.apirest.repository.UserLoginRepository;
import com.apirestspringboot.apirest.service.UserService;
import com.apirestspringboot.apirest.user.UserLogin;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api")
public class UserController {

	private UserService userService;
	private ObjectMapper objectMapper;
	private UserLoginRepository userLoginRepository;
	private BCryptPasswordEncoder passwordEncoder;
	
	public UserController(UserService userService, ObjectMapper objectMapper, UserLoginRepository userLoginRepository,
			BCryptPasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.objectMapper = objectMapper;
		this.userLoginRepository = userLoginRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@PreAuthorize("hasAuthority('SCOPE_ADMIN')")
	@PostMapping("/users")
	public ResponseEntity<Object> saveUser(@RequestBody @Valid UserDto userDto) {
		if(userService.existsByModelCarAndCelNumber(userDto.getModelCar(), userDto.getCelNumber())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: user with modelCar and celNumber already exists in database.");
		}
		
		UserModel userModel = new UserModel();
		BeanUtils.copyProperties(userDto, userModel);
		userModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
		
		return ResponseEntity.status(HttpStatus.OK).body(userService.save(userModel));
	}
	
	@PreAuthorize("hasAuthority('SCOPE_USER')")
	@GetMapping("/users")
	public ResponseEntity<Object> getUsers(@PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable){
		return ResponseEntity.status(HttpStatus.OK).body(userService.retornaTodos(pageable));
	}
	
	@PreAuthorize("hasAuthority('SCOPE_ADMIN')")
	@GetMapping("/users/{id}")
	public ResponseEntity<Object> returnUserById(@PathVariable UUID id) {
		Optional<UserModel> userOptional = userService.findById(id);
		
		if(userOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
		}
		
		UserModel userModel = new UserModel();
		userModel = userOptional.get();
		
		return ResponseEntity.status(HttpStatus.OK).body(userModel);
	}
	
	@PreAuthorize("hasAuthority('SCOPE_ADMIN')")
	@PutMapping("/users/{id}")
	public ResponseEntity<Object> atualizeUser(@PathVariable UUID id, @RequestBody @Valid UserDto userDto) {
		Optional<UserModel> userOptional = userService.findById(id);
		
		if(userOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found in database.");
		}
		
		UserModel userModel = userOptional.get();
		BeanUtils.copyProperties(userDto, userModel);
		
		return ResponseEntity.status(HttpStatus.OK).body(userService.save(userModel));
	}
	
	@PreAuthorize("hasAuthority('SCOPE_ADMIN')")
	@PatchMapping("/users/{id}")
	public ResponseEntity<Object> atualizePartialUser(@PathVariable UUID id, @RequestBody Map<String, Object> camposAtualizados) throws Exception {
		Optional<UserModel> userOptional = userService.findById(id);
		
		if(userOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found in database.");
		}
		
		UserModel userModel = new UserModel();
		userModel = userOptional.get();
		objectMapper.updateValue(userModel, camposAtualizados);
		
		return ResponseEntity.status(HttpStatus.OK).body(userService.save(userModel));
	}
	
	@PreAuthorize("hasAuthority('SCOPE_ADMIN')")
	@DeleteMapping("/users/{id}")
	public ResponseEntity<Object> deleteUser(@PathVariable UUID id){
		Optional<UserModel> userOptional = userService.findById(id);
		
		if(userOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
		}
		
		UserModel userModel = userOptional.get();
		userService.deletarUser(userModel);
		
		return ResponseEntity.status(HttpStatus.OK).body("User deleted.");
	}
	
	@Transactional
    @PostMapping("/usersLogin")
    public ResponseEntity<Void> newUser(@RequestBody CreateUserDto dto) {

        var userFromDb = userLoginRepository.findByUsername(dto.getUsername());
        if (userFromDb.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        var user = new UserLogin();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(dto.getRoles());

        userLoginRepository.save(user);

        return ResponseEntity.ok().build();
    }
	
	@GetMapping("/usersLogin")
	@PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<UserLogin>> listUsers() {
        var users = userLoginRepository.findAll();
        return ResponseEntity.ok(users);
    }
}
