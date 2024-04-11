package com.smusoak.restapi.repositories;

import com.smusoak.restapi.models.ChatRoom;
import com.smusoak.restapi.models.Message;
import com.smusoak.restapi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByUsersMail(String mail);
}
