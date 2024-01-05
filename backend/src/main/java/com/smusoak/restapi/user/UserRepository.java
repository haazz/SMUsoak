package com.smusoak.restapi.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long>{
	Optional<Users> findByMail(String mail);
}
