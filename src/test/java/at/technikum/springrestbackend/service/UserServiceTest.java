//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.service;


import at.technikum.springrestbackend.dto.UserRegisterDto;
import at.technikum.springrestbackend.dto.UserResponseDto;
import at.technikum.springrestbackend.dto.UserUpdateDto;
import at.technikum.springrestbackend.entity.Role;
import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//#######################################################################
//#######################################################################
//#######################################################################
// class
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UUID testId;

    @BeforeEach
    void setUp(){
        testId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(testId);
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedPassword");
        testUser.setCountry("AT");
        testUser.setRole(Role.USER);
    }

    @Test
    void findAll_returnsAllUsers(){
        when(userRepository.findAll()).thenReturn(List.of(testUser));
        List<UserResponseDto> result = userService.findAll();
        assertEquals(1, result.size());
        assertEquals("testuser", result.getFirst().getUsername());
    }

     @Test
    void findById_returnsUserById(){
        when(userRepository.findById(testId)).thenReturn(Optional.of(testUser));
        UserResponseDto result = userService.findById(testId);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void findById_nonExistingUser_throwsNotFound(){
        when(userRepository.findById(testId)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, ()->userService.findById(testId));
    }

    @Test
    void register_validData_createUser(){
        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("newuser");
        dto.setEmail("new@example.com");
        dto.setPassword("Password1");
        dto.setCountry("AT");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Passwor1")).thenReturn("hashedPw");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponseDto result = userService.register(dto);
        assertNotNull(result);
        verify(passwordEncoder).encode("Password1");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_duplicateUsername_throwsConflict(){
        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("testuser");
        dto.setEmail("new@example.com");

        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        assertThrows(ResponseStatusException.class, ()->userService.register(dto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_duplicateEmail_throwsConflict(){
        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("newuser");
        dto.setEmail("test@example.com");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        assertThrows(ResponseStatusException.class, ()->userService.register(dto));
    }

    @Test
    void update_validData_updateUser(){
        UserUpdateDto dto = new UserUpdateDto();
        dto.setUsername("updateuser");
        dto.setEmail("updated@example.com");
        dto.setCountry("DE");

        when(userRepository.findById(testId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("updateuser")).thenReturn(false);
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponseDto result = userService.update(testId, dto);
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update_duplicateUsername_throwsConflict(){
        UserUpdateDto dto = new UserUpdateDto();
        dto.setUsername("takenuser");

        when(userRepository.findById(testId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("takenuser")).thenReturn(true);
        assertThrows(ResponseStatusException.class, ()->userService.update(testId, dto));
    }

    @Test
    void delete_existingUser_deletesUser(){
        when(userRepository.existsById(testId)).thenReturn(true);
        userService.delete(testId);
        verify(userRepository).deleteById(testId);
    }

    @Test
    void delete_nonExistingUser_throwsNotFound(){
        when(userRepository.existsById(testId)).thenReturn(false);
        assertThrows(ResponseStatusException.class, ()->userService.delete(testId));
    }

    @Test
    void saveEntity_savesUser(){
        when(userRepository.save(testUser)).thenReturn(testUser);
        User result = userService.saveEntity(testUser);
        assertEquals(testUser, result);
    }
}
