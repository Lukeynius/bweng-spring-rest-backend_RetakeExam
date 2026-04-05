//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.controller;


import at.technikum.springrestbackend.dto.UserResponseDto;
import at.technikum.springrestbackend.entity.Role;
import at.technikum.springrestbackend.security.JwtAuthenticationFilter;
import at.technikum.springrestbackend.security.JwtTokenProvider;
import at.technikum.springrestbackend.security.JwtValidator;
import at.technikum.springrestbackend.security.SecurityConfig;
import at.technikum.springrestbackend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//#######################################################################
//#######################################################################
//#######################################################################
// class
@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtValidator jwtValidator;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_asAdmin_returnsUser() throws Exception{
        UserResponseDto dto = new UserResponseDto();
        dto.setId(UUID.randomUUID());
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setRole(Role.USER);

        when(userService.findAll()).thenReturn(List.of(dto));
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    void getAll_unauthenticated_returnsUnauthorized() throws Exception{
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAll_asUser_returnsForbidden() throws Exception{
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void register_validData_returnsCreated() throws Exception{
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(UUID.randomUUID());
        responseDto.setUsername("newuser");
        responseDto.setEmail("new@example.com");
        responseDto.setRole(Role.USER);

        when(userService.register(any())).thenReturn(responseDto);
        mockMvc.perform(post("/api/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "newuser", "email","new@example.com","password","Password1","country","AT"
                        ))))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    void register_invalidEmail_returnsBadRequest() throws Exception{
        mockMvc.perform(post("/api/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "newuser", "email","invalidmail","password","Password1","country","AT"
                        ))))
                        .andExpect(status().isBadRequest());
    }

    @Test
    void register_shortUsername_returnsBadRequest() throws Exception{
        mockMvc.perform(post("/api/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "ab", "email","new@example.com","password","Password1","country","AT"
                        ))))
                        .andExpect(status().isBadRequest());
    }
}
