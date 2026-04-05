//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.controller;


import at.technikum.springrestbackend.dto.SurveyCreateDto;
import at.technikum.springrestbackend.dto.SurveyParticipateDto;
import at.technikum.springrestbackend.dto.SurveyResponseDto;
import at.technikum.springrestbackend.entity.SurveyStatus;
import at.technikum.springrestbackend.service.SurveyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

//#######################################################################
//#######################################################################
//#######################################################################
// class
@RestController
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    //GET - all surveys
    @GetMapping
    public ResponseEntity<List<SurveyResponseDto>> getAll(
            @RequestParam(required = false) SurveyStatus status
    ){
        if (status != null) {
            return ResponseEntity.ok(surveyService.findActive());
        }
        return ResponseEntity.ok(surveyService.findAll());
    }

    //GET - survey details
    @GetMapping("/{id}")
    public ResponseEntity<SurveyResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(surveyService.findById(id));
    }

    //POST - create survey
    @PostMapping
    public ResponseEntity<SurveyResponseDto> create(
            @Valid @RequestBody SurveyCreateDto dto,
            Authentication authentication
    ){
        UUID creatorId = (UUID) authentication.getPrincipal();
        SurveyResponseDto created = surveyService.create(dto, creatorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    //PUT - update survey [ADMIN]
    @PutMapping("/{id}")
    public ResponseEntity<SurveyResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody SurveyCreateDto dto
    ){
        return ResponseEntity.ok(surveyService.update(id, dto));
    }

    //PATCH - update survey status
    @PatchMapping("/{id}/status")
    public ResponseEntity<SurveyResponseDto> updateStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body
    ){
        SurveyStatus status = SurveyStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(surveyService.updateStatus(id, status));
    }

    //DELETE - delete survey [ADMIN]
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        surveyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    //POST - participate
    @PostMapping("/{id}/participate")
    public ResponseEntity<Void> participate(
            @PathVariable UUID id,
            @Valid @RequestBody SurveyParticipateDto dto,
            Authentication authentication
    ){
        UUID userId = (UUID) authentication.getPrincipal();
        surveyService.participate(id, userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}