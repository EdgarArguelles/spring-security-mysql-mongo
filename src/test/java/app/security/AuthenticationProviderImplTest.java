package app.security;

import app.security.pojos.LoggedUser;
import app.security.services.TokenService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthenticationProviderImplTest {

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @MockBean
    private TokenService tokenService;

    /**
     * Should return null when token invalid
     */
    @Test
    public void authenticateTokenInvalid() throws IOException {
        final String TOKEN = "token";
        final Authentication authentication = new UsernamePasswordAuthenticationToken(TOKEN, null);

        given(tokenService.getLoggedUser(TOKEN)).willReturn(null);

        final Authentication authenticateResult = authenticationProvider.authenticate(authentication);

        assertNull(authenticateResult);
        verify(tokenService, times(1)).getLoggedUser(TOKEN);
    }

    /**
     * Should create valid UsernamePasswordAuthenticationToken when token valid without permissions
     */
    @Test
    public void authenticateTokenValidWithNotPermissions() throws IOException {
        final String TOKEN = "token";
        final LoggedUser userMocked = new LoggedUser("ID", "ROLE");
        final Authentication authentication = new UsernamePasswordAuthenticationToken(TOKEN, null);

        given(tokenService.getLoggedUser(TOKEN)).willReturn(userMocked);

        final Authentication authenticateExpected = new UsernamePasswordAuthenticationToken(userMocked, null, Collections.EMPTY_LIST);

        final Authentication authenticateResult = authenticationProvider.authenticate(authentication);

        assertNotSame(authentication, authenticateResult);
        assertNotSame(authenticateExpected, authenticateResult);
        assertSame(authenticateExpected.getPrincipal(), authenticateResult.getPrincipal());
        assertEquals(authenticateExpected.getAuthorities().size(), authenticateResult.getAuthorities().size());
        assertEquals(0, authenticateResult.getAuthorities().size());
        verify(tokenService, times(1)).getLoggedUser(TOKEN);
    }

    /**
     * Should create valid UsernamePasswordAuthenticationToken when token valid with permissions
     */
    @Test
    public void authenticateTokenValidWithPermissions() throws IOException {
        final String TOKEN = "token";
        final LoggedUser userMocked = new LoggedUser("ID", "ROLE");
        userMocked.setPermissions(new HashSet<>(Arrays.asList("PERMISSION1", "PERMISSION2")));
        final Authentication authentication = new UsernamePasswordAuthenticationToken(TOKEN, null);

        given(tokenService.getLoggedUser(TOKEN)).willReturn(userMocked);

        final List<GrantedAuthority> authorities = userMocked.getPermissions().stream().map(p -> (GrantedAuthority) () -> "ROLE_" + p).collect(Collectors.toList());
        final Authentication authenticateExpected = new UsernamePasswordAuthenticationToken(userMocked, null, authorities);

        final Authentication authenticateResult = authenticationProvider.authenticate(authentication);

        assertNotSame(authentication, authenticateResult);
        assertNotSame(authenticateExpected, authenticateResult);
        assertSame(authenticateExpected.getPrincipal(), authenticateResult.getPrincipal());
        assertEquals(authenticateExpected.getAuthorities().size(), authenticateResult.getAuthorities().size());
        assertEquals(2, authenticateResult.getAuthorities().size());
        verify(tokenService, times(1)).getLoggedUser(TOKEN);
    }

    /**
     * Should return true
     */
    @Test
    public void supports() {
        final boolean result = authenticationProvider.supports(null);

        assertTrue(result);
    }
}