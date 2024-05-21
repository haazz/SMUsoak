package com.smusoak.restapi.controllers;
import com.smusoak.restapi.dto.*;
import com.smusoak.restapi.models.*;
import com.smusoak.restapi.response.ApiResponseEntity;
import com.smusoak.restapi.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/openChatRoom")
public class OpenChatRoomController {
    private final OpenChatRoomService openChatRoomService;

    //이메일로 조회(오류)
    @GetMapping("/open_chat/creator_mail/{creatorMail}")
    public ResponseEntity<ApiResponseEntity> getOpenChatsByCreatorMail(@PathVariable String creatorMail) {
        List<OpenChat> openChats = openChatRoomService.getOpenChatsByCreatorMail(creatorMail);
        return ApiResponseEntity.toResponseEntity(openChats);
    }

    @GetMapping("/group_chat/creator_mail/{creatorMail}")
    public ResponseEntity<ApiResponseEntity> getGroupChatsByCreatorMail(@PathVariable String creatorMail) {
        List<OpenGroupChat> groupChats = openChatRoomService.getGroupChatsByCreatorMail(creatorMail);
        return ApiResponseEntity.toResponseEntity(groupChats);
    }

    //전체 조회(오류)
    @GetMapping("/open_chat/all")
    public ResponseEntity<ApiResponseEntity> getAllOpenChats() {
        List<OpenChat> openChats = openChatRoomService.getAllOpenChats();
        return ApiResponseEntity.toResponseEntity(openChats);
    }

    @GetMapping("/group_chat/all")
    public ResponseEntity<ApiResponseEntity> getAllGroupChats() {
        List<OpenGroupChat> groupChats = openChatRoomService.getAllGroupChats();
        return ApiResponseEntity.toResponseEntity(groupChats);
    }


    //삭제(테스트 완료)
    @DeleteMapping("/open_chat/delete/{chatId}")
    public ResponseEntity<ApiResponseEntity> deleteOpenChatByCreatorMail(@RequestParam String creatorMail, @PathVariable Long chatId) {
        openChatRoomService.deleteOpenChatByCreatorMail(creatorMail, chatId);
        String message = "1:1 chat room with ID " + chatId + " created by user with email " + creatorMail + " has been deleted successfully.";
        return ApiResponseEntity.toResponseEntity(message);
    }

    @DeleteMapping("/group_chat/delete/{chatId}")
    public ResponseEntity<ApiResponseEntity> deleteGroupChatByCreatorMail(@RequestParam String creatorMail, @PathVariable Long chatId) {
        openChatRoomService.deleteGroupChatByCreatorMail(creatorMail, chatId);
        String message = "group chat room with ID " + chatId + " created by user with email " + creatorMail + " has been deleted successfully.";
        return ApiResponseEntity.toResponseEntity(message);
    }

    //생성(테스트 완료)
    @PostMapping("/open_chat/create")
    public ResponseEntity<ApiResponseEntity> createOpenChat(@RequestBody OpenChatDto.OneToOneRequest request) {
        Long chatId = openChatRoomService.createOpenChat(request);
        return ApiResponseEntity.toResponseEntity(OpenChatDto.OneToOneResponse.builder().chatId(chatId).build());
        // 리스폰즈 해놓은거에서 보기
    }

    @PostMapping("/group_chat/create")
    public ResponseEntity<ApiResponseEntity> createGroupChat(@RequestBody OpenGroupChatDto.GroupRequest request) {
        Long chatId = openChatRoomService.createGroupChat(request);
        return ApiResponseEntity.toResponseEntity(OpenGroupChatDto.GroupResponse.builder().chatId(chatId).build());
    }

}




//{
//  "title": "hi",
//  "description": "nice",
//  "mail": "smusoak@gmail.com",
//  "createdAt": ""
//}
//ALTER TABLE user_mail DROP FOREIGN KEY FKrx1maikvwr8s9qqayl3blfu1w;

//"timestamp": "2024-04-19T08:40:10.127+00:00",
//    "status": 404,
//    "error": "Not Found"

//선택적인 쿼리 파라미터를 받을 때는 @RequestParam을 사용하고,
// 경로 일부를 동적으로 처리해야 할 때는 @PathVariable을 사용하는 것이 좋음
//dto를 짜서 requestbody를 사용할지, 지원이가 한걸로 비교하기
// 클래스를 짜서 dto를 한번에 넘기기
//http://localhost:8080/openChatRoom/group_chat/delete/1?creatorMail=smusoak@gmail.com