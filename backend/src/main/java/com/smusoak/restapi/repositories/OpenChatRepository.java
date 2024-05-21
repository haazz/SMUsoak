package com.smusoak.restapi.repositories;

import com.smusoak.restapi.models.OpenChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OpenChatRepository extends JpaRepository<OpenChat, Long> {
//    @Query("SELECT * FROM open_chat JOIN user")
    List<OpenChat> findByCreatorMail(String mail);


    Optional<OpenChat> findByIdAndCreatorId(Long chatId, Long id);
    //다 수정 api 레파지토리
}
