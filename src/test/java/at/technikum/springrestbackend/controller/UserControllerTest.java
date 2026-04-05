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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Test
    void getById_asOwner_returnsOk() throws Exception {
        UUID userId = UUID.randomUUID();
        UserResponseDto dto = new UserResponseDto();
        dto.setId(userId);
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setRole(Role.USER);

        when(userService.findById(userId)).thenReturn(dto);

        mockMvc.perform(get("/api/users/" + userId)
                        .with(authentication(
                                createAuth(userId, "ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username")
                        .value("testuser"));
    }

    @Test
    void getById_asOtherUser_returnsForbidden()
            throws Exception {
        UUID userId = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();

        mockMvc.perform(get("/api/users/" + userId)
                        .with(authentication(
                                createAuth(otherId, "ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_asAdmin_returnsNoContent()
            throws Exception {
        UUID userId = UUID.randomUUID();
        doNothing().when(userService).delete(userId);

        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void update_asOwner_returnsOk() throws Exception {
        UUID userId = UUID.randomUUID();
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(userId);
        responseDto.setUsername("updated");
        responseDto.setRole(Role.USER);

        when(userService.update(any(), any()))
                .thenReturn(responseDto);

        mockMvc.perform(put("/api/users/" + userId)
                        .with(authentication(
                                createAuth(userId, "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("username", "updated"))))
                .andExpect(status().isOk());
    }

    private UsernamePasswordAuthenticationToken
    createAuth(UUID userId, String role) {
        return new UsernamePasswordAuthenticationToken(
                userId, null,
                List.of(new SimpleGrantedAuthority(role)));
    }

}
