//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.dto;

import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;


//#######################################################################
//#######################################################################
//#######################################################################
// class
public class ParticipateDto {

    @NotEmpty(message = "Answer is required")
    private Map<UUID, List<UUID>> answers;

}
