package tasks;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import utils.Manager;

import java.time.Duration;
import java.time.Instant;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return Manager.getDefault();
    }

    @Test
    void addToHistoryTest() {
        Task task = new Task(1, "task name", "task description", Status.NEW);
        int taskId = taskManager.createTask(task);
        taskManager.getTask(1);

        Task newTask = new Task(taskId, "new task name", "new task description", Status.DONE);

        taskManager.updateTask(newTask);
        taskManager.getTask(1);

        assertEquals(task, taskManager.getHistory().get(0));
    }

    @Test
    void calculateStatusEpicTest() {
        Epic epic = new Epic("epic name", "epic description");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("subtask name", "subtask description", epicId);
        int subtaskId = taskManager.createSubtask(subtask);

        assertEquals(Status.NEW, epic.getStatus());

        Subtask newSubtask = new Subtask(subtaskId, "subtask name", "subtask description",
                Status.IN_PROGRESS, epicId);
        taskManager.updateSubtask(newSubtask);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void isIntersectTest() {
        Task task1 = new Task("Task1", "Task 1 Description",
                Instant.parse("2022-01-01T10:00:00Z"), Duration.ofMinutes(60));
        Task task2 = new Task("Task2", "Task 2 Description",
                Instant.parse("2022-01-01T11:00:00Z"), Duration.ofMinutes(5));
        assertFalse(taskManager.isIntersect(task1, task2));

        Task task3 = new Task("Task3", "Task 3 Description",
                Instant.parse("2022-01-01T12:00:00Z"), Duration.ofMinutes(60));
        Task task4 = new Task("Task4", "Task 4 Description",
                Instant.parse("2022-01-01T11:30:00Z"), Duration.ofMinutes(60));
        assertTrue(taskManager.isIntersect(task3, task4));

        Task task5 = new Task("Task5", "Task 5 Description",
                Instant.parse("2022-01-01T14:00:00Z"), Duration.ofMinutes(60));
        Task task6 = new Task("Task6", "Task 6 Description",
                Instant.parse("2022-01-01T14:30:00Z"), Duration.ofMinutes(60));
        assertTrue(taskManager.isIntersect(task5, task6));

        Task task7 = new Task("Task7", "Task 7 Description",
                Instant.parse("2022-01-01T16:00:00Z"), Duration.ofMinutes(60));
        Task task8 = new Task("Task8", "Task 8 Description",
                Instant.parse("2022-01-01T19:00:00Z"), Duration.ofMinutes(90));
        assertFalse(taskManager.isIntersect(task7, task8));

        Task task9 = new Task("Task9", "Task 9 Description",
                Instant.parse("2022-01-01T22:00:00Z"), Duration.ofMinutes(0));
        Task task10 = new Task("Task10", "Task 10 Description",
                Instant.parse("2022-01-01T22:00:00Z"), Duration.ofMinutes(60));
        assertFalse(taskManager.isIntersect(task9, task10));
    }

    @Test
    void calculateDurationEpicTest() {
        Epic epic = new Epic("epic name", "epic description");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(24, "subtask1 name", "subtask1 description",
                Status.IN_PROGRESS, Instant.parse("2022-01-01T22:00:00Z"), Duration.ofMinutes(5), epicId);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(30, "subtask2 name", "subtask3 description",
                Status.IN_PROGRESS, Instant.parse("2022-01-01T13:00:00Z"), Duration.ofMinutes(30), epicId);
        taskManager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask(67, "subtask2 name", "subtask4 description",
                Status.IN_PROGRESS, Instant.parse("2022-01-01T09:00:00Z"), Duration.ofMinutes(80), epicId);
        taskManager.createSubtask(subtask3);

        assertEquals(115, epic.getDuration().get().toMinutes());
        assertEquals(Instant.parse("2022-01-01T09:00:00Z"), epic.getStartTime().get());
        assertEquals(Instant.parse("2022-01-01T22:05:00Z"), epic.getEndTime().get());
    }
}