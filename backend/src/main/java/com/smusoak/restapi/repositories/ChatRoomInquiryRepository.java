package com.smusoak.restapi.repositories;

import com.smusoak.restapi.models.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomInquiryRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr JOIN cr.users u1 JOIN cr.users u2 " +
            "WHERE u1.mail = :mail1 AND u2.mail = :mail2 " +
            "AND SIZE(cr.users) = 2")
    List<ChatRoom> findChatRoomsByUsers(@Param("mail1") String email1, @Param("mail2") String email2);
}
