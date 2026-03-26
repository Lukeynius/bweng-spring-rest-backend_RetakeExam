//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

//#######################################################################
//#######################################################################
//#######################################################################
// class
@Getter
@AllArgsConstructor
public class LoginResponseDto {

    private String token;
    private String username;
    private String role;
}
