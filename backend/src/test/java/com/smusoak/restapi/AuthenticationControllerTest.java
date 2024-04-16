package com.smusoak.restapi;

import com.smusoak.restapi.controllers.AuthenticationController;
import com.smusoak.restapi.dto.JwtAuthenticationResponse;
import com.smusoak.restapi.dto.UserDto;
import com.smusoak.restapi.filters.JwtAuthenticationFilter;
import com.smusoak.restapi.response.ApiResponseEntity;
import com.smusoak.restapi.restdocs.AbstractRestDocsTests;
import com.smusoak.restapi.services.AuthenticationService;
import com.smusoak.restapi.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@MockBeans({
        @MockBean(JwtAuthenticationFilter.class)
})
public class AuthenticationControllerTest extends AbstractRestDocsTests {

    @MockBean
    JwtService jwtService;
    @MockBean
    AuthenticationService authenticationService;

    @Test
    void MailSendTest() throws Exception {
        UserDto.SendCodeRequest sendAuthCodeDto = new UserDto.SendCodeRequest();
        sendAuthCodeDto.setMail("tmp@sangmyung.kr");

        mockMvc.perform(post("/api/v1/auth/mail/send-code")
                        .content(objectMapper.writeValueAsString(sendAuthCodeDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("mail").type(JsonFieldType.STRING).description("@sangmyung.kr로 끝나는 메일")
                                ),
                                responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("반환되는 데이터 없음")
                                )
                        )
                );
    }

    @Test
    void MailVerificationTest() throws Exception {
        UserDto.MailVerificationRequest  mailVerificationDto = new UserDto.MailVerificationRequest();
        mailVerificationDto.setMail("tmp@sangmyung.kr");
        mailVerificationDto.setAuthCode("tmp");

        mockMvc.perform(post("/api/v1/auth/mail/verification")
                    .content(objectMapper.writeValueAsString(mailVerificationDto))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("mail").type(JsonFieldType.STRING).description("@sangmyung.kr로 끝나는 메일"),
                                        fieldWithPath("authCode").type(JsonFieldType.STRING).description("이메일로 보낸 6자리 인증코드")
                                ),
                                responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("반환되는 데이터 없음")
                                )
                        )
                );
    }

    @Test
    void SignupTest() throws Exception {
        UserDto.SignupRequest createUserDto = new UserDto.SignupRequest();
        createUserDto.setMail("tmp@sangmyung.kr");
        createUserDto.setPassword("tmptmp1234");
        createUserDto.setNickname("tmp");
        createUserDto.setAge(20);
        createUserDto.setGender('M');
        createUserDto.setMail("ENTP");

        given(authenticationService.createUser(any(UserDto.SignupRequest.class)))
                .willReturn("Bearer token");

        mockMvc.perform(post("/api/v1/auth/signup")
                        .content(objectMapper.writeValueAsString(createUserDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("mail").type(JsonFieldType.STRING).description("@sangmyung.kr로 끝나는 메일"),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("8~20자, 영문, 숫자 1개 이상 포함 (특수문자: ~!@#$%^&*()+|=)"),
                                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("nickname").optional(),
                                        fieldWithPath("age").type(JsonFieldType.NUMBER).description("나이").optional(),
                                        fieldWithPath("gender").type(JsonFieldType.STRING).description("M 혹은 W Charter").optional(),
                                        fieldWithPath("mbti").type(JsonFieldType.STRING).description("MBTI").optional()
                                ),
                                responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
                                ).andWithPrefix("data.",
                                        fieldWithPath("token").type(JsonFieldType.STRING).description("JWT 토큰")
                                )
                        )
                );
    }

    @Test
    void SigninTest() throws Exception {
        UserDto.SigninRequest signinDto = new UserDto.SigninRequest();
        signinDto.setMail("tmp@sangmyung.kr");
        signinDto.setPassword("tmptmp");
        signinDto.setFcmToken("fcm token");

        given(authenticationService.signin(any(UserDto.SigninRequest.class)))
                .willReturn("Bearer token");

        mockMvc.perform(post("/api/v1/auth/signin")
                        .content(objectMapper.writeValueAsString(signinDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("mail").type(JsonFieldType.STRING).description("@sangmyung.kr로 끝나는 메일"),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                        fieldWithPath("fcmToken").type(JsonFieldType.STRING).description("FCM 토큰")
                                ),
                                responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
                                ).andWithPrefix("data.",
                                        fieldWithPath("token").type(JsonFieldType.STRING).description("JWT 토큰")
                                )
                        )
                );
    }
}
