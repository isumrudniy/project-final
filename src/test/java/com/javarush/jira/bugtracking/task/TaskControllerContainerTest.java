package com.javarush.jira.bugtracking.task;

import com.javarush.jira.bugtracking.task.to.TaskToExt;
import com.javarush.jira.bugtracking.task.to.TaskToFull;
import com.javarush.jira.common.util.JsonUtil;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static com.javarush.jira.bugtracking.task.TaskTestData.*;
import static com.javarush.jira.common.util.JsonUtil.writeValue;
import static io.restassured.RestAssured.given;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskControllerContainerTest {

    @Value("${test.user}")
    private String testUser;

    @Value("${test.password}")
    private String testPassword;

    @LocalServerPort
    private Integer port;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    @BeforeAll
    static void beforeAll() {
        postgreSQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        postgreSQLContainer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    void getTask() {
        Response response = given()
                .auth()
                .preemptive()
                .basic(testUser, testPassword)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/tasks/" + TASK1_ID)
                .then()
                .extract().response();

        Assert.assertEquals(HttpStatus.OK.value(), response.statusCode());

        ObjectMapper objectMapper = new ObjectMapper();
        TaskToFull taskToFull = JsonUtil.readValue(response.body().asString(), TaskToFull.class);
        TASK_TO_FULL_MATCHER.assertMatch(taskToFull, taskToFull1);
    }

    @Test
    void updateTask() {
        TaskToExt updatedTo = TaskTestData.getUpdatedTaskTo();

        Response response = given()
                .auth()
                .preemptive()
                .basic(testUser, testPassword)
                .header("Content-type", "application/json")
                .and()
                .body(writeValue(updatedTo))
                .when()
                .put("/api/tasks/" + TASK2_ID)
                .then()
                .extract().response();

        Task updated = new Task(updatedTo.getId(), updatedTo.getTitle(), updatedTo.getTypeCode(), updatedTo.getStatusCode(), updatedTo.getParentId(), updatedTo.getProjectId(), updatedTo.getSprintId());
        TASK_MATCHER.assertMatch(taskRepository.getExisted(TASK2_ID), updated);
    }

    @Test
    void getUnAuth() {
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/tasks/" + TASK1_ID)
                .then()
                .extract().response();

        Assert.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.statusCode());
    }

}
