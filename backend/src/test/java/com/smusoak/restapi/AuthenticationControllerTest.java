package com.smusoak.restapi;

import com.smusoak.restapi.controllers.AuthenticationController;
import com.smusoak.restapi.dto.UserDto;
import com.smusoak.restapi.filters.JwtAuthenticationFilter;
import com.smusoak.restapi.restdocs.AbstractRestDocsTests;
import com.smusoak.restapi.services.AuthenticationService;
import com.smusoak.restapi.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@MockBeans({
        @MockBean(JwtAuthenticationFilter.class),
        @MockBean(AuthenticationService.class)
})
public class AuthenticationControllerTest extends AbstractRestDocsTests {

    @MockBean
    JwtService jwtService;

    @Test
    void MailSendTest() throws Exception {
        UserDto.SendCodeRequest sendAuthCodeDto = new UserDto.SendCodeRequest();
        sendAuthCodeDto.setMail("tmp@sangmyung.kr");

        mockMvc.perform(post("/api/v1/auth/mail/send-code")
                        .content(objectMapper.writeValueAsString(sendAuthCodeDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void MailVerificationTest() throws Exception {
        UserDto.MailVerificationRequest  mailVerificationDto = new UserDto.MailVerificationRequest();
        mailVerificationDto.setMail("tmp@sangmyung.kr");
        mailVerificationDto.setAuthCode("tmp");

        mockMvc.perform(post("/api/v1/auth/mail/verification")
                    .content(objectMapper.writeValueAsString(mailVerificationDto))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void SignupTest() throws Exception {
        UserDto.SignupRequest createUserDto = new UserDto.SignupRequest();
        createUserDto.setMail("tmp@sangmyung.kr");
        createUserDto.setPassword("tmptmp");
        createUserDto.setNickname("tmp");
        createUserDto.setAge(20);
        createUserDto.setGender('M');


        mockMvc.perform(post("/api/v1/auth/signup")
                        .content(objectMapper.writeValueAsString(createUserDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void SigninTest() throws Exception {
        UserDto.SigninRequest signinDto = new UserDto.SigninRequest();
        signinDto.setMail("tmp@sangmyung.kr");
        signinDto.setPassword("tmptmp");;

        mockMvc.perform(post("/api/v1/auth/signin")
                        .content(objectMapper.writeValueAsString(signinDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
