package com.smusoak.restapi.dto;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

public class OpenChatDto {

    @Getter
    @Data
    public static class OneToOneRequest {
        private String title;
        private String description;

        private String mail;
        private LocalDateTime createdAt;

        private String participant;
    }
    @Builder
    @Data
    public static class OneToOneResponse {
        private Long chatId;
    }
}
// 이번주 굼요일 7시전까지
// 1. 오픈그룹챗과 chatroom 1대1로 연결 후 생성된 채팅방에 들어오는 인원수마다 조인하게 해주기(챗룸에 구현되어 있음)
// 2. 오픈 1대1챗은 사람이 조인할 때마다 채팅방 생성
// 3. 이름도 다 바꿔주기 OpenChat,OpenGroupChat
// 4. LocalDateTime createdAt; 만든 시간 추가
// 5. 전체 리스트 한번에 조회하는거 만들기
// 6. 해당 dto 이름 변경 및 추가