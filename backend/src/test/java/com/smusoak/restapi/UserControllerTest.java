package com.smusoak.restapi;

import com.smusoak.restapi.controllers.TestController;
import com.smusoak.restapi.controllers.UserController;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@MockBeans({
        @MockBean(JwtAuthenticationFilter.class),
        @MockBean(UserService.class)
})
public class UserControllerTest extends AbstractRestDocsTests {

    @MockBean
    JwtService jwtService;

    @Test
    void UpdateUserDetailsTest() throws Exception {
        String mail = "test@sangmyung.kr";

        UserDto.UpdateUserDetailsRequest updateUserDetailsRequest = new UserDto.UpdateUserDetailsRequest();
        updateUserDetailsRequest.setMail("test@sangmyung.kr");
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
                .andDo(MockMvcResultHandlers.print());
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
                .andExpect(status().isOk());
    }

    @Test
    void GetUserImgTest() throws Exception {
        ImgDto.UserImgRequest userImgRequest = new ImgDto.UserImgRequest();
        List<String> mailList = new ArrayList<>();
        userImgRequest.setMail("tmp@sangmyung.kr");
        mailList.add("tmp1@sangmyung.kr");
        mailList.add("tmp2@sangmyung.kr");
        mailList.add("tmp3@sangmyung.kr");
        userImgRequest.setMailList(mailList);

        mockMvc.perform(post("/api/v1/user/imgs")
                        .header("Authorization", "Bearer " +
                                jwtService.generateToken(User
                                        .builder()
                                        .mail("tmp")
                                        .build()))
                        .content(objectMapper.writeValueAsString(userImgRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        )
                .andExpect(status().isOk());
    }
}
