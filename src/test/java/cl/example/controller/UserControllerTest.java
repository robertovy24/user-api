package cl.example.controller;

import cl.example.dto.PhoneDTO;
import cl.example.dto.UserRequest;
import cl.example.dto.UserResponse;
import cl.example.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(cl.example.config.TestSecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        UserRequest request = UserRequest.builder()
                .name("Juan Rodriguez")
                .email("juan@rodriguez.org")
                .password("hunter2")
                .phones(Collections.singletonList(PhoneDTO.builder()
                        .number("1234567")
                        .citycode("1")
                        .contrycode("57")
                        .build()))
                .build();

        UserResponse response = UserResponse.builder()
                .id(UUID.randomUUID())
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .token("mocked-jwt-token")
                .isActive(true)
                .build();

        Mockito.when(userService.register(Mockito.any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.token", is("mocked-jwt-token")))
                .andExpect(jsonPath("$.isActive", is(true)));
    }
}
