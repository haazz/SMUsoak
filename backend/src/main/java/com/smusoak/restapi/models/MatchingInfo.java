package com.smusoak.restapi.models;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import java.util.Objects;

// 유저를 폴린키로 or id를 폴린키로?
@Entity
@Getter
@Setter
public class MatchingInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int minPartnerAge; // 상대방의 최소 나이 조건
    private int maxPartnerAge; // 상대방의 최대 나이 조건
    private char partnerGender; // 상대방의 성별 조건

    @ManyToOne
    @JoinColumn(name = "user_mail")
    private User user;
}

