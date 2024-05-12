package http;

import utils.Manager;

import http.HttpTaskServer;

import tasks.*;

import com.google.gson.Gson;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {
    TaskManager taskManager = Manager.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = taskServer.getGson();

    private HttpResponse<String> getPostResponse(String endpoint, String taskJson) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080" + endpoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> getGetResponse(String endpoint) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080" + endpoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> getDeleteResponse(String endpoint) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080" + endpoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager.removeAllTask();
        taskManager.removeAllSubtask();
        taskManager.removeAllEpic();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    void testCreateTask() throws IOException, InterruptedException {
        Task task = new Task("Test create task", "Testing creation task", Instant.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpResponse<String> response = getPostResponse("/tasks", taskJson);

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getTasks();

        assertEquals(1, tasksFromManager.size());
        assertEquals("Test create task", tasksFromManager.get(0).getName());

        Task task1 = new Task("Test create task1", "Testing creation task1", Instant.now(), Duration.ofMinutes(5));
        taskJson = gson.toJson(task1);
        response = getPostResponse("/tasks", taskJson);

        assertEquals(406, response.statusCode());

        tasksFromManager = taskManager.getTasks();
        assertEquals(1, tasksFromManager.size());
        assertEquals("Test create task", tasksFromManager.get(0).getName());

    }

    @Test
    void testCreateEpic() throws IOException, InterruptedException {
        Epic task = new Epic("Test create epic", "Testing creation epic");
        String taskJson = gson.toJson(task);

        HttpResponse<String> response = getPostResponse("/epics", taskJson);

        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = taskManager.getEpics();

        assertEquals(1, tasksFromManager.size());
        assertEquals("Test create epic", tasksFromManager.get(0).getName());
    }

    @Test
    void testCreateSubtask() throws IOException, InterruptedException {
        Subtask task = new Subtask("Test create subtask", "Testing creation subtask", 1);
        String taskJson = gson.toJson(task);

        HttpResponse<String> response = getPostResponse("/subtasks", taskJson);

        assertEquals(404, response.statusCode());

        List<Subtask> tasksFromManager = taskManager.getSubtasks();

        assertTrue(tasksFromManager.isEmpty());

        Epic epic = new Epic("Test create epic", "Testing creation epic");
        String epicJson = gson.toJson(epic);
        getPostResponse("/epics", epicJson);

        response = getPostResponse("/subtasks", taskJson);

        assertEquals(201, response.statusCode());

        tasksFromManager = taskManager.getSubtasks();

        assertFalse(tasksFromManager.isEmpty());
        assertEquals(1, tasksFromManager.size());
        assertEquals("Test create subtask", tasksFromManager.get(0).getName());
    }

    @Test
    void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test create task", "Testing creation task",
                Instant.now(), Duration.ofMinutes(5));
        Task task1 = new Task("Test create task", "Testing creation task",
                Instant.now().plus(Duration.ofMinutes(10)), Duration.ofMinutes(5));
        taskManager.createTask(task);
        taskManager.createTask(task1);


        Task newTaskWithIncorrectId = new Task(10, "Test update task incorrect id",
                "Testing updating task", Status.IN_PROGRESS);
        String newTaskJson = gson.toJson(newTaskWithIncorrectId);
        HttpResponse<String> response = getPostResponse("/tasks", newTaskJson);

        assertEquals(404, response.statusCode());

        List<Task> tasksFromManager = taskManager.getTasks();

        assertEquals(2, tasksFromManager.size());
        assertEquals("Test create task", tasksFromManager.get(0).getName());
        assertEquals(Status.NEW, tasksFromManager.get(0).getStatus());


        Task newTaskWithInteraction = new Task(task.getId(), "Test create task time intersect",
                "Testing creation task", Status.IN_PROGRESS,
                Instant.now().plus(Duration.ofMinutes(9)), Duration.ofMinutes(5));
        newTaskJson = gson.toJson(newTaskWithInteraction);
        response = getPostResponse("/tasks", newTaskJson);

        assertEquals(406, response.statusCode());

        tasksFromManager = taskManager.getTasks();

        assertEquals(2, tasksFromManager.size());
        assertEquals("Test create task", tasksFromManager.get(0).getName());
        assertEquals(Status.NEW, tasksFromManager.get(0).getStatus());


        Task newTaskWithCorrectId = new Task(task.getId(), "Test update task",
                "Testing updating task", Status.IN_PROGRESS,
                Instant.now().plus(Duration.ofMinutes(20)), Duration.ofMinutes(5));
        newTaskJson = gson.toJson(newTaskWithCorrectId);
        response = getPostResponse("/tasks", newTaskJson);

        assertEquals(201, response.statusCode());

        tasksFromManager = taskManager.getTasks();
        assertEquals(2, tasksFromManager.size());
        assertEquals("Test update task", tasksFromManager.get(0).getName());
        assertEquals(Status.IN_PROGRESS, tasksFromManager.get(0).getStatus());
    }

    @Test
    void testUpdateEpic() throws IOException, InterruptedException {
        Epic task = new Epic("Test create epic", "Testing creation epic");
        taskManager.createEpic(task);

        Epic newEpicWithIncorrectId = new Epic(2, "Test update epic incorrect id", "Test update epic");
        String newTaskJson = gson.toJson(newEpicWithIncorrectId);

        HttpResponse<String> response = getPostResponse("/epics", newTaskJson);

        assertEquals(404, response.statusCode());

        List<Epic> tasksFromManager = taskManager.getEpics();

        assertEquals(1, tasksFromManager.size());
        assertEquals("Test create epic", tasksFromManager.get(0).getName());

        Epic newEpicWithCorrectId = new Epic(1, "Test update epic", "Test update epic");
        newTaskJson = gson.toJson(newEpicWithCorrectId);

        response = getPostResponse("/epics", newTaskJson);

        assertEquals(201, response.statusCode());

        tasksFromManager = taskManager.getEpics();

        assertEquals(1, tasksFromManager.size());
        assertEquals("Test update epic", tasksFromManager.get(0).getName());
    }

    @Test
    void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test create epic", "Testing creation epic");
        taskManager.createEpic(epic);
        Subtask task = new Subtask("Test create subtask", "Testing creation subtask", 1);
        taskManager.createSubtask(task);

        Subtask newSubtaskIncorrectID = new Subtask(3, "Test updated subtask", "Testing updating subtask", Status.IN_PROGRESS, 1);
        String newTaskJson = gson.toJson(newSubtaskIncorrectID);

        HttpResponse<String> response = getPostResponse("/subtasks", newTaskJson);

        assertEquals(404, response.statusCode());

        List<Subtask> tasksFromManager = taskManager.getSubtasks();

        assertEquals(1, tasksFromManager.size());
        assertEquals("Test create subtask", tasksFromManager.get(0).getName());


        Subtask newSubtaskWithAnotherEpicId = new Subtask(2, "Test updated subtask", "Testing updating subtask", Status.IN_PROGRESS, 10);
        newTaskJson = gson.toJson(newSubtaskWithAnotherEpicId);

        response = getPostResponse("/subtasks", newTaskJson);

        assertEquals(406, response.statusCode());

        tasksFromManager = taskManager.getSubtasks();

        assertEquals(1, tasksFromManager.size());
        assertEquals("Test create subtask", tasksFromManager.get(0).getName());
        assertEquals(Status.NEW, tasksFromManager.get(0).getStatus());

        Subtask newSubtaskWithCorrectId = new Subtask(2, "Test updated subtask", "Testing updating subtask", Status.IN_PROGRESS, 1);
        newTaskJson = gson.toJson(newSubtaskWithCorrectId);

        response = getPostResponse("/subtasks", newTaskJson);

        assertEquals(201, response.statusCode());

        tasksFromManager = taskManager.getSubtasks();

        assertEquals(1, tasksFromManager.size());
        assertEquals("Test updated subtask", tasksFromManager.get(0).getName());
        assertEquals(Status.IN_PROGRESS, tasksFromManager.get(0).getStatus());
    }

    @Test
    void testGetTask() throws IOException, InterruptedException {
        Task task = new Task("Test create task", "Testing creation task",
                Instant.now(), Duration.ofMinutes(5));
        taskManager.createTask(task);
        String taskJson = gson.toJson(task);

        HttpResponse<String> response = getGetResponse("/tasks/" + (task.getId() + 1));

        assertEquals(404, response.statusCode());

        List<Task> history = taskManager.getHistory();

        assertTrue(history.isEmpty());

        response = getGetResponse("/tasks/" + task.getId());

        assertEquals(200, response.statusCode());

        history = taskManager.getHistory();

        assertFalse(history.isEmpty());
        assertEquals(1, history.size());
        assertEquals(taskJson, response.body());
    }

    @Test
    void testGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test create epic", "Testing creation epic");
        taskManager.createEpic(epic);
        String taskJson = gson.toJson(epic);

        HttpResponse<String> response = getGetResponse("/epics/" + (epic.getId() + 1));

        assertEquals(404, response.statusCode());

        List<Task> history = taskManager.getHistory();

        assertTrue(history.isEmpty());

        response = getGetResponse("/epics/" + epic.getId());

        assertEquals(200, response.statusCode());

        history = taskManager.getHistory();

        assertFalse(history.isEmpty());
        assertEquals(1, history.size());
        assertEquals(taskJson, response.body());
    }

    @Test
    void testGetSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test create epic", "Testing creation epic");
        taskManager.createEpic(epic);
        Subtask task = new Subtask("Test create subtask", "Testing creation subtask", epic.getId());
        taskManager.createSubtask(task);
        String taskJson = gson.toJson(task);

        HttpResponse<String> response = getGetResponse("/subtasks/" + (task.getId() + 1));

        assertEquals(404, response.statusCode());

        List<Task> history = taskManager.getHistory();

        assertTrue(history.isEmpty());

        response = getGetResponse("/subtasks/" + task.getId());

        assertEquals(200, response.statusCode());

        history = taskManager.getHistory();

        assertFalse(history.isEmpty());
        assertEquals(1, history.size());
        assertEquals(taskJson, response.body());
    }

    @Test
    void testGetTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Test create task", "Testing creation task",
                Instant.now(), Duration.ofMinutes(5));
        Task task2 = new Task("Test create task", "Testing creation task",
                Instant.now().minus(Duration.ofMinutes(10)), Duration.ofMinutes(5));
        Task task3 = new Task("Test create task", "Testing creation task",
                Instant.now().plus(Duration.ofMinutes(10)), Duration.ofMinutes(5));

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        String arrJson = gson.toJson(taskManager.getTasks());

        HttpResponse<String> response = getGetResponse("/tasks");

        assertEquals(200, response.statusCode());
        assertEquals(arrJson, response.body());
    }

    @Test
    void testGetEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test create epic1", "Testing creation epic2");
        Epic epic2 = new Epic("Test create epic2", "Testing creation epic2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask task = new Subtask("Test create subtask", "Testing creation subtask", epic1.getId());
        taskManager.createSubtask(task);

        String arrJson = gson.toJson(taskManager.getEpics());

        HttpResponse<String> response = getGetResponse("/epics");

        assertEquals(200, response.statusCode());
        assertEquals(arrJson, response.body());
    }

    @Test
    void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test create epic1", "Testing creation epic2");
        Epic epic2 = new Epic("Test create epic2", "Testing creation epic2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask task1 = new Subtask("Test create subtask1", "Testing creation subtask1", epic1.getId());
        Subtask task2 = new Subtask("Test create subtask2", "Testing creation subtask2", epic1.getId());
        Subtask task3 = new Subtask("Test create subtask3", "Testing creation subtask3", epic2.getId());
        taskManager.createSubtask(task1);
        taskManager.createSubtask(task2);
        taskManager.createSubtask(task3);

        String arrJson = gson.toJson(taskManager.getSubtasks());

        HttpResponse<String> response = getGetResponse("/subtasks");

        assertEquals(200, response.statusCode());
        assertEquals(arrJson, response.body());
    }

    @Test
    void testDeleteTask() throws IOException, InterruptedException {
        Task task1 = new Task("Test create task1", "Testing creation task",
                Instant.now(), Duration.ofMinutes(5));
        Task task2 = new Task("Test create task2", "Testing creation task",
                Instant.now().minus(Duration.ofMinutes(10)), Duration.ofMinutes(5));
        Task task3 = new Task("Test create task3", "Testing creation task",
                Instant.now().plus(Duration.ofMinutes(10)), Duration.ofMinutes(5));

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        HttpResponse<String> response = getDeleteResponse("/tasks/" + (task1.getId() + 10));

        assertEquals(404, response.statusCode());

        List<Task> tasksFromManager = taskManager.getTasks();
        assertEquals(3, tasksFromManager.size());

        response = getDeleteResponse("/tasks/" + task2.getId());

        assertEquals(201, response.statusCode());

        tasksFromManager = taskManager.getTasks();

        assertEquals(2, tasksFromManager.size());
    }

    @Test
    void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test create epic1", "Testing creation epic2");
        Epic epic2 = new Epic("Test create epic2", "Testing creation epic2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask task = new Subtask("Test create subtask", "Testing creation subtask", epic1.getId());
        taskManager.createSubtask(task);

        HttpResponse<String> response = getDeleteResponse("/epics/" + (epic1.getId() + 10));

        assertEquals(404, response.statusCode());

        List<Epic> tasksFromManager = taskManager.getEpics();
        assertEquals(2, tasksFromManager.size());

        response = getDeleteResponse("/epics/" + epic1.getId());

        assertEquals(201, response.statusCode());

        tasksFromManager = taskManager.getEpics();

        assertEquals(1, tasksFromManager.size());
    }

    @Test
    void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test create epic1", "Testing creation epic2");
        Epic epic2 = new Epic("Test create epic2", "Testing creation epic2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask task1 = new Subtask("Test create subtask1", "Testing creation subtask1", epic1.getId());
        Subtask task2 = new Subtask("Test create subtask2", "Testing creation subtask2", epic1.getId());
        Subtask task3 = new Subtask("Test create subtask3", "Testing creation subtask3", epic2.getId());
        taskManager.createSubtask(task1);
        taskManager.createSubtask(task2);
        taskManager.createSubtask(task3);

        HttpResponse<String> response = getDeleteResponse("/subtasks/" + (task1.getId() + 10));

        assertEquals(404, response.statusCode());

        List<Subtask> tasksFromManager = taskManager.getSubtasks();
        assertEquals(3, tasksFromManager.size());

        response = getDeleteResponse("/subtasks/" + task1.getId());

        assertEquals(201, response.statusCode());

        tasksFromManager = taskManager.getSubtasks();

        assertEquals(2, tasksFromManager.size());
        assertEquals(1, epic1.getSubtasks().size());
    }

    @Test
    void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test create epic1", "Testing creation epic2");
        Epic epic2 = new Epic("Test create epic2", "Testing creation epic2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask task1 = new Subtask("Test create subtask1", "Testing creation subtask1", epic1.getId());
        Subtask task2 = new Subtask("Test create subtask2", "Testing creation subtask2", epic1.getId());
        Subtask task3 = new Subtask("Test create subtask3", "Testing creation subtask3", epic2.getId());
        taskManager.createSubtask(task1);
        taskManager.createSubtask(task2);
        taskManager.createSubtask(task3);

        String arrJson = gson.toJson(List.of(task1, task2));

        HttpResponse<String> response = getGetResponse("/epics/" + (epic1.getId() + 10) + "/subtasks");

        assertEquals(404, response.statusCode());

        response = getGetResponse("/epics/" + epic1.getId() + "/subtasks");

        assertEquals(arrJson, response.body());
    }

    @Test
    void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Test create task1", "Testing creation task",
                Instant.now(), Duration.ofMinutes(5));
        Task task2 = new Task("Test create task2", "Testing creation task",
                Instant.now().minus(Duration.ofMinutes(10)), Duration.ofMinutes(5));
        Task task3 = new Task("Test create task3", "Testing creation task",
                Instant.now().plus(Duration.ofMinutes(10)), Duration.ofMinutes(5));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        Epic epic1 = new Epic("Test create epic1", "Testing creation epic2");
        Epic epic2 = new Epic("Test create epic2", "Testing creation epic2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Test create subtask1", "Testing creation subtask1", epic1.getId());
        Subtask subtask2 = new Subtask("Test create subtask2", "Testing creation subtask2", epic1.getId());
        Subtask subtask3 = new Subtask("Test create subtask3", "Testing creation subtask3", epic2.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        taskManager.getTask(task1.getId());
        taskManager.getSubtask(subtask2.getId());
        taskManager.getEpic(epic2.getId());
        taskManager.getTask(task2.getId());

        String arrJson = gson.toJson(List.of(task1, subtask2, epic2, task2));

        HttpResponse<String> response = getGetResponse("/history");

        assertEquals(200, response.statusCode());

        assertEquals(arrJson, response.body());
    }

    @Test
    void testPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Test create task1", "Testing creation task",
                Instant.now(), Duration.ofMinutes(5));
        Task task2 = new Task("Test create task2", "Testing creation task",
                Instant.now().minus(Duration.ofMinutes(10)), Duration.ofMinutes(5));
        Task task3 = new Task("Test create task3", "Testing creation task",
                Instant.now().plus(Duration.ofMinutes(10)), Duration.ofMinutes(5));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        Epic epic1 = new Epic("Test create epic1", "Testing creation epic2");
        Epic epic2 = new Epic("Test create epic2", "Testing creation epic2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask(1, "Test create subtask1", "Testing creation subtask1",
                Status.NEW, Instant.now().minus(Duration.ofMinutes(60)), Duration.ofMinutes(10), epic1.getId());
        Subtask subtask2 = new Subtask("Test create subtask2", "Testing creation subtask2", epic1.getId());
        Subtask subtask3 = new Subtask(1, "Test create subtask3", "Testing creation subtask3",
                Status.DONE, Instant.now().plus(Duration.ofMinutes(60)), Duration.ofMinutes(10), epic2.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        String arrJson = gson.toJson(List.of(subtask1, task2, task1, task3, subtask3));

        HttpResponse<String> response = getGetResponse("/prioritized");

        assertEquals(200, response.statusCode());

        assertEquals(arrJson, response.body());
    }
}
