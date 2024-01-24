package com.smusoak.restapi.repositories;

import java.util.Optional;

import com.smusoak.restapi.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long>{
	Optional<Users> findByMail(String mail);
}
