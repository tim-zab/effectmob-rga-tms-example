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
import rga.task.management.system.example.dtos.CommentDto;
import rga.task.management.system.example.dtos.ResponseMessageDto;
import rga.task.management.system.example.enums.Role;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@TestPropertySource(locations = "/application-test.yml")
@DisplayName("Test CommentController methods:")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentControllerTest {

    private final TestRestTemplate testRestTemplate;

    private final String COMMENTS_ENDPOINT = "/rest/v1/comments";

    private HttpHeaders httpHeadersForAdmin;
    private HttpHeaders httpHeadersForUser;

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setUp() {
        httpHeadersForAdmin = new HttpHeaders();
        httpHeadersForAdmin.setContentType(MediaType.APPLICATION_JSON);
        final String adminEmail = "ivanov123@mail.ru";
        final String adminPassword = "passwordADMIN";
        prepareHttpHeaders(httpHeadersForAdmin, adminEmail, adminPassword);

        httpHeadersForUser = new HttpHeaders();
        httpHeadersForUser.setContentType(MediaType.APPLICATION_JSON);
        final String userEmail = "pet89rov@mail.ru";
        final String userPassword = "passwordUSER";
        prepareHttpHeaders(httpHeadersForUser, userEmail, userPassword);
    }

    private void prepareHttpHeaders(HttpHeaders httpHeaders, String email, String password) {
        var userJwt = getJwt(email, password);
        httpHeaders.setBearerAuth(userJwt);
    }

    private String getJwt(String email, String password) {
        final String AUTH_REQUEST = "{\"email\":\"%s\",\"password\":\"%s\"}".formatted(email, password);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<String> HTTP_ENTITY = new HttpEntity<>(AUTH_REQUEST, httpHeaders);

        final String AUTH_URL = "/rest/v1/security/auth";
        ResponseEntity<UserAuthResponseDto> authResponse = testRestTemplate.postForEntity(AUTH_URL, HTTP_ENTITY, UserAuthResponseDto.class);
        return Objects.requireNonNull(authResponse.getBody()).getJwt();
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
    @Order(10)
    @DisplayName("Test get page with comments (by task id) by user with role ROLE_ADMIN")
    void testGetAllCommentsByTaskIdByAdmin(){
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(httpHeadersForAdmin);
        ResponseEntity<String> response = testRestTemplate.exchange(COMMENTS_ENDPOINT + "/task/3", HttpMethod.GET, stringHttpEntity, String.class);
        
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("[{\"id\":1,\"content\":\"Content 1\",\"timestamp\":"));
        assertTrue(response.getBody().contains(",\"Commentator\":{\"id\":2,\"e-mail\":\"pet89rov@mail.ru\",\"user role\":\"ROLE_USER\"}}]"));
        assertSame(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    @Order(11)
    @DisplayName("Test get page with comments (by task id) by task executor")
    void testGetAllCommentsByTaskIdByTaskExecutor(){
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(httpHeadersForUser);
        ResponseEntity<String> response = testRestTemplate.exchange(COMMENTS_ENDPOINT + "/task/3", HttpMethod.GET, stringHttpEntity, String.class);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("[{\"id\":1,\"content\":\"Content 1\",\"timestamp\":"));
        assertTrue(response.getBody().contains(",\"Commentator\":{\"id\":2,\"e-mail\":\"pet89rov@mail.ru\",\"user role\":\"ROLE_USER\"}}]"));
        assertSame(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    @Order(13)
    @DisplayName("Test get page with comments (by nonexistent task id) by executor or user with role ROLE_ADMIN")
    void testGetAllCommentsByNonExistentTaskIdByAdmin(){
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(httpHeadersForAdmin);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.exchange(COMMENTS_ENDPOINT + "/task/33", HttpMethod.GET, stringHttpEntity, ResponseMessageDto.class);

        assertNotNull(response.getBody());
        assertEquals("ResponseMessageDto(url=/rest/v1/comments/task/33, message=Task with id 33 is not found)", response.getBody().toString());
        assertSame(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(12)
    @DisplayName("Test get page with comments (by task id) by user with role ROLE_USER and is not task executor")
    void testGetAllCommentsByTaskIdByNotTaskExecutor(){
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(httpHeadersForUser);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.exchange(COMMENTS_ENDPOINT + "/task/2", HttpMethod.GET, stringHttpEntity, ResponseMessageDto.class);

        assertNotNull(response.getBody());
        assertEquals("ResponseMessageDto(url=/rest/v1/comments/task/2, message=Access denied. User does not have the required permissions to get this task)", response.getBody().toString());
        assertSame(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(41)
    @DisplayName("Test update comment by not its author")
    void testUpdateCommentByNotItsAuthor(){
        HttpEntity<String> stringHttpEntity = new HttpEntity<>("some content", httpHeadersForAdmin);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.exchange(COMMENTS_ENDPOINT + "/2", HttpMethod.PUT, stringHttpEntity, ResponseMessageDto.class);

        assertNotNull(response.getBody());
        assertEquals("ResponseMessageDto(url=/rest/v1/comments/2, message=Access denied. User does not have the required permissions to edit comment.)", response.getBody().toString());
        assertSame(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(40)
    @DisplayName("Test update nonexistent comment by user with role ROLE_ADMIN")
    void testUpdateNonExistentComment(){
        HttpEntity<String> stringHttpEntity = new HttpEntity<>("some content", httpHeadersForAdmin);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.exchange(COMMENTS_ENDPOINT + "/12", HttpMethod.PUT, stringHttpEntity, ResponseMessageDto.class);

        assertNotNull(response.getBody());
        assertEquals("ResponseMessageDto(url=/rest/v1/comments/12, message=Comment with id 12 is not found)", response.getBody().toString());
        assertSame(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(42)
    @DisplayName("Test update comment by its author")
    void testUpdateCommentByItsAuthor(){
        HttpEntity<String> stringHttpEntity = new HttpEntity<>("some content", httpHeadersForUser);
        ResponseEntity<CommentDto> response = testRestTemplate.exchange(COMMENTS_ENDPOINT + "/3", HttpMethod.PUT, stringHttpEntity, CommentDto.class);

        assertNotNull(response.getBody());
        assertEquals(3L, response.getBody().getId());
        assertEquals("some content", response.getBody().getContent());
        assertEquals(2L, response.getBody().getCommentator().getId());
        assertEquals("pet89rov@mail.ru", response.getBody().getCommentator().getEmail());
        assertEquals(response.getBody().getCommentator().getRole(), Role.ROLE_USER);
        assertSame(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    @Order(30)
    @DisplayName("Test add comment to existent task by user with role ROLE_ADMIN")
    void testAddCommentToExistentTaskByAdmin() {
        HttpEntity<String> stringHttpEntity = new HttpEntity<>("new content", httpHeadersForAdmin);
        ResponseEntity<CommentDto> response = testRestTemplate.postForEntity(COMMENTS_ENDPOINT + "/task/1", stringHttpEntity, CommentDto.class);

        assertNotNull(response.getBody());
        assertEquals(6L, response.getBody().getId());
        assertEquals("new content", response.getBody().getContent());
        assertEquals(1L, response.getBody().getCommentator().getId());
        assertEquals("ivanov123@mail.ru", response.getBody().getCommentator().getEmail());
        assertEquals(response.getBody().getCommentator().getRole(), Role.ROLE_ADMIN);
        assertSame(response.getStatusCode(), HttpStatus.CREATED);
    }

    @Test
    @Order(31)
    @DisplayName("Test add comment to existent task by task executor")
    void testAddCommentToExistentTaskByTaskExecutor() {
        HttpEntity<String> stringHttpEntity = new HttpEntity<>("new content", httpHeadersForUser);
        ResponseEntity<CommentDto> response = testRestTemplate.postForEntity(COMMENTS_ENDPOINT + "/task/1", stringHttpEntity, CommentDto.class);

        assertNotNull(response.getBody());
        assertEquals(7L, response.getBody().getId());
        assertEquals("new content", response.getBody().getContent());
        assertEquals(2L, response.getBody().getCommentator().getId());
        assertEquals("pet89rov@mail.ru", response.getBody().getCommentator().getEmail());
        assertEquals(response.getBody().getCommentator().getRole(), Role.ROLE_USER);
        assertSame(response.getStatusCode(), HttpStatus.CREATED);
    }

    @Test
    @Order(32)
    @DisplayName("Test add comment to existent task by not task executor")
    void testAddCommentToExistentTaskIdByNotTaskExecutor() {
        HttpEntity<String> stringHttpEntity = new HttpEntity<>("new content", httpHeadersForUser);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.postForEntity(COMMENTS_ENDPOINT + "/task/2", stringHttpEntity, ResponseMessageDto.class);

        assertNotNull(response.getBody());
        assertEquals("ResponseMessageDto(url=/rest/v1/comments/task/2, message=Access denied. User does not have the required permissions to get this task)", response.getBody().toString());
        assertSame(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(33)
    @DisplayName("Test add comment to nonexistent task by user with role ROLE_ADMIN or ROLE_USER")
    void testAddCommentToNonexistentTask() {
        HttpEntity<String> stringHttpEntity = new HttpEntity<>("new content", httpHeadersForUser);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.postForEntity(COMMENTS_ENDPOINT + "/task/11", stringHttpEntity, ResponseMessageDto.class);

        assertNotNull(response.getBody());
        assertEquals("ResponseMessageDto(url=/rest/v1/comments/task/11, message=Task with id 11 is not found)", response.getBody().toString());
        assertSame(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(20)
    @DisplayName("Test delete nonexistent comment by user with role ROLE_ADMIN or ROLE_USER")
    void testDeleteNonExistentComment(){
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(httpHeadersForAdmin);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.exchange(COMMENTS_ENDPOINT + "/15", HttpMethod.DELETE, stringHttpEntity, ResponseMessageDto.class);

        assertNotNull(response.getBody());
        assertEquals("ResponseMessageDto(url=/rest/v1/comments/15, message=Comment with id 15 is not found)", response.getBody().toString());
        assertSame(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(21)
    @DisplayName("Test delete comment (by id) by not its author")
    void testDeleteCommentByNotItsAuthor(){
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(httpHeadersForAdmin);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.exchange(COMMENTS_ENDPOINT + "/5", HttpMethod.DELETE, stringHttpEntity, ResponseMessageDto.class);

        assertNotNull(response.getBody());
        assertEquals("ResponseMessageDto(url=/rest/v1/comments/5, message=Access denied. This user cannot delete this comment because he/she is not the comment author.)", response.getBody().toString());
        assertSame(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(22)
    @DisplayName("Test delete comment (by id) by its author")
    void testDeleteCommentByItsAuthor(){
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(httpHeadersForUser);
        ResponseEntity<Void> response = testRestTemplate.exchange(COMMENTS_ENDPOINT + "/5", HttpMethod.DELETE, stringHttpEntity, Void.class);

        assertNull(response.getBody());
        assertSame(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

}