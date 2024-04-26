package com.smusoak.restapi.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Data
@Table(name = "openChat")
public class OpenChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant")
    private User participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator")
    private User creator;
    // 생성자와 참가자의 내용 데이터 베이스에 뜨게 해야함
}
// 하나로 합치기 기본을 1대1
// 그룹 1대1 모델 나눠서
// 모델 2, 서비스 3, 컨트롤러 1, 레파지토리 1
// 오픈챗 생성, 조회, 참가, 삭제, 그룹, 플래그로
// 포스트맨으로 테스트

// 1번 모델 2개로 나누기(onetoone and group) O
// 2번 이에 맞게 컨트롤러 수정 O
// 3번 서비스 3개 완성 시키기(조회, 1대1, 그룹) O
// 4번 조회, 다른사용자가 오픈챗 참가, 만든사람이 오픈챗 삭제 기능 구현 O
// 레파지토리도 다 수정 후 실행 포스트맨 X
// 오픈챗 생성은 각각의 서비스에서 구현 O
//
//LocalDateTime createdAt; 만든 시간 추가
//참가할떄마다 방만들어줌
//CreateUser
//오픈챗 오픈그룹챗 리스트구분해서 한번에 조회할 수 있게
//chatRoom은 null값 리턴해서