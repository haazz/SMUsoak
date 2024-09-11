package com.smusoak.restapi.models;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MatchingInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int minPartnerAge; // 상대방의 최소 나이 조건

    private int maxPartnerAge; // 상대방의 최대 나이 조건

    private String partnerGender; // 상대방의 성별 조건

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id") // User 엔티티의 id 필드를 참조
    private User user;
}
