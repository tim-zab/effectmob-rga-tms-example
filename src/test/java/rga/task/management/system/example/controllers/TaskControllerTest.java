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
import rga.task.management.system.example.dtos.TaskAddOrUpdateDto;
import rga.task.management.system.example.dtos.UserAuthResponseDto;
import rga.task.management.system.example.dtos.ResponseMessageDto;
import rga.task.management.system.example.dtos.TaskDto;
import rga.task.management.system.example.enums.Priority;
import rga.task.management.system.example.enums.Role;
import rga.task.management.system.example.enums.Status;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@TestPropertySource(locations = "/application-test.yml")
@DisplayName("Test CommentController methods:")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskControllerTest {

    private final TestRestTemplate testRestTemplate;

    private final String TASKS_ENDPOINT = "/rest/v1/tasks";

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
    @DisplayName("Test get task (by its id) by user with role ROLE_ADMIN")
    void testGetTaskByIdByAdmin() {
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(httpHeadersForAdmin);
        ResponseEntity<TaskDto> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/1", HttpMethod.GET, stringHttpEntity, TaskDto.class);

        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Task 1", response.getBody().getTitle());
        assertEquals("Description 1", response.getBody().getDescription());
        assertEquals(response.getBody().getStatus(), Status.PENDING);
        assertEquals(response.getBody().getPriority(), Priority.LOW);
        assertEquals(1L, response.getBody().getAuthor().getId());
        assertEquals(2L, response.getBody().getExecutor().getId());
        assertSame(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    @Order(11)
    @DisplayName("Test get task (by its id) by task executor with role ROLE_USER")
    void testGetTaskByIdByTaskExecutor() {
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(httpHeadersForUser);
        ResponseEntity<TaskDto> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/1", HttpMethod.GET, stringHttpEntity, TaskDto.class);

        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Task 1", response.getBody().getTitle());
        assertEquals("Description 1", response.getBody().getDescription());
        assertEquals(response.getBody().getStatus(), Status.PENDING);
        assertEquals(response.getBody().getPriority(), Priority.LOW);
        assertEquals(1L, response.getBody().getAuthor().getId());
        assertEquals(2L, response.getBody().getExecutor().getId());
        assertSame(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    @Order(13)
    @DisplayName("Test get task (by its id) by unauthenticated user")
    void testGetTaskByIdByUnauthenticatedUser() {
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(null);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/2", HttpMethod.GET, stringHttpEntity, ResponseMessageDto.class);

        assertNull(response.getBody());
        assertSame(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(12)
    @DisplayName("Test get task (by its id) by not task executor with role ROLE_USER")
    void testGetTaskByIdByNotTaskExecutor() {
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(httpHeadersForUser);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/2", HttpMethod.GET, stringHttpEntity, ResponseMessageDto.class);

        assertNotNull(response.getBody());
        assertEquals("ResponseMessageDto(url=/rest/v1/tasks/2, message=Access denied. User does not have the required permissions to get this task)", response.getBody().toString());
        assertSame(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(30)
    @DisplayName("Test get tasks page (by executor id) by user with role ROLE_ADMIN")
    void testGetTasksPageByExecutorIdByAdmin(){
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(httpHeadersForAdmin);
        ResponseEntity<String> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/executor/2", HttpMethod.GET, stringHttpEntity, String.class);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("[{\"id\":1,\"title\":\"Task 1\",\"description\":\"Description 1\",\"status\":\"PENDING\",\"priority\":\"LOW\",\"timestamp\":"));
        assertTrue(response.getBody().contains(",\"Author\":{\"id\":1,\"e-mail\":\"ivanov123@mail.ru\",\"user role\":\"ROLE_ADMIN\"},\"Executor\":{\"id\":2,\"e-mail\":\"pet89rov@mail.ru\",\"user role\":\"ROLE_USER\"}}]"));
        assertSame(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    @Order(20)
    @DisplayName("Test get tasks page (by author id) by user with role ROLE_ADMIN")
    void testGetTasksPageByAuthorIdByAdmin(){
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(httpHeadersForAdmin);
        ResponseEntity<String> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/author/1", HttpMethod.GET, stringHttpEntity, String.class);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("[{\"id\":1,\"title\":\"Task 1\",\"description\":\"Description 1\",\"status\":\"PENDING\",\"priority\":\"LOW\",\"timestamp\":"));
        assertTrue(response.getBody().contains(",\"Author\":{\"id\":1,\"e-mail\":\"ivanov123@mail.ru\",\"user role\":\"ROLE_ADMIN\"},\"Executor\":{\"id\":2,\"e-mail\":\"pet89rov@mail.ru\",\"user role\":\"ROLE_USER\"}}]"));
        assertSame(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    @Order(31)
    @DisplayName("Test get tasks page (by executor id) by tasks executor with role ROLE_USER")
    void testGetTasksPageByExecutorIdByTasksExecutor(){
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(httpHeadersForUser);
        ResponseEntity<String> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/executor/2", HttpMethod.GET, stringHttpEntity, String.class);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("[{\"id\":1,\"title\":\"Task 1\",\"description\":\"Description 1\",\"status\":\"PENDING\",\"priority\":\"LOW\",\"timestamp\":"));
        assertTrue(response.getBody().contains(",\"Author\":{\"id\":1,\"e-mail\":\"ivanov123@mail.ru\",\"user role\":\"ROLE_ADMIN\"},\"Executor\":{\"id\":2,\"e-mail\":\"pet89rov@mail.ru\",\"user role\":\"ROLE_USER\"}}]"));
        assertSame(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    @Order(32)
    @DisplayName("Test get tasks page (by executor id) by not tasks executor with role ROLE_USER")
    void testGetTasksPageByExecutorIdByNotTasksExecutor(){
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(httpHeadersForUser);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/executor/3", HttpMethod.GET, stringHttpEntity, ResponseMessageDto.class);

        assertNotNull(response.getBody());
        assertEquals("ResponseMessageDto(url=/rest/v1/tasks/executor/3, message=Access denied. User does not have the required permissions to get tasks page by this executor id)", response.getBody().toString());
        assertSame(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(21)
    @DisplayName("Test get tasks page (by author id) by user with role ROLE_USER")
    void testGetTasksPageByAuthorIdByUser(){
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(httpHeadersForUser);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/author/1", HttpMethod.GET, stringHttpEntity, ResponseMessageDto.class);

        assertNotNull(response.getBody());
        assertEquals("ResponseMessageDto(url=/rest/v1/tasks/author/1, message=Access denied. User does not have the required permissions to get tasks page by author id)", response.getBody().toString());
        assertSame(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(33)
    @DisplayName("Test get tasks page (by nonexistent executor id) by user with role ROLE_ADMIN")
    void testGetTasksPageByNonexistentExecutorIdByAdmin(){
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(httpHeadersForAdmin);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/executor/33", HttpMethod.GET, stringHttpEntity, ResponseMessageDto.class);

        assertNotNull(response.getBody());
        assertEquals("ResponseMessageDto(url=/rest/v1/tasks/executor/33, message=User with id 33 is not found)", response.getBody().toString());
        assertSame(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(34)
    @DisplayName("Test get tasks page (by nonexistent executor id) by user with role ROLE_USER")
    void testGetTasksPageByNonexistentExecutorIdByUser(){
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(httpHeadersForUser);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/executor/33", HttpMethod.GET, stringHttpEntity, ResponseMessageDto.class);

        assertNotNull(response.getBody());
        assertEquals("ResponseMessageDto(url=/rest/v1/tasks/executor/33, message=Access denied. User does not have the required permissions to get tasks page by this executor id)", response.getBody().toString());
        assertSame(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(23)
    @DisplayName("Test get tasks page (by nonexistent author id) by user with role ROLE_USER")
    void testGetTasksPageByNonexistentAuthorIdByUser(){
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(httpHeadersForUser);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/author/31", HttpMethod.GET, stringHttpEntity, ResponseMessageDto.class);

        assertNotNull(response.getBody());
        assertEquals("ResponseMessageDto(url=/rest/v1/tasks/author/31, message=Access denied. User does not have the required permissions to get tasks page by author id)", response.getBody().toString());
        assertSame(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(22)
    @DisplayName("Test get tasks page (by nonexistent author id) by user with role ROLE_ADMIN")
    void testGetTasksPageByNonexistentAuthorIdByAdmin(){
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(httpHeadersForAdmin);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/author/31", HttpMethod.GET, stringHttpEntity, ResponseMessageDto.class);

        assertNotNull(response.getBody());
        assertEquals("ResponseMessageDto(url=/rest/v1/tasks/author/31, message=User with id 31 is not found)", response.getBody().toString());
        assertSame(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(70)
    @DisplayName("Test delete task (by id) by user with role ROLE_USER")
    void testDeleteTaskByIdByUser(){
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(httpHeadersForUser);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/1", HttpMethod.DELETE, stringHttpEntity, ResponseMessageDto.class);

        assertNotNull(response.getBody());
        assertEquals("ResponseMessageDto(url=/rest/v1/tasks/1, message=Access denied. User does not have the required permissions to delete task)", response.getBody().toString());
        assertSame(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(71)
    @DisplayName("Test delete task (by nonexistent id) by user with role ROLE_ADMIN")
    void testDeleteTaskByNonexistentIdByAdmin(){
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(httpHeadersForAdmin);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/111", HttpMethod.DELETE, stringHttpEntity, ResponseMessageDto.class);

        assertNotNull(response.getBody());
        assertEquals("ResponseMessageDto(url=/rest/v1/tasks/111, message=Task with id 111 is not found)", response.getBody().toString());
        assertSame(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(72)
    @DisplayName("Test delete task (by nonexistent id) by user with role ROLE_USER")
    void testDeleteTaskByNonexistentIdByUser(){
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(httpHeadersForUser);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/111", HttpMethod.DELETE, stringHttpEntity, ResponseMessageDto.class);

        assertNotNull(response.getBody());
        assertEquals("ResponseMessageDto(url=/rest/v1/tasks/111, message=Access denied. User does not have the required permissions to delete task)", response.getBody().toString());
        assertSame(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(73)
    @DisplayName("Test delete task (by id) by user with role ROLE_ADMIN")
    void testDeleteTaskByIdByAdmin(){
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(httpHeadersForAdmin);
        ResponseEntity<Void> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/1", HttpMethod.DELETE, stringHttpEntity, Void.class);

        assertNull(response.getBody());
        assertSame(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    @Order(51)
    @DisplayName("Test add new task by user with role ROLE_ADMIN")
    void testAddNewTaskByAdmin(){
        TaskAddOrUpdateDto dto = new TaskAddOrUpdateDto(
                "New title",
                "New description",
                Status.PENDING,
                Priority.MEDIUM,
                "pet89rov@mail.ru");
        HttpEntity<TaskAddOrUpdateDto> httpEntity = new HttpEntity<>(dto, httpHeadersForAdmin);
        ResponseEntity<TaskDto> response = testRestTemplate.postForEntity(TASKS_ENDPOINT + "/", httpEntity, TaskDto.class);

        assertNotNull(response.getBody());
        assertEquals(4L, response.getBody().getId());
        assertEquals("New title", response.getBody().getTitle());
        assertEquals("New description", response.getBody().getDescription());
        assertEquals(response.getBody().getStatus(), Status.PENDING);
        assertEquals(response.getBody().getPriority(), Priority.MEDIUM);
        assertEquals(1L, response.getBody().getAuthor().getId());
        assertEquals("ivanov123@mail.ru", response.getBody().getAuthor().getEmail());
        assertEquals(response.getBody().getAuthor().getRole(), Role.ROLE_ADMIN);
        assertEquals(2L, response.getBody().getExecutor().getId());
        assertEquals("pet89rov@mail.ru", response.getBody().getExecutor().getEmail());
        assertEquals(response.getBody().getExecutor().getRole(), Role.ROLE_USER);
        assertSame(response.getStatusCode(), HttpStatus.CREATED);
    }

    @Test
    @Order(63)
    @DisplayName("Test update task by user with role ROLE_ADMIN")
    void testUpdateTaskByAdmin(){
        TaskAddOrUpdateDto dto = new TaskAddOrUpdateDto(
                "New task title",
                "New description",
                Status.IN_PROGRESS,
                Priority.HIGH,
                "pet89rov@mail.ru");
        HttpEntity<TaskAddOrUpdateDto> httpEntity = new HttpEntity<>(dto, httpHeadersForAdmin);
        ResponseEntity<TaskDto> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/1", HttpMethod.PUT, httpEntity, TaskDto.class);

        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("New task title", response.getBody().getTitle());
        assertEquals("New description", response.getBody().getDescription());
        assertEquals(response.getBody().getStatus(), Status.IN_PROGRESS);
        assertEquals(response.getBody().getPriority(), Priority.HIGH);
        assertEquals(1L, response.getBody().getAuthor().getId());
        assertEquals("ivanov123@mail.ru", response.getBody().getAuthor().getEmail());
        assertEquals(response.getBody().getAuthor().getRole(), Role.ROLE_ADMIN);
        assertEquals(2L, response.getBody().getExecutor().getId());
        assertEquals("pet89rov@mail.ru", response.getBody().getExecutor().getEmail());
        assertEquals(response.getBody().getExecutor().getRole(), Role.ROLE_USER);
        assertSame(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    @Order(43)
    @DisplayName("Test update task status by user with role ROLE_ADMIN")
    void testUpdateTaskStatusByAdmin(){
        HttpEntity<Status> httpEntity = new HttpEntity<>(Status.PENDING, httpHeadersForAdmin);
        ResponseEntity<TaskDto> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/3/status", HttpMethod.PUT, httpEntity, TaskDto.class);

        assertNotNull(response.getBody());
        assertEquals(3L, response.getBody().getId());
        assertEquals("Task 3", response.getBody().getTitle());
        assertEquals("Description 3", response.getBody().getDescription());
        assertEquals(response.getBody().getStatus(), Status.PENDING);
        assertEquals(response.getBody().getPriority(), Priority.HIGH);
        assertEquals(1L, response.getBody().getAuthor().getId());
        assertEquals("ivanov123@mail.ru", response.getBody().getAuthor().getEmail());
        assertEquals(response.getBody().getAuthor().getRole(), Role.ROLE_ADMIN);
        assertEquals(2L, response.getBody().getExecutor().getId());
        assertEquals("pet89rov@mail.ru", response.getBody().getExecutor().getEmail());
        assertEquals(response.getBody().getExecutor().getRole(), Role.ROLE_USER);
        assertSame(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    @Order(42)
    @DisplayName("Test update task status by executor with role ROLE_USER")
    void testUpdateTaskStatusByExecutor(){
        HttpEntity<Status> httpEntity = new HttpEntity<>(Status.IN_PROGRESS, httpHeadersForUser);
        ResponseEntity<TaskDto> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/3/status", HttpMethod.PUT, httpEntity, TaskDto.class);

        assertNotNull(response.getBody());
        assertEquals(3L, response.getBody().getId());
        assertEquals("Task 3", response.getBody().getTitle());
        assertEquals("Description 3", response.getBody().getDescription());
        assertEquals(response.getBody().getStatus(), Status.IN_PROGRESS);
        assertEquals(response.getBody().getPriority(), Priority.HIGH);
        assertEquals(1L, response.getBody().getAuthor().getId());
        assertEquals("ivanov123@mail.ru", response.getBody().getAuthor().getEmail());
        assertEquals(response.getBody().getAuthor().getRole(), Role.ROLE_ADMIN);
        assertEquals(2L, response.getBody().getExecutor().getId());
        assertEquals("pet89rov@mail.ru", response.getBody().getExecutor().getEmail());
        assertEquals(response.getBody().getExecutor().getRole(), Role.ROLE_USER);
        assertSame(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    @Order(41)
    @DisplayName("Test update task status by not executor with role ROLE_USER")
    void testUpdateTaskStatusByNotExecutor(){
        HttpEntity<Status> httpEntity = new HttpEntity<>(Status.FINISHED, httpHeadersForUser);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/2/status", HttpMethod.PUT, httpEntity, ResponseMessageDto.class);

        assertNotNull(response.getBody());
        assertEquals("ResponseMessageDto(url=/rest/v1/tasks/2/status, message=Access denied. User does not have the required permissions to get this task)", response.getBody().toString());
        assertSame(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(60)
    @DisplayName("Test update task by user with role ROLE_USER")
    void testUpdateTaskByUser(){
        TaskAddOrUpdateDto dto = new TaskAddOrUpdateDto(
                "New task title",
                "New description",
                Status.FINISHED,
                Priority.HIGH,
                "pet89rov@mail.ru");
        HttpEntity<TaskAddOrUpdateDto> httpEntity = new HttpEntity<>(dto, httpHeadersForUser);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/1", HttpMethod.PUT, httpEntity, ResponseMessageDto.class);

        assertNotNull(response.getBody());
        assertEquals("ResponseMessageDto(url=/rest/v1/tasks/1, message=Access is denied. User does not have permission to update task.)", response.getBody().toString());
        assertSame(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(62)
    @DisplayName("Test update nonexistent task by user with role ROLE_USER")
    void testUpdateNonexistentTaskByUser(){
        TaskAddOrUpdateDto dto = new TaskAddOrUpdateDto(
                "New task title",
                "New description",
                Status.FINISHED,
                Priority.HIGH,
                "pet89rov@mail.ru");
        HttpEntity<TaskAddOrUpdateDto> httpEntity = new HttpEntity<>(dto, httpHeadersForUser);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/125", HttpMethod.PUT, httpEntity, ResponseMessageDto.class);

        assertNotNull(response.getBody());
        assertEquals("ResponseMessageDto(url=/rest/v1/tasks/125, message=Access is denied. User does not have permission to update task.)", response.getBody().toString());
        assertSame(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(61)
    @DisplayName("Test update nonexistent task by user with role ROLE_ADMIN")
    void testUpdateNonexistentTaskByAdmin(){
        TaskAddOrUpdateDto dto = new TaskAddOrUpdateDto(
                "New task title",
                "New description",
                Status.FINISHED,
                Priority.HIGH,
                "pet89rov@mail.ru");
        HttpEntity<TaskAddOrUpdateDto> httpEntity = new HttpEntity<>(dto, httpHeadersForAdmin);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/125", HttpMethod.PUT, httpEntity, ResponseMessageDto.class);

        assertNotNull(response.getBody());
        assertEquals("ResponseMessageDto(url=/rest/v1/tasks/125, message=Task with id 125 is not found)", response.getBody().toString());
        assertSame(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(50)
    @DisplayName("Test add new task by user with role ROLE_USER")
    void testAddNewTaskByUser(){
        TaskAddOrUpdateDto dto = new TaskAddOrUpdateDto(
                "New title",
                "New description",
                Status.PENDING,
                Priority.MEDIUM,
                "pet89rov@mail.ru");
        HttpEntity<TaskAddOrUpdateDto> httpEntity = new HttpEntity<>(dto, httpHeadersForUser);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.postForEntity(TASKS_ENDPOINT + "/", httpEntity, ResponseMessageDto.class);

        assertNotNull(response.getBody());
        assertEquals("ResponseMessageDto(url=/rest/v1/tasks/, message=Access is denied. User does not have permission to create task.)", response.getBody().toString());
        assertSame(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(52)
    @DisplayName("Test add new task by unauthenticated user")
    void testAddNewTaskByUnauthenticatedUser(){
        TaskAddOrUpdateDto dto = new TaskAddOrUpdateDto(
                "New title",
                "New description",
                Status.PENDING,
                Priority.MEDIUM,
                "pet89rov@mail.ru");
        HttpEntity<TaskAddOrUpdateDto> httpEntity = new HttpEntity<>(dto, null);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.postForEntity(TASKS_ENDPOINT + "/", httpEntity, ResponseMessageDto.class);

        assertNull(response.getBody());
        assertSame(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(64)
    @DisplayName("Test update task by unauthenticated user")
    void testUpdateTaskByUnauthenticatedUser(){
        TaskAddOrUpdateDto dto = new TaskAddOrUpdateDto(
                "New task title",
                "New description",
                Status.FINISHED,
                Priority.HIGH,
                "pet89rov@mail.ru");
        HttpEntity<TaskAddOrUpdateDto> httpEntity = new HttpEntity<>(dto, null);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/125", HttpMethod.PUT, httpEntity, ResponseMessageDto.class);

        assertNull(response.getBody());
        assertSame(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(40)
    @DisplayName("Test update task status by unauthenticated user")
    void testUpdateTaskStatusByUnauthenticatedUser(){
        HttpEntity<Status> httpEntity = new HttpEntity<>(Status.FINISHED, null);
        ResponseEntity<ResponseMessageDto> response = testRestTemplate.exchange(TASKS_ENDPOINT + "/2/status", HttpMethod.PUT, httpEntity, ResponseMessageDto.class);

        assertNull(response.getBody());
        assertSame(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

}
