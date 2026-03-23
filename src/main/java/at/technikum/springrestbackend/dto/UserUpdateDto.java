//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.dto;

import lombok.*;
import jakarta.validation.constraints.*;


//#######################################################################
//#######################################################################
//#######################################################################
// class
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {

    @Size(min = 5, message = "Username must beat least 5 characters")
    private String username;

    @Email(message = "Email must be a valid address")
    private String email;

    @Size(min = 2, max = 3, message = "Country must be valid country code")
    private String country;

}
