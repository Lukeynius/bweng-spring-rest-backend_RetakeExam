//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.dto;

import jakarta.validation.Valid;
import lombok.*;
import jakarta.validation.constraints.*;

import java.util.List;


//#######################################################################
//#######################################################################
//#######################################################################
// class
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyCreateDto {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be less than 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @NotEmpty(message = "Survey must have at least one question")
    @Valid
    private List<QuestionCreateDto> questions;
}
