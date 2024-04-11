package com.smusoak.restapi.repositories;


import com.smusoak.restapi.models.ChatRoom;
import com.smusoak.restapi.models.User;
import org.apache.catalina.LifecycleState;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByMail(String mail);
    Set<User> findByChatRoomsId(Long id);
}
