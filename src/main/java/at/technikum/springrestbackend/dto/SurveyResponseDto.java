//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.dto;

import at.technikum.springrestbackend.entity.SurveyStatus;
import lombok.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


//#######################################################################
//#######################################################################
//#######################################################################
// class
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResponseDto {

    private UUID id;
    private String title;
    private String description;
    private String coverImage;
    private SurveyStatus status;
    private String creatorUsername;
    private List<QuestionsResponseDto> questions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
