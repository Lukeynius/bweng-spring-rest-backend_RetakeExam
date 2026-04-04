//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.controller;


import at.technikum.springrestbackend.dto.SurveyResponseDto;
import at.technikum.springrestbackend.entity.SurveyStatus;
import at.technikum.springrestbackend.security.JwtTokenProvider;
import at.technikum.springrestbackend.security.SecurityConfig;
import at.technikum.springrestbackend.service.SurveyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//#######################################################################
//#######################################################################
//#######################################################################
// class
@WebMvcTest(SurveyController.class)
@Import(SecurityConfig.class)
public class SurveyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SurveyService surveyService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

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
}
