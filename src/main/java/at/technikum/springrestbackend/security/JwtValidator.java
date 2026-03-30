package at.technikum.springrestbackend.security;

import org.springframework.stereotype.Component;

@Component
public class JwtValidator {

    private JwtTokenProvider jwtTokenProvider;

    public boolean validateToken(String token){
        try{
            jwtTokenProvider.parseClaims(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }

}
