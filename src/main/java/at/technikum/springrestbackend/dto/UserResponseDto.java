//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.dto;

import at.technikum.springrestbackend.entity.Role;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


//#######################################################################
//#######################################################################
//#######################################################################
// class
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private UUID id;
    private String username;
    private String email;
    private String country;
    private String profilePicture;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
