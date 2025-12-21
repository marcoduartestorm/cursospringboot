package com.apirestspringboot.apirest.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.apirestspringboot.apirest.user.UserLogin;

public interface UserLoginRepository extends JpaRepository<UserLogin, UUID>{
	Optional<UserLogin> findByUsername(String username);
}
