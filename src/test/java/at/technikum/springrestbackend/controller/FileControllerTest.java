//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.controller;


import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.security.JwtAuthenticationFilter;
import at.technikum.springrestbackend.security.JwtTokenProvider;
import at.technikum.springrestbackend.security.JwtValidator;
import at.technikum.springrestbackend.security.SecurityConfig;
import at.technikum.springrestbackend.service.FileStorageService;
import at.technikum.springrestbackend.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//#######################################################################
//#######################################################################
//#######################################################################
// class
@WebMvcTest(FileController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileStorageService fileStorageService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtValidator jwtValidator;

    @MockitoBean
    private UserService userService;

    @Test
    void uploadProfilePicture_returnsCreated() throws Exception{
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        when(fileStorageService.uploadFile(
                any(), eq("profile-picture")
        )).thenReturn("profile-picture/img.jpg");
        when(userService.findEntityById(userId)).thenReturn(user);
        when(userService.saveEntity(any())).thenReturn(user);

        MockMultipartFile file = new MockMultipartFile(
                "file", "img.jpg", "image/jpeg", "test".getBytes()
        );

        mockMvc.perform(multipart("/api/files/profile-picture")
            .file(file)
                .with(authentication(createAuth(userId, "ROLE_USER"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileName")
                        .value("profile-picture/img.jpg"));
    }

    @Test
    void uploadProfilePicture_unauthenticated_returnsUnauthorized() throws Exception{
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "data".getBytes()
        );
        mockMvc.perform(multipart("/api/files/profile-picture")
                .file(file))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void downloadFile_returnsOk() throws Exception{
        ByteArrayInputStream stream = new ByteArrayInputStream("data".getBytes());

        when(fileStorageService.downloadFile(any())).thenReturn(stream);
        mockMvc.perform(get("/api/files/profile-picture/img.jpg"))
                .andExpect(status().isOk());
    }

    private UsernamePasswordAuthenticationToken createAuth(
            UUID userId,
            String role
    ){
        return new UsernamePasswordAuthenticationToken(
                userId,
                null,
                List.of(new SimpleGrantedAuthority(role))
        );
    }

}
