//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.controller;


import at.technikum.springrestbackend.dto.SurveyResponseDto;
import at.technikum.springrestbackend.entity.SurveyStatus;
import at.technikum.springrestbackend.security.JwtAuthenticationFilter;
import at.technikum.springrestbackend.security.JwtTokenProvider;
import at.technikum.springrestbackend.security.JwtValidator;
import at.technikum.springrestbackend.security.SecurityConfig;
import at.technikum.springrestbackend.service.SurveyService;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//#######################################################################
//#######################################################################
//#######################################################################
// class
@WebMvcTest(SurveyController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
public class SurveyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SurveyService surveyService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtValidator jwtValidator;

    @Test
    void getAll_anonymous_returnsOk() throws Exception{
        SurveyResponseDto dto = new SurveyResponseDto();
        dto.setId(UUID.randomUUID());
        dto.setTitle("Test Survey");
        dto.setStatus(SurveyStatus.ACTIVE);

        when(surveyService.findAll()).thenReturn(List.of(dto));
        mockMvc.perform(get("/api/surveys"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Survey"));
    }

    @Test
    void getAll_withStatusFilter_returnsFiltered() throws Exception{
        SurveyResponseDto dto = new SurveyResponseDto();
        dto.setId(UUID.randomUUID());
        dto.setTitle("Active Survey");
        dto.setStatus(SurveyStatus.ACTIVE);

        when(surveyService.findActive()).thenReturn(List.of(dto));
        mockMvc.perform(get("/api/surveys").param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Active Survey"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getById_authenticated_returnsOk()
            throws Exception {
        UUID id = UUID.randomUUID();
        SurveyResponseDto dto = new SurveyResponseDto();
        dto.setId(id);
        dto.setTitle("My Survey");

        when(surveyService.findById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/surveys/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title")
                        .value("My Survey"));
    }

    @Test
    void create_asAdmin_returnsCreated()
            throws Exception {
                UUID adminId = UUID.randomUUID();
                SurveyResponseDto responseDto = new SurveyResponseDto();
                responseDto.setId(UUID.randomUUID());
                responseDto.setTitle("New Survey");
                responseDto.setStatus(SurveyStatus.DRAFT);

                when(surveyService.create(any(), eq(adminId)))
                        .thenReturn(responseDto);

                String body = """
                    {
                    "title": "New Survey",
                    "description": "Desc",
                    "questions": [{
                    "question": "Q1?",
                    "questionType": "SINGLE_CHOICE",
                    "options": ["A", "B"]
                    }]
                    }
                    """;

                mockMvc.perform(post("/api/surveys")
                                .with(authentication(
                                        createAuth(adminId, "ROLE_ADMIN")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                        .andExpect(status().isCreated());
    }

    @Test
    void delete_asAdmin_returnsNoContent()
            throws Exception {
        UUID id = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        doNothing().when(surveyService).delete(id);

        mockMvc.perform(delete("/api/surveys/" + id)
                        .with(authentication(
                                createAuth(adminId, "ROLE_ADMIN"))))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateStatus_asAdmin_returnsOk()
            throws Exception {
        UUID id = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        SurveyResponseDto dto = new SurveyResponseDto();
        dto.setId(id);
        dto.setStatus(SurveyStatus.ACTIVE);

        when(surveyService.updateStatus(
                eq(id), any(SurveyStatus.class)))
                .thenReturn(dto);

        mockMvc.perform(patch("/api/surveys/" + id
                        + "/status")
                        .with(authentication(
                                createAuth(adminId, "ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void create_unauthenticated_returnsUnauthorized()
            throws Exception {
        mockMvc.perform(post("/api/surveys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test\"}"))
                .andExpect(status().isUnauthorized());
    }

    private UsernamePasswordAuthenticationToken
    createAuth(UUID userId, String role) {
        return new UsernamePasswordAuthenticationToken(
                userId, null,
                List.of(new SimpleGrantedAuthority(role)));
    }

}
