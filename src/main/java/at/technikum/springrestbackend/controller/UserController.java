//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.dto.UserRegisterDto;
import at.technikum.springrestbackend.dto.UserResponseDto;
import at.technikum.springrestbackend.dto.UserUpdateDto;
import at.technikum.springrestbackend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;


//#######################################################################
//#######################################################################
//#######################################################################
// class
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //GET - all user [Admin]
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    //GET - single user
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable UUID id, Authentication authentication){
        checkOwnerOrAdmin(id, authentication);
        return ResponseEntity.ok(userService.findById(id));
    }

    //POST - register
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRegisterDto dto){
        UserResponseDto created = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    //PUT - update user
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(@PathVariable UUID id, @Valid @RequestBody UserUpdateDto dto, Authentication authentication){
        checkOwnerOrAdmin(id, authentication);
        return ResponseEntity.ok(userService.update(id, dto));
    }

    //DELETE - delete user [Admin]
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    //check
    private void checkOwnerOrAdmin(UUID id, Authentication authentication){
        UUID currentUserId = (UUID) authentication.getPrincipal();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if(!isAdmin && !id.equals(currentUserId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this resource");
        }
    }
}
