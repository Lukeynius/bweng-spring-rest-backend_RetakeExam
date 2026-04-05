//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.security;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

//#######################################################################
//#######################################################################
//#######################################################################
// class
public class JwtTokenProviderTest {

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtValidator jwtValidator;

    @BeforeEach
    void setUp(){
        String secret = "testSecretKeyThatIsAtLeast256BitsLong" + "ForHS256AlgorithmTestTestTest";
        this.jwtTokenProvider = new JwtTokenProvider(secret, 86400000);
    }

    @Test
    void generateToken_createsValidToken(){
        UUID userId = UUID.randomUUID();
        String token = jwtTokenProvider.generateToken(userId, "testuser", "USER");
        assertNotNull(token);
        assertTrue(token.contains("."));
    }

    @Test
    void getUserIdFromToken_returnsCorrectId(){
        UUID userId = UUID.randomUUID();
        String token = jwtTokenProvider.generateToken(userId, "testuser", "USER");
        UUID result = jwtTokenProvider.getUserIdFromToken(token);
        assertEquals(userId, result);
    }

    @Test
    void getUsernameFromToken_returnsCorrectUsername(){
        UUID userId = UUID.randomUUID();
        String token = jwtTokenProvider.generateToken(userId, "testuser", "USER");
        String result = jwtTokenProvider.getUsernameFromToken(token);
        assertEquals("testuser", result);
    }

    @Test
    void getRoleFromToken_returnsCorrectRole(){
        UUID userId = UUID.randomUUID();
        String token = jwtTokenProvider.generateToken(userId, "testuser", "ADMIN");
        String result = jwtTokenProvider.getRoleFromToken(token);
        assertEquals("ADMIN", result);
    }

}
