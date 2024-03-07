package com.smusoak.restapi.repositories;

import com.smusoak.restapi.models.ChatRoom;
import com.smusoak.restapi.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatRoomId(Long id);
}
