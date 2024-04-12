package com.smusoak.restapi.repositories;

import com.smusoak.restapi.models.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // userMail을 포함하는 모든 채팅방 정보를 가져옴
    @Query("SELECT cr.id, " +
            "       GROUP_CONCAT(u.mail) AS userMails " +
            "FROM ChatRoom cr " +
            "LEFT JOIN cr.users u " +
            "GROUP BY cr.id " +
            "HAVING EXISTS (SELECT 1 FROM cr.users u WHERE u.mail = :userMail)")
    List<Object[]> findByUserMail(@Param(value = "userMail") String userMail);
}
