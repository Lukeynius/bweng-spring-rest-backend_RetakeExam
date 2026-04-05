//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.service;

import at.technikum.springrestbackend.dto.*;
import at.technikum.springrestbackend.entity.*;
import at.technikum.springrestbackend.repository.OptionRepository;
import at.technikum.springrestbackend.repository.SurveyRepository;
import at.technikum.springrestbackend.repository.SurveyResponseRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final OptionRepository optionRepository;
    private final UserService userService;

    public List<SurveyResponseDto> findAll() {
        return surveyRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    public List<SurveyResponseDto> findActive(){
        return surveyRepository.findByStatusOrderByCreatedAtDesc(
                SurveyStatus.ACTIVE
                )
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    public SurveyResponseDto findById(UUID id){
        Survey survey = findEntityById(id);
        return toResponseDto(survey);
    }

    public Survey findEntityById(UUID id){
        return surveyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Survey not found"
                ));
    }

    @Transactional
    public SurveyResponseDto create(SurveyCreateDto dto, UUID creatorId){
        User creator = userService.findEntityById(creatorId);

        Survey survey = new Survey();
        survey.setTitle(dto.getTitle());
        survey.setDescription(dto.getDescription());
        survey.setStatus(SurveyStatus.DRAFT);
        survey.setCreator(creator);

        for(QuestionCreateDto qDto : dto.getQuestions()){
            Question question = new Question();
            question.setText(qDto.getQuestion());
            question.setType(qDto.getQuestionType());
            question.setSurvey(survey);
            for(String optionText : qDto.getOptions()){
                Option option = new Option();
                option.setText(optionText);
                option.setQuestion(question);
                question.getOptions().add(option);
            }
            survey.getQuestions().add(question);
        }

        Survey saved = surveyRepository.save(survey);
        return toResponseDto(saved);
    }

    @Transactional
    public SurveyResponseDto update(UUID id, SurveyCreateDto dto){
        Survey survey = findEntityById(id);
        survey.setTitle(dto.getTitle());
        survey.setDescription(dto.getDescription());

        survey.getQuestions().clear();

        for(QuestionCreateDto qDto : dto.getQuestions()){
            Question question = new Question();
            question.setText(qDto.getQuestion());
            question.setType(qDto.getQuestionType());
            question.setSurvey(survey);
            for(String optionText : qDto.getOptions()){
                Option option = new Option();
                option.setText(optionText);
                option.setQuestion(question);
                question.getOptions().add(option);
            }
            survey.getQuestions().add(question);
        }
        Survey saved = surveyRepository.save(survey);
        return toResponseDto(saved);
    }

    public SurveyResponseDto updateStatus(UUID id, SurveyStatus status){
        Survey survey = findEntityById(id);
        survey.setStatus(status);
        Survey saved = surveyRepository.save(survey);
        return toResponseDto(saved);
    }

    public void delete(UUID id){
        if(!surveyRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Survey not found");
        }
        surveyRepository.deleteById(id);
    }

    @Transactional
    public void participate(UUID surveyId, UUID userId, SurveyParticipateDto dto){
        Survey survey = findEntityById(surveyId);

        validateParticipation(survey, userId);

        User user = userService.findEntityById(userId);

        SurveyResponse surveyResponse = new SurveyResponse();
        surveyResponse.setUser(user);
        surveyResponse.setSurvey(survey);
        surveyResponse.setAnswers(buildAnswers(dto, surveyResponse));
        surveyResponseRepository.save(surveyResponse);
    }

    private SurveyResponseDto toResponseDto(Survey survey){
        SurveyResponseDto dto = new SurveyResponseDto();
        dto.setId(survey.getId());
        dto.setTitle(survey.getTitle());
        dto.setDescription(survey.getDescription());
        dto.setCoverImage(survey.getCoverImage());
        dto.setStatus(survey.getStatus());
        dto.setCreatorUsername(survey.getCreator().getUsername());
        dto.setCreatedAt(survey.getCreatedAt());
        dto.setUpdatedAt(survey.getUpdatedAt());

        List<QuestionsResponseDto> questionsResponseDtos =
                survey.getQuestions().stream()
                        .map(this::toQuestionDto)
                        .toList();
        dto.setQuestions(questionsResponseDtos);

        return dto;
    }

    private QuestionsResponseDto toQuestionDto(Question question){
        QuestionsResponseDto dto = new QuestionsResponseDto();
        dto.setId(question.getId());
        dto.setText(question.getText());
        dto.setType(question.getType());

        List<OptionResponseDto> optionDtos =
                question.getOptions().stream()
                        .map(this::toOptionDto)
                        .toList();
        dto.setOptions(optionDtos);

        return dto;
    }

    private OptionResponseDto toOptionDto(Option option){
        OptionResponseDto dto = new OptionResponseDto();
        dto.setId(option.getId());
        dto.setText(option.getText());
        return dto;       
    }

    private void validateParticipation(Survey survey, UUID userId){
        if(survey.getStatus() != SurveyStatus.ACTIVE){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Survey is not active"
            );
        }
        if(surveyResponseRepository.existsByUserIdAndSurveyId(userId, survey.getId())){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You already participated in this survey"
            );
        }
    }

    private List<Answer> buildAnswers(SurveyParticipateDto dto, SurveyResponse surveyResponse){

        List<Answer> answers = new ArrayList<>();

        for(Map.Entry<UUID, List<UUID>> entry : dto.getAnswers().entrySet()){
            List<UUID> optionIds = entry.getValue();
            for(UUID optionId : optionIds){
                Option option = optionRepository
                        .findById(optionId)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Option is not valid: " + optionId
                        ));
                Answer answer = new Answer();
                answer.setSurveyResponse(surveyResponse);
                answer.setQuestion(option.getQuestion());
                answer.setSelectedOption(option);
                answers.add(answer);
            }
        }
        return answers;
    }

}
