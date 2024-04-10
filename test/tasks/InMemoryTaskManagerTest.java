package tasks;

import org.junit.jupiter.api.*;
import utils.Manager;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Manager.getDefault();
        assertNotNull(taskManager);
    }

    @Test
    void createTask() {
        Task task = new Task(89, "task name", "task description");
        int taskId = taskManager.createTask(task);

        Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask);
        assertEquals(task, savedTask);
    }

    @Test
    void createEpic() {
        Epic epic = new Epic(2, "epic name", "epic description");
        int epicId = taskManager.createEpic(epic);

        Epic savedEpic = taskManager.getEpic(epicId);
        System.out.println(epicId);

        assertNotNull(savedEpic);
        assertEquals(epic, savedEpic);
    }

    @Test
    void createSubtask() {
        Epic epic = new Epic("epic name", "epic description");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(10, "subtask name", "subtask description", Status.NEW, epicId);
        int subtaskId = taskManager.createSubtask(subtask);

        Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask);
        assertEquals(subtask, savedSubtask);
    }

    @Test
    void updateTask() {
        Task task = new Task(89, "task name", "task description");
        int taskId = taskManager.createTask(task);
        Task newTask = new Task(taskId, "new task name", "new task description", Status.DONE);
        Task newTaskIncorrectId = new Task(5, "broken task name", "broken description", Status.DONE);

        taskManager.updateTask(newTask);

        Task updatedTask = taskManager.getTask(taskId);
        assertEquals(task, updatedTask);

        taskManager.updateTask(newTaskIncorrectId);
        updatedTask = taskManager.getTask(taskId);
        assertEquals(task, updatedTask);
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("epic name", "epic description");
        int epicId = taskManager.createEpic(epic);
        Epic newEpic = new Epic(epicId, "new epic name", "new epic description");

        taskManager. updateEpic(newEpic);

        Epic updatedEpic = taskManager.getEpic(epicId);
        assertEquals(epic, updatedEpic);
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("epic name", "epic description");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("subtask name", "subtask description", epicId);
        int subtaskId = taskManager.createSubtask(subtask);
        Subtask newSubtask = new Subtask(subtaskId, "subtask name", "subtask description", Status.IN_PROGRESS, epicId);
        taskManager.updateSubtask(newSubtask);

        Subtask updatedSubtask = taskManager.getSubtask(subtaskId);
        assertEquals(subtask, updatedSubtask);
    }

    @Test
    void addToHistory() {
        Task task = new Task(1, "task name", "task description", Status.NEW);
        int taskId = taskManager.createTask(task);
        Task returnedTask = taskManager.getTask(taskId);

        Task newTask = new Task(taskId, "new task name", "new task description", Status.DONE);

        taskManager.updateTask(newTask);

        assertEquals(task, taskManager.getHistory().get(0));
    }

    @Test
    void calculateStatusEpic() {
        Epic epic = new Epic("epic name", "epic description");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("subtask name", "subtask description", epicId);
        int subtaskId = taskManager.createSubtask(subtask);

        assertEquals(Status.NEW, epic.getStatus());

        Subtask newSubtask = new Subtask(subtaskId, "subtask name", "subtask description", Status.IN_PROGRESS, epicId);
        taskManager.updateSubtask(newSubtask);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}