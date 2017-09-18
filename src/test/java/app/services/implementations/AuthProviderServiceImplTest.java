package app.services.implementations;

import app.models.AuthProvider;
import app.repositories.AuthProviderRepository;
import app.services.AuthProviderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthProviderServiceImplTest {

    @Autowired
    private AuthProviderService authProviderService;

    @MockBean
    private AuthProviderRepository authProviderRepository;

    /**
     * Should call findAll function
     */
    @Test
    public void findAll() {
        final List<AuthProvider> authProvidersMocked = Arrays.asList(
                new AuthProvider("ID1"), new AuthProvider("ID2"), null, new AuthProvider("ID4"));
        given(authProviderRepository.findAll()).willReturn(authProvidersMocked);

        final List<AuthProvider> authProvidersExpected = Arrays.asList(
                new AuthProvider("ID1"), new AuthProvider("ID2"), null, new AuthProvider("ID4"));

        final List<AuthProvider> authProvidersResult = authProviderService.findAll();

        assertSame(authProvidersMocked, authProvidersResult);
        assertNotSame(authProvidersExpected, authProvidersResult);
        assertEquals(authProvidersExpected, authProvidersResult);
        verify(authProviderRepository, times(1)).findAll();
    }
}