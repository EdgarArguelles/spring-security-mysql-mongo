package app.integration_test;

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

import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class InfoCtrlIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TokenService tokenService;

    private final String BASE_URL = "/info";

    private IntegrationTest integrationTest;

    @Before
    public void setup() throws Exception {
        integrationTest = new IntegrationTest(mvc, mapper, tokenService);
    }

    /**
     * Should get application.properties api-version
     */
    @Test
    public void version() throws Exception {
        final String VERSION = "0.1";

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(BASE_URL + "/version"));

        assertNotSame(VERSION, mapResult.get("data"));
        assertEquals(VERSION, mapResult.get("data"));
        assertNull(mapResult.get("metaData"));
    }
}