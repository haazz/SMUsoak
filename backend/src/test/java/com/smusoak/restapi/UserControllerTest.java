package com.smusoak.restapi;

import com.smusoak.restapi.controllers.TestController;
import com.smusoak.restapi.controllers.UserController;
import com.smusoak.restapi.dto.ChatDto;
import com.smusoak.restapi.dto.ImgDto;
import com.smusoak.restapi.dto.UserDto;
import com.smusoak.restapi.filters.JwtAuthenticationFilter;
import com.smusoak.restapi.models.User;
import com.smusoak.restapi.restdocs.AbstractRestDocsTests;
import com.smusoak.restapi.services.AuthenticationService;
import com.smusoak.restapi.services.JwtService;
import com.smusoak.restapi.services.RedisService;
import com.smusoak.restapi.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
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

@WebMvcTest(UserController.class)
@MockBeans({
        @MockBean(JwtAuthenticationFilter.class)
})
public class UserControllerTest extends AbstractRestDocsTests {

    @MockBean
    JwtService jwtService;
    @MockBean
    UserService userService;

    @Test
    void UpdateUserDetailsTest() throws Exception {
        String mail = "test@sangmyung.kr";

        UserDto.UpdateUserDetailsRequest updateUserDetailsRequest = new UserDto.UpdateUserDetailsRequest();
        updateUserDetailsRequest.setMail("test@sangmyung.kr");
        updateUserDetailsRequest.setNickname("test");
        updateUserDetailsRequest.setAge(20);
        updateUserDetailsRequest.setGender('M');
        updateUserDetailsRequest.setMbti("MBTI");

        mockMvc.perform(post("/api/v1/user/update/info")
                        .header("Authorization", "Bearer " +
                                jwtService.generateToken(User
                                        .builder()
                                        .mail("tmp@sangmyung.kr")
                                        .build()))
                        .content(objectMapper.writeValueAsString(updateUserDetailsRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("mail").type(JsonFieldType.STRING).description("@sangmyung.kr로 끝나는 메일"),
                                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("nickname").optional(),
                                        fieldWithPath("age").type(JsonFieldType.NUMBER).description("나이").optional(),
                                        fieldWithPath("gender").type(JsonFieldType.STRING).description("M 혹은 W Charter").optional(),
                                        fieldWithPath("mbti").type(JsonFieldType.STRING).description("MBTI").optional()
                                ),
                                responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("반환되는 데이터 없음")
                                )
                        )
                );
    }

    @Test
    void UpdateUserImgTest() throws Exception {
        ImgDto.UpdateUserImgRequest updateUserImgRequest = new ImgDto.UpdateUserImgRequest();
        updateUserImgRequest.setMail("tmp@sangmyung.kr");

        MockMultipartFile file = new MockMultipartFile("file", "tmp.png", "multipart/form-data",
                "uploadFile".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile info = new MockMultipartFile("info", null, "application/json",
                objectMapper.writeValueAsString(updateUserImgRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/user/update/img")
                        .file(file)
                        .file(info)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " +
                                jwtService.generateToken(User
                                        .builder()
                                        .mail("tmp")
                                        .build())
                        )
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestParts(
                                        partWithName("file").description("최대 용량 5MB 이미지 파일"),
                                        partWithName("info").description("이미지 정보 (JSON)")
                                ),
                                requestPartFields("info",
                                        fieldWithPath("mail").type(JsonFieldType.STRING).description("@sangmyung.kr로 끝나는 메일"),
                                        fieldWithPath("file").type(JsonFieldType.NULL).description("NULL입력")
                                ),
                                responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("반환되는 데이터 없음")
                                )
                        )
                );
    }

    @Test
    void CheckNicknameTest() throws Exception {
        // given
        given(userService.checkDuplicatiedNickname(any(String.class)))
                .willReturn(true);

        mockMvc.perform(get("/api/v1/user/check/nickname/{nickname}", "test"))
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
                                ).andWithPrefix("data.",
                                        fieldWithPath("available").type(JsonFieldType.BOOLEAN).description("닉네임 사용 가능 여부 (true = 사용가능 / false = 불가능)")
                                )
                        )
                );
    }

    @Test
    void GetUserInfoTest() throws Exception {
        UserDto.UserInfoRequest userInfoRequest = new UserDto.UserInfoRequest();

        List<String> mailList = new ArrayList<>();
        mailList.add("tmp1@sangmyung.kr");
        mailList.add("tmp2@sangmyung.kr");
        mailList.add("tmp3@sangmyung.kr");
        userInfoRequest.setMailList(mailList);

        // given
        List<UserDto.UserInfoResponse> userInfoResponses = new ArrayList<>();
        for(String mail : userInfoRequest.getMailList()) {
            userInfoResponses.add(UserDto.UserInfoResponse.builder()
                    .mail(mail)
                    .nickname(mail.substring(0, 4))
                    .age(20)
                    .gender('M')
                    .mbti("ENTP")
                    .imgUrl("URL")
                    .imgType("imge/jpeg")
                    .build());
        }
        given(userService.getUserInfo(any(UserDto.UserInfoRequest.class)))
                .willReturn(userInfoResponses);

        mockMvc.perform(post("/api/v1/user/info")
                        .header("Authorization", "Bearer " +
                                jwtService.generateToken(User
                                        .builder()
                                        .mail("tmp")
                                        .build()))
                        .content(objectMapper.writeValueAsString(userInfoRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("mailList").type(JsonFieldType.ARRAY).description("정보를 가져오고 싶은 mail list")
                                ),
                                responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
                                ).andWithPrefix("data.[].",
                                        fieldWithPath("mail").type(JsonFieldType.STRING).description("이미지 메일"),
                                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("nickname"),
                                        fieldWithPath("age").type(JsonFieldType.NUMBER).description("나이"),
                                        fieldWithPath("gender").type(JsonFieldType.STRING).description("M 혹은 W Charter"),
                                        fieldWithPath("mbti").type(JsonFieldType.STRING).description("MBTI"),
                                        fieldWithPath("imgUrl").type(JsonFieldType.STRING).description("이미지 다운로드 링크"),
                                        fieldWithPath("imgType").type(JsonFieldType.STRING).description("이미지 타입")
                                )
                        )
                );
    }
}
