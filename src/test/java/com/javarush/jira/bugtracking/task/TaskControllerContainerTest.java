package com.javarush.jira.bugtracking.task;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.testcontainers.containers.PostgreSQLContainer;

import static com.javarush.jira.login.internal.web.UserTestData.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskControllerContainerTest {

    @LocalServerPort
    private Integer port;

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

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
//        taskRepository.deleteAll();
    }

    @Test
    void shouldGetAllTask() {
//        List<Task> tasks = List.of(
//                new Task(900L, "Data", "epic", "in_progress", null, 1L, null),
//                new Task(901L, "Trees", "epic", "in_progress", null, 1L, null)
//        );
//        taskRepository.saveAll(tasks);

        Response response = given()
                .auth().basic("admin@gmail.com", "admin")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/tasks/1")
                .then()
                .statusCode(200)
                .extract()
                .response();

        System.out.println(response.getBody().toString());

    }
}
