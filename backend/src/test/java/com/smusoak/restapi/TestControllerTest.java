package com.smusoak.restapi;

import com.smusoak.restapi.controllers.TestController;
import com.smusoak.restapi.dto.ChatDto;
import com.smusoak.restapi.dto.ImgDto;
import com.smusoak.restapi.dto.TestDto;
import com.smusoak.restapi.filters.JwtAuthenticationFilter;
import com.smusoak.restapi.models.Role;
import com.smusoak.restapi.models.User;
import com.smusoak.restapi.restdocs.AbstractRestDocsTests;
import com.smusoak.restapi.services.ChatService;
import com.smusoak.restapi.services.FirebaseCloudMessageService;
import com.smusoak.restapi.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TestController에 Rest Docs를 만들기 위한 테스트케이스
@WebMvcTest(TestController.class)
@MockBeans({
        @MockBean(JwtAuthenticationFilter.class),
        @MockBean(FirebaseCloudMessageService.class)
})
public class TestControllerTest extends AbstractRestDocsTests {

    @MockBean
    JwtService jwtService;
    @MockBean
    ChatService chatService;

    @Test
    void HelloTest() throws Exception {
        mockMvc.perform(get("/api/v1/test/hello"))
                .andExpect(status().isOk());
    }

    @Test
    void UserTest() throws Exception {
        mockMvc.perform(get("/api/v1/test/user")
                .header("Authorization", "Bearer " +
                        jwtService.generateToken(User
                                .builder()
                                .mail("tmp")
                                .build()))
                )
                .andExpect(status().isOk());
    }

    @Test
    void AdminTest() throws Exception {
        mockMvc.perform(get("/api/v1/test/admin")
                        .header("Authorization", "Bearer " +
                                jwtService.generateToken(User.builder()
                                        .mail("tmp")
                                        .role(Role.ROLE_ADMIN)
                                        .build()))
                )
                .andExpect(status().isOk());
    }

    @Test
    void PutChatRoomTest() throws Exception {
        List<String> mails = new ArrayList<>();
        mails.add("tmp@sangmyung.kr");
        mails.add("tmp1@sangmyung.kr");
        mails.add("tmp2@sangmyung.kr");
        TestDto.ChatRoomRequest chatRoomRequest = new TestDto.ChatRoomRequest();
        chatRoomRequest.setMails(mails);

        // given
        given(chatService.putUserToChatRoom(any(ArrayList.class)))
                .willReturn(1L);

        mockMvc.perform(post("/api/v1/test/chat-room")
                .header("Authorization", "Bearer token")
                .content(objectMapper.writeValueAsString(chatRoomRequest))
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andDo(
                restDocs.document(
                        requestFields(
                                fieldWithPath("mails").type(JsonFieldType.ARRAY).description("@sangmyung.kr로 끝나는 메일 리스트")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
                        ).andWithPrefix("data.",
                                fieldWithPath("chatRoomId").type(JsonFieldType.NUMBER).description("새롯 생성된 채팅방 번호")
                        )
                )
        );
    }
}
