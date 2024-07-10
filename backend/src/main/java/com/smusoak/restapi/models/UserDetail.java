package com.smusoak.restapi.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
public class UserDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User detail info
    @Column(unique = true)
    private String nickname;
    private Integer age;
    private Character gender;
    private String mbti;
    private LocalDateTime imgUpdateDate;
}
