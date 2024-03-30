package com.smusoak.restapi;

import com.smusoak.restapi.controllers.TestController;
import com.smusoak.restapi.filters.JwtAuthenticationFilter;
import com.smusoak.restapi.models.Role;
import com.smusoak.restapi.models.User;
import com.smusoak.restapi.restdocs.AbstractRestDocsTests;
import com.smusoak.restapi.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TestController에 Rest Docs를 만들기 위한 테스트케이스
@WebMvcTest(TestController.class)
@MockBeans({
        @MockBean(JwtAuthenticationFilter.class)
})
public class TestControllerTest extends AbstractRestDocsTests {

    @MockBean
    JwtService jwtService;

    @Test
    void HelloTest() throws Exception {
        mockMvc.perform(get("/test/hello"))
                .andExpect(status().isOk());
    }

    @Test
    void UserTest() throws Exception {
        mockMvc.perform(get("/test/user")
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
        mockMvc.perform(get("/test/admin")
                        .header("Authorization", "Bearer " +
                                jwtService.generateToken(User.builder()
                                        .mail("tmp")
                                        .role(Role.ROLE_ADMIN)
                                        .build()))
                )
                .andExpect(status().isOk());
    }
}
