//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.dto;

import at.technikum.springrestbackend.entity.QuestionType;
import lombok.*;
import jakarta.validation.constraints.*;

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
public class QuestionsResponseDto {

    private UUID id;
    private String text;
    private QuestionType type;
    private List<OptionResponseDto> options;

}
