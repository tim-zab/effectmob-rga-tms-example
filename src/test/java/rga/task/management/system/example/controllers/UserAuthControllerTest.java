package rga.task.management.system.example.controllers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import rga.task.management.system.example.dtos.UserAuthResponseDto;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@TestPropertySource(locations = "/application-test.yml")
@DisplayName("Test CommentController methods:")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserAuthControllerTest {

    private final TestRestTemplate testRestTemplate;

    private HttpHeaders headers;

    private final String AUTH_URL = "/rest/v1/security/auth";
    private final String REGISTER_URL = "/rest/v1/security/register";

    private String request = "{\"email\":\"%s\",\"password\":\"%s\"}";

    @LocalServerPort
    private Integer port;

    @BeforeEach
    public void setUp() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @Order(0)
    @DisplayName("Test access application")
    void accessApplication() {
        System.out.println(port);
    }

    @Test
    @Order(1)
    @DisplayName("Test context loads")
    public void contextLoads() {
    }

    @Test
    @Order(12)
    @DisplayName("Test authenticate user with valid e-mail and password")
    void testAuthenticateUserWithValidCredentials() {
        request = request.formatted("ivanov123@mail.ru", "passwordADMIN");
        var requestEntity = new HttpEntity<>(request, headers);

        var authResponse = testRestTemplate.postForEntity(AUTH_URL, requestEntity, UserAuthResponseDto.class);

        assertNotNull(authResponse.getBody());
        assertNotNull(authResponse.getBody().getJwt());
        assertSame(authResponse.getStatusCode(), HttpStatus.OK);
    }

    @Test
    @Order(23)
    @DisplayName("Test register new user with valid e-mail and password")
    void testRegisterNewUserWithValidData() {
        request = request.formatted("user@mail.ru", "user1234567");
        var requestEntity = new HttpEntity<>(request, headers);

        var registerResponse = testRestTemplate.exchange(REGISTER_URL, HttpMethod.POST, requestEntity, UserAuthResponseDto.class);

        assertNotNull(registerResponse.getBody());
        assertNotNull(registerResponse.getBody().getJwt());
        assertSame(registerResponse.getStatusCode(), HttpStatus.CREATED);
    }

    @Test
    @Order(10)
    @DisplayName("Test authenticate user with invalid e-mail")
    void testAuthenticateUserWithInvalidEmail() {
        request = request.formatted("mail.ru", "passwordADMIN");
        var requestEntity = new HttpEntity<>(request, headers);

        var authResponse = testRestTemplate.exchange(AUTH_URL, HttpMethod.POST, requestEntity, String.class);

        assertEquals("{\"url\":\"/rest/v1/security/auth\",\"message\":\"Invalid e-mail mail.ru has been provided\"}", authResponse.getBody());
        assertSame(authResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(20)
    @DisplayName("Test register new user with already existent e-mail")
    void testRegisterNewUserWithAlreadyExistentEmail() {
        request = request.formatted("ivanov123@mail.ru", "somePassword");
        var requestEntity = new HttpEntity<>(request, headers);

        var registerResponse = testRestTemplate.exchange(REGISTER_URL, HttpMethod.POST, requestEntity, String.class);

        assertEquals("{\"url\":\"/rest/v1/security/register\"," +
                "\"message\":\"User with e-mail ivanov123@mail.ru is already existent in the system. " +
                "Try to use another e-mail to register or just try to authenticate with this e-mail.\"}", registerResponse.getBody());
        assertSame(registerResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(21)
    @DisplayName("Test register new user with invalid e-mail")
    void testRegisterNewUserWithInvalidEmail() {
        request = request.formatted("@mail.ru", "newPassword");
        var requestEntity = new HttpEntity<>(request, headers);

        var registerResponse = testRestTemplate.exchange(REGISTER_URL, HttpMethod.POST, requestEntity, String.class);

        assertEquals("{\"url\":\"/rest/v1/security/register\",\"message\":\"Invalid e-mail @mail.ru has been provided\"}", registerResponse.getBody());
        assertSame(registerResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(22)
    @DisplayName("Test register new user with invalid password")
    void testRegisterNewUserWithInvalidPassword() {
        request = request.formatted("another@mail.ru", "passw");
        var requestEntity = new HttpEntity<>(request, headers);

        var registerResponse = testRestTemplate.exchange(REGISTER_URL, HttpMethod.POST, requestEntity, String.class);

        assertEquals("{\"url\":\"/rest/v1/security/register\",\"message\":\"Invalid password passw has been provided\"}", registerResponse.getBody());
        assertSame(registerResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(11)
    @DisplayName("Test authenticate user with invalid password")
    void testAuthenticateUserWithInvalidPassword() {
        request = request.formatted("ivanov123@mail.ru", "pass");
        var requestEntity = new HttpEntity<>(request, headers);

        var authResponse = testRestTemplate.exchange(AUTH_URL, HttpMethod.POST, requestEntity, String.class);

        assertEquals("{\"url\":\"/rest/v1/security/auth\",\"message\":\"Invalid password pass has been provided\"}", authResponse.getBody());
        assertSame(authResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

}
