//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.service;


import at.technikum.springrestbackend.dto.UserRegisterDto;
import at.technikum.springrestbackend.dto.UserResponseDto;
import at.technikum.springrestbackend.dto.UserUpdateDto;
import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.repository.UserRepository;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

//#######################################################################
//#######################################################################
//#######################################################################
// class
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    public UserResponseDto findById(UUID id){
        User user = findEntityById(id);
        return toResponseDto(user);
    }

    public User findEntityById(UUID id){
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public UserResponseDto register(UserRegisterDto dto){
        if(userRepository.existsByUsername(dto.getUsername())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        if(userRepository.existsByEmail(dto.getEmail())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        //DO NOT FORGET TO HASH PW
        user.setPassword(dto.getPassword());
        user.setCountry(dto.getCountry());
        User saved = userRepository.save(user);
        return toResponseDto(saved);
    }

    public UserResponseDto update(UUID id, UserUpdateDto dto){
        User user = findEntityById(id);
        if(dto.getUsername() != null){
            if(userRepository.existsByUsername(dto.getUsername()) && !user.getUsername().equals(dto.getUsername())){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
            }
            user.setUsername(dto.getUsername());
        }
        if(dto.getEmail() != null){
            if(userRepository.existsByEmail(dto.getEmail()) && !user.getEmail().equals(dto.getEmail())){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
            }
            user.setEmail(dto.getEmail());
        }
        if(dto.getCountry() != null){
            user.setCountry(dto.getCountry());
        }
        User saved = userRepository.save(user);
        return toResponseDto(saved);
    }

    public void delete(UUID id){
        if(!userRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(id);
    }

    private UserResponseDto toResponseDto(User user){
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCountry(user.getCountry());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

}
