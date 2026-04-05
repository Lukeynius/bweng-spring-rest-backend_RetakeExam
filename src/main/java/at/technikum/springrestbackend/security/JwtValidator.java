//#######################################################################
//#######################################################################
//#######################################################################
// import & package
package at.technikum.springrestbackend.security;

import org.springframework.stereotype.Component;


//#######################################################################
//#######################################################################
//#######################################################################
// class
@Component
public class JwtValidator {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtValidator(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public boolean validateToken(String token){
        try{
            jwtTokenProvider.parseClaims(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }

}
