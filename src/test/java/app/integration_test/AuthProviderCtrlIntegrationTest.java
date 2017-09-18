package app.integration_test;

import app.models.AuthProvider;
import app.repositories.*;
import app.security.services.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthProviderCtrlIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TokenService tokenService;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private AuthProviderRepository authProviderRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    private List<AuthProvider> dbAuthProviders;

    private final String BASE_URL = "/auth_providers";

    private IntegrationTest integrationTest;

    @Before
    public void setup() throws Exception {
        integrationTest = new IntegrationTest(mvc, mapper, tokenService);
        IntegrationTest.cleanAllData(authenticationRepository, authProviderRepository, personRepository, roleRepository, permissionRepository);

        dbAuthProviders = Arrays.asList(
                new AuthProvider("N1", "D1", "U1", "AK1", "AS1"),
                new AuthProvider("N2", "D2", "U2", "AK2", "AS2"),
                new AuthProvider("N3", "D3", "U3", "AK3", "AS3"),
                new AuthProvider("N4", "D4", "U4", "AK4", "AS4")
        );
        authProviderRepository.save(dbAuthProviders);
    }

    /**
     * Should get Auth Providers list
     */
    @Test
    public void list() throws Exception {
        final List<AuthProvider> authProviderExpected = dbAuthProviders;
        authProviderExpected.forEach(a -> {
            a.setUrl(null);
            a.setAuthKey(null);
            a.setAuthSecret(null);
        });

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(BASE_URL));
        final List<AuthProvider> authProviderResult = IntegrationTest.getAuthProviders(mapResult.get("data"));

        assertNotSame(authProviderExpected, authProviderResult);
        assertEquals(authProviderExpected, authProviderResult);
        assertNull(mapResult.get("metaData"));
    }
}