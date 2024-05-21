package com.smusoak.restapi.repositories;

import com.smusoak.restapi.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OpenGroupChatRepository extends JpaRepository<OpenGroupChat, Long> {
    List<OpenGroupChat> findByCreatorMail(String mail);
    Optional<OpenGroupChat> findByIdAndCreatorId(Long chatId, Long id);
}
