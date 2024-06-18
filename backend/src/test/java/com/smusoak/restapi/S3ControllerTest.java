package com.smusoak.restapi;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.smusoak.restapi.controllers.AuthenticationController;
import com.smusoak.restapi.controllers.S3Controller;
import com.smusoak.restapi.dto.UserDto;
import com.smusoak.restapi.filters.JwtAuthenticationFilter;
import com.smusoak.restapi.models.User;
import com.smusoak.restapi.repositories.UserRepository;
import com.smusoak.restapi.restdocs.AbstractRestDocsTests;
import com.smusoak.restapi.services.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(S3Controller.class)
@MockBeans({
        @MockBean(JwtAuthenticationFilter.class),
        @MockBean(S3Service.class)
})
public class S3ControllerTest extends AbstractRestDocsTests {

    @MockBean
    JwtService jwtService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void DownloadImg() throws Exception {
        mockMvc.perform(get("/api/v1/download/img/{path}/{fileName}", "testPath", "testFileName")
                        .header("Authorization", "Bearer ")
                        )
                .andExpect(status().isOk());
    }
}