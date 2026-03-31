//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;



//#######################################################################
//#######################################################################
//#######################################################################
// class
public class JwtValidatorTest {

    private JwtTokenProvider jwtTokenProvider;
    private JwtValidator jwtValidator;

    @BeforeEach
    void setUp(){
        String secret = "testSecretKeyThatIsAtLeast256BitsLong" + "ForHS256AlgorithmTestTestTest";
        jwtTokenProvider = new JwtTokenProvider(secret, 86400000);
    }

    @Test
    void validateToken_validToken_returnsTrue(){
        UUID userId = UUID.randomUUID();
        String token = jwtTokenProvider.generateToken(userId, "testuser", "USER");
        assertTrue(jwtValidator.validateToken(token));
    }

    @Test
    void validateToken_invalidToken_returnsFalse(){
        assertFalse(jwtValidator.validateToken("invalid.token.with.no.parts"));
    }

    @Test
    void validateToken_expiredToken_returnsFalse(){
        JwtTokenProvider expiredProvider = new JwtTokenProvider("testSecretKeyThatIsAtLeast256BitsLong" + "ForHS256AlgorithmTestTestTest", 0);
        UUID userId = UUID.randomUUID();
        String token = expiredProvider.generateToken(userId, "testuser", "USER");
        assertFalse(jwtValidator.validateToken(token));
    }

}
