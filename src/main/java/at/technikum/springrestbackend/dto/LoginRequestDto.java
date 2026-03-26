//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

//#######################################################################
//#######################################################################
//#######################################################################
// class
@Getter
@Setter
public class LoginRequestDto {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

}
