package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    void init() {
        taskManager = createTaskManager();
    }

    @Test
    void createTaskTest() {
        Task task = new Task(89, "task name", "task description");
        int taskId = taskManager.createTask(task);

        Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask);
        assertEquals(task, savedTask);
    }

    @Test
    void createEpicTest() {
        Epic epic = new Epic(2, "epic name", "epic description");
        int epicId = taskManager.createEpic(epic);

        Epic savedEpic = taskManager.getEpic(epicId);
        System.out.println(epicId);

        assertNotNull(savedEpic);
        assertEquals(epic, savedEpic);
    }

    @Test
    void createSubtaskTest() {
        Epic epic = new Epic("epic name", "epic description");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(10, "subtask name", "subtask description", Status.NEW, epicId);
        int subtaskId = taskManager.createSubtask(subtask);

        Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask);
        assertEquals(subtask, savedSubtask);
    }

    @Test
    void updateTaskTest() {
        Task task = new Task(89, "task name", "task description");
        int taskId = taskManager.createTask(task);
        Task newTask = new Task(taskId, "new task name", "new task description", Status.DONE);
        Task newTaskIncorrectId = new Task(5, "broken task name", "broken description", Status.DONE);
        Task newTaskIncorrectDuration = new Task(taskId, "broken duration task name",
                "broken duration description", Status.DONE,
                Instant.parse("2022-01-01T14:00:00Z"), Duration.ofMinutes(-1));

        taskManager.updateTask(newTask);

        Task updatedTask = taskManager.getTask(taskId);
        assertEquals(task, updatedTask);

        taskManager.updateTask(newTaskIncorrectId);
        updatedTask = taskManager.getTask(taskId);
        assertEquals(task, updatedTask);

        taskManager.updateTask(newTaskIncorrectDuration);
        updatedTask = taskManager.getTask(taskId);
        assertEquals(task, updatedTask);
    }

    @Test
    void updateEpicTest() {
        Epic epic = new Epic("epic name", "epic description");
        int epicId = taskManager.createEpic(epic);
        Epic newEpic = new Epic(epicId, "new epic name", "new epic description");

        taskManager.updateEpic(newEpic);

        Epic updatedEpic = taskManager.getEpic(epicId);
        assertEquals(epic, updatedEpic);
    }

    @Test
    void updateSubtaskTest() {
        Epic epic = new Epic("epic name", "epic description");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("subtask name", "subtask description", epicId);
        int subtaskId = taskManager.createSubtask(subtask);
        Subtask newSubtask = new Subtask(subtaskId, "subtask name", "subtask description",
                Status.IN_PROGRESS, epicId);
        taskManager.updateSubtask(newSubtask);

        Subtask updatedSubtask = taskManager.getSubtask(subtaskId);
        assertEquals(subtask, updatedSubtask);
    }

    @Test
    void removeTaskTest() {
        Task task = new Task(89, "task name", "task description");
        int taskId = taskManager.createTask(task);

        taskManager.removeTask(taskId);

        assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    void removeEpicTest() {
        Epic epic = new Epic("epic name", "epic description");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("subtask name", "subtask description", epicId);
        taskManager.createSubtask(subtask);

        taskManager.removeEpic(epicId);

        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    void removeSubtaskTest() {
        Epic epic = new Epic("epic name", "epic description");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("subtask name", "subtask description", epicId);
        int subtaskId = taskManager.createSubtask(subtask);

        taskManager.removeSubtask(subtaskId);

        assertEquals(1, taskManager.getEpics().size());
        assertTrue(taskManager.getSubtasks().isEmpty());
        assertTrue(taskManager.getEpic(epicId).getSubtasks().isEmpty());
    }

    @Test
    void removeAllTaskTest() {
        Task task1 = new Task(11, "task1 name", "task1 description");
        taskManager.createTask(task1);
        Task task2 = new Task(22, "task2 name", "task2 description");
        taskManager.createTask(task2);
        Task task3 = new Task(33, "task3 name", "task3 description");
        taskManager.createTask(task3);

        taskManager.removeAllTask();

        assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    void removeAllSubtaskTest() {
        Epic epic1 = new Epic("epic1 name", "epic1 description");
        int epic1Id = taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1 name", "subtask1 description", epic1Id);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2 name", "subtask2 description", epic1Id);
        taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("epic2 name", "epic2 description");
        int epic2Id = taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("subtask3 name", "subtask3 description", epic2Id);
        taskManager.createSubtask(subtask3);

        taskManager.removeAllSubtask();

        assertTrue(taskManager.getEpic(epic1Id).getSubtasks().isEmpty());
        assertTrue(taskManager.getEpic(epic2Id).getSubtasks().isEmpty());
    }


}
