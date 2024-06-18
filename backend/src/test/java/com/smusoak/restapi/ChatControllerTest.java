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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        given(chatService.getChatRoomList(any(String.class)))
                .willReturn(chatRoomInfos);

        mockMvc.perform(get("/api/v1/chat/room/list/{mail}", mails.get(0))
                        .header("Authorization", "Bearer token")
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
                                ).andWithPrefix("data.[].",
                                        fieldWithPath("roomId").type(JsonFieldType.NUMBER).description("채팅방 ID"),
                                        fieldWithPath("mails").type(JsonFieldType.ARRAY).description("채팅방에 참여 중인 메일 리스트")
                                )
                        )
                );
    }

    @Test
    void UpdateChatImgTest() throws Exception {
        ImgDto.UpdateChatImgRequest updateChatImgRequest = new ImgDto.UpdateChatImgRequest();
        updateChatImgRequest.setRoomId("0");

        MockMultipartFile file = new MockMultipartFile("file", "tmp.png", "multipart/form-data",
                "uploadFile".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile info = new MockMultipartFile("info", null, "application/json",
                objectMapper.writeValueAsString(updateChatImgRequest).getBytes(StandardCharsets.UTF_8));

        given(chatService.updateImg(any(String.class), any(MultipartFile.class)))
                .willReturn("http://baseurl/download/img/tmp");

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/chat/update/img")
                        .file(file)
                        .file(info)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer ")
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestParts(
                                        partWithName("file").description("최대 용량 5MB 이미지 파일"),
                                        partWithName("info").description("이미지 정보 (JSON)")
                                ),
                                requestPartFields("info",
                                        fieldWithPath("roomId").type(JsonFieldType.STRING).description("채팅방번호")
                                ),
                                responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
                                ).andWithPrefix("data.",
                                        fieldWithPath("downloadUrl").type(JsonFieldType.STRING).description("업데이트한 이미지 다운로드 url")
                                )
                        )
                );
    }
}
