package com.smusoak.restapi;

import com.smusoak.restapi.controllers.TestController;
import com.smusoak.restapi.filters.JwtAuthenticationFilter;
import com.smusoak.restapi.restdocs.AbstractRestDocsTests;
import com.smusoak.restapi.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.TestPropertySource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestController.class)
@MockBeans({
        @MockBean(JwtAuthenticationFilter.class),
        @MockBean(JwtService.class)
})
public class TestControllerTest extends AbstractRestDocsTests {
    @Test
    void RestDocsTest() throws Exception {
        mockMvc.perform(get("/test/hello")).andExpect(status().isOk());
    }
}
