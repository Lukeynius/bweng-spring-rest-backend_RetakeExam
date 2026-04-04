//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.controller;


import at.technikum.springrestbackend.entity.Role;
import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.repository.UserRepository;
import at.technikum.springrestbackend.security.JwtAuthenticationFilter;
import at.technikum.springrestbackend.security.JwtTokenProvider;
import at.technikum.springrestbackend.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean; //deprecated - sub: MockitoBean
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//#######################################################################
//#######################################################################
//#######################################################################
// class
@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    void login_validCredentials_returnsToken() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setPassword("hashedPassword");
        user.setRole(Role.USER);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password1", "hashedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateToken(user.getId(), "testuser", "USER")).thenReturn("mock-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("username", "testuser", "password", "Password1"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void login_invalidUsername_returnsUnaothorized() throws Exception {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("username", "unknown", "password", "Password1"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_wrongPassword_returnsUnaothorized() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setPassword("hashedPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword", "hashedPassword")).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("username", "testuser", "password", "WrongPassword"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_missingFields_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

}
