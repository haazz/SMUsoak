package com.smusoak.restapi;

import com.smusoak.restapi.controllers.ChatController;
import com.smusoak.restapi.controllers.TestController;
import com.smusoak.restapi.dto.ChatDto;
import com.smusoak.restapi.dto.ImgDto;
import com.smusoak.restapi.filters.JwtAuthenticationFilter;
import com.smusoak.restapi.models.User;
import com.smusoak.restapi.restdocs.AbstractRestDocsTests;
import com.smusoak.restapi.services.ChatService;
import com.smusoak.restapi.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
@MockBeans({
        @MockBean(JwtAuthenticationFilter.class),
        @MockBean(SimpMessagingTemplate.class)
})
public class ChatControllerTest extends AbstractRestDocsTests {

    @MockBean
    JwtService jwtService;
    @MockBean
    ChatService chatService;

    @Test
    void ChatRoomListTest() throws Exception {
        ChatDto.ChatRoomListRequest chatRoomListRequest = new ChatDto.ChatRoomListRequest();
        chatRoomListRequest.setMail("tmp@sangmyung.kr");

        // given
        List<String> mails = new ArrayList<>();
        mails.add("tmp@sangmyung.kr");
        mails.add("tmp1@sangmyung.kr");
        List<ChatDto.ChatRoomInfo> chatRoomInfos = new ArrayList<>();
        chatRoomInfos.add(ChatDto.ChatRoomInfo.builder()
                .roomId(1L)
                .mails(mails)
                .build());
        mails.add("tmp2@sangmyung.kr");
        chatRoomInfos.add(ChatDto.ChatRoomInfo.builder()
                .roomId(2L)
                .mails(mails)
                .build());
        given(chatService.getChatRoomList(any(ChatDto.ChatRoomListRequest.class)))
                .willReturn(chatRoomInfos);

        mockMvc.perform(get("/api/v1/chat/room/list")
                        .header("Authorization", "Bearer token")
                        .content(objectMapper.writeValueAsString(chatRoomListRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("mail").type(JsonFieldType.STRING).description("@sangmyung.kr로 끝나는 메일")
                                ),
                                responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
                                ).andWithPrefix("data.[].",
                                        fieldWithPath("roomId").type(JsonFieldType.NUMBER).description("채팅방 ID"),
                                        fieldWithPath("mails").type(JsonFieldType.ARRAY).description("채팅방에 참여 중인 메일 리스트")
                                )
                        )
                );
    }
}
