//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.dto;

import at.technikum.springrestbackend.entity.QuestionType;
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
public class QuestionCreateDto {

    @NotBlank(message = "Question is required")
    @Size(max = 500, message = "Question must be less than 500 characters")
    private String question;

    @NotNull(message = "Question type is required")
    private QuestionType questionType;

    @NotEmpty(message = "Question must have at least 2 options")
    @Size(min = 2, message = "At least 2 options are required")
    private List<String> options;

}
