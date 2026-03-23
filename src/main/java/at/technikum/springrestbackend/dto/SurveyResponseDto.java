//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.dto;

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
    private String status;
    private String creatorUsername;
    private List<QuestionResponseDto> questions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
