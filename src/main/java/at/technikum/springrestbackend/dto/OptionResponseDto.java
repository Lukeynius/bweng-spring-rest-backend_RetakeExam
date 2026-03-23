//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.dto;

import lombok.*;
import jakarta.validation.constraints.*;

import java.util.UUID;


//#######################################################################
//#######################################################################
//#######################################################################
// class
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OptionResponseDto {

    private UUID id;
    private String text;

}
