//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.service;


import at.technikum.springrestbackend.dto.QuestionCreateDto;
import at.technikum.springrestbackend.dto.SurveyCreateDto;
import at.technikum.springrestbackend.dto.SurveyParticipateDto;
import at.technikum.springrestbackend.dto.SurveyResponseDto;
import at.technikum.springrestbackend.entity.*;
import at.technikum.springrestbackend.repository.AnswerRepository;
import at.technikum.springrestbackend.repository.OptionRepository;
import at.technikum.springrestbackend.repository.SurveyRepository;
import at.technikum.springrestbackend.repository.SurveyResponseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//#######################################################################
//#######################################################################
//#######################################################################
// class
@ExtendWith(MockitoExtension.class)
public class SurveyServiceTest {

    @Mock
    private SurveyRepository surveyRepository;

    @Mock
    private SurveyResponseRepository surveyResponseRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private UserService userService;

    @Mock
    private OptionRepository optionRepository;

    @InjectMocks
    private SurveyService surveyService;

    private Survey testSurvey;
    private User testUser;
    private UUID surveyId;
    private UUID userId;

    @BeforeEach
    void setUp(){
        surveyId = UUID.randomUUID();
        userId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("admin");
        testUser.setRole(Role.ADMIN);
        testSurvey = new Survey();
        testSurvey.setId(surveyId);
        testSurvey.setTitle("Test Survey");
        testSurvey.setDescription("A test survey");
        testSurvey.setStatus(SurveyStatus.DRAFT);
        testSurvey.setCreator(testUser);
        testSurvey.setQuestions(new ArrayList<>());
    }

    @Test
    void findAll_returnsAllSurveys(){
        when(surveyRepository.findAll()).thenReturn(List.of(testSurvey));
        List<SurveyResponseDto> result = surveyService.findAll();
        assertEquals(1, result.size());
        assertEquals("Test Survey", result.getFirst().getTitle());
    }

    @Test
    void findById_existingSurvey_returnsSurvey(){
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(testSurvey));
        SurveyResponseDto result = surveyService.findById(surveyId);
        assertEquals("Test Survey", result.getTitle());
    }

    @Test
    void findById_nonExistingSurvey_throwsNotFound(){
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, ()->surveyService.findById(surveyId));
    }

    @Test
    void findActive_returnsActiveSurveys(){
        testSurvey.setStatus(SurveyStatus.ACTIVE);
        when(surveyRepository.findByStatusOrderByCreatedAtDesc(SurveyStatus.ACTIVE)).thenReturn(List.of(testSurvey));
        List<SurveyResponseDto> result = surveyService.findActive();
        assertEquals(1, result.size());
    }

    @Test
    void create_validData_createsSurvey(){
        SurveyCreateDto dto = new SurveyCreateDto();
        dto.setTitle("New Survey");
        dto.setDescription("Description of the new survey");
        QuestionCreateDto qDto = new QuestionCreateDto();
        qDto.setQuestion("Question 1");
        qDto.setQuestionType(QuestionType.SINGLE_CHOICE);
        qDto.setOptions(List.of("Option 1", "Option 2"));
        dto.setQuestions(List.of(qDto));

        when(userService.findEntityById(userId)).thenReturn(testUser);
        when(surveyRepository.save(any(Survey.class))).thenReturn(testSurvey);

        SurveyResponseDto result = surveyService.create(dto, userId);
        assertNotNull(result);
        verify(surveyRepository).save(any(Survey.class));
    }

    @Test
    void updateStatus_validTransition_updateStatus(){
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(testSurvey));
        when(surveyRepository.save(any(Survey.class))).thenReturn(testSurvey);
        SurveyResponseDto result = surveyService.updateStatus(surveyId, SurveyStatus.ACTIVE);
        assertNotNull(result);
        verify(surveyRepository).save(any(Survey.class));
    }

    @Test
    void delete_existingSurvey_deletesSurvey(){
        when(surveyRepository.existsById(surveyId)).thenReturn(true);
        surveyService.delete(surveyId);
        verify(surveyRepository).deleteById(surveyId);
    }

    @Test
    void delete_nonExistingSurvey_throwsNotFound(){
        when(surveyRepository.existsById(surveyId)).thenReturn(false);
        assertThrows(ResponseStatusException.class, ()->surveyService.delete(surveyId));
    }

    @Test
    void participate_validData_createsResponse(){
        Question question = new Question();
        UUID questionId = UUID.randomUUID();
        question.setId(questionId);
        question.setType(QuestionType.SINGLE_CHOICE);
        Option option = new Option();
        UUID optionId = UUID.randomUUID();
        option.setId(optionId);
        option.setQuestion(question);
        question.setOptions(List.of(option));

        testSurvey.setStatus(SurveyStatus.ACTIVE);
        testSurvey.setQuestions(List.of(question));

        SurveyParticipateDto dto = new SurveyParticipateDto();
        dto.setAnswers(Map.of(questionId, List.of(optionId)));

        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(testSurvey));
        when(userService.findEntityById(userId)).thenReturn(testUser);
        when(surveyResponseRepository.existsByUserIdAndSurveyId(userId, surveyId)).thenReturn(false);
        when(surveyResponseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(optionRepository.findById(optionId)).thenReturn(Optional.of(option));

        surveyService.participate(surveyId, userId, dto);
        verify(surveyResponseRepository).save(any());
        verify(answerRepository).saveAll(any());
    }

    @Test
    void participate_alreadyParticipated_throwsConflict(){
        testSurvey.setStatus(SurveyStatus.ACTIVE);
        SurveyParticipateDto dto = new SurveyParticipateDto();
        dto.setAnswers(Map.of());

        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(testSurvey));
        when(surveyResponseRepository.existsByUserIdAndSurveyId(userId, surveyId)).thenReturn(true);

        assertThrows(ResponseStatusException.class, ()->surveyService.participate(surveyId, userId, dto));
    }

    @Test
    void participate_surveyNotActive_throwsBadRequest(){
        testSurvey.setStatus(SurveyStatus.DRAFT);
        SurveyParticipateDto dto = new SurveyParticipateDto();

        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(testSurvey));

        assertThrows(ResponseStatusException.class, ()->surveyService.participate(surveyId, userId, dto));
    }

}
