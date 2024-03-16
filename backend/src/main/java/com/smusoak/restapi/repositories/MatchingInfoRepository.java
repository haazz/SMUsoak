package com.smusoak.restapi.repositories;
import com.smusoak.restapi.models.MatchingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface MatchingInfoRepository extends JpaRepository<MatchingInfo, Long> {
//    List<MatchingInfo> findAllByOrderByCreatedDateAsc();
    List<MatchingInfo> findAll();
}
