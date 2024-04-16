package com.smusoak.restapi.repositories;

import com.smusoak.restapi.models.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserDetailRepository extends JpaRepository<UserDetail, Long> {
    @Query("SELECT u.userDetail FROM User u WHERE u.mail = :mail")
    Optional<UserDetail> findByUserMail(String mail);
    Optional<UserDetail> findByNickname(String nickname);
}
