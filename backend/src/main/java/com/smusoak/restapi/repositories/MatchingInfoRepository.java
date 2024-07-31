package com.smusoak.restapi.repositories;

import com.smusoak.restapi.models.MatchingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchingInfoRepository extends JpaRepository<MatchingInfo, Long> {

    @Query("SELECT mi FROM MatchingInfo mi " +
            "JOIN mi.user u " +
            "JOIN u.userDetail ud " +
            "WHERE :requestAge BETWEEN mi.minPartnerAge AND mi.maxPartnerAge " +
            "AND mi.partnerGender = :requestGender")
    List<MatchingInfo> findPotentialMatches(
            int requestAge,
            String requestGender
    );


    List<MatchingInfo> findAll();
    Optional<MatchingInfo> findByUserMail(String userMail);
}









