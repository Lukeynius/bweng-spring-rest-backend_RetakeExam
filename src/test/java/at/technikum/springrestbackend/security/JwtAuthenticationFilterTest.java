//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//#######################################################################
//#######################################################################
//#######################################################################
// class
@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtValidator jwtValidator;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;


    @BeforeEach
    void setUp(){
        filter = new JwtAuthenticationFilter(jwtTokenProvider, jwtValidator);
        SecurityContextHolder.clearContext();
    }

    @Test
    void validToken_setsAuthentication() throws Exception {
        String token = "valid-token";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);
        when(jwtValidator.validateToken(token))
                .thenReturn(true);
        when(jwtTokenProvider
                .getUsernameFromToken(token))
                .thenReturn("testuser");
        when(jwtTokenProvider
                .getRoleFromToken(token))
                .thenReturn("USER");

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder
                .getContext().
                getAuthentication()
        );
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void noToken_doesNotSetAuthentication() throws Exception{
        when(request.getHeader("Authorization")).thenReturn(null);
        filter.doFilterInternal(
                request, response, filterChain
        );
        assertNull(SecurityContextHolder
                .getContext()
                .getAuthentication()
        );
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void invalidToken_doesNotSetAuthentication() throws Exception{
        String token = "invalid-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer" + token);
        //when(jwtValidator.validateToken(token)).thenReturn(false);
        filter.doFilterInternal(
                request, response, filterChain
        );
        assertNull(SecurityContextHolder
                .getContext().
                getAuthentication()
        );
        verify(filterChain).doFilter(request, response);
    }

}
