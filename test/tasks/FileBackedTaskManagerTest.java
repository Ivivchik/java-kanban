package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Manager;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private File f;
    private FileBackedTaskManager fm;

    @BeforeEach
    void init() throws IOException {
        f = File.createTempFile("test", "fileBackedManager");
        fm = Manager.getFileTaskManager(f);
    }

    @Test
    void testLoadTaskToFile() {

        Task task = new Task("task", "desc for task");
        int taskId = fm.createTask(task);

        FileBackedTaskManager.loadFromFile(f);

        Task taskFromFile = fm.getTask(taskId);

        assertEquals(task, taskFromFile);

    }

    @Test
    void testRemoveFromFile() {

        Task task = new Task("task", "desc for task");
        int taskId = fm.createTask(task);

        FileBackedTaskManager.loadFromFile(f);

        fm.removeTask(taskId);

        assertTrue(fm.getTasks().isEmpty());
    }

    @Test
    void testReadEmptyFile() {

        FileBackedTaskManager.loadFromFile(f);

        assertTrue(fm.getTasks().isEmpty());
        assertTrue(fm.getSubtasks().isEmpty());
        assertTrue(fm.getEpics().isEmpty());
        assertTrue(fm.getHistory().isEmpty());
    }


    @Test
    void testReadSomeTaskFromFile() {

        Task task1 = new Task("task 1", "desc for task1");
        Task task2 = new Task("task 2", "desc for task2");

        fm.createTask(task1);
        fm.createTask(task2);

        Epic epic = new Epic("epic 1", "desc for epic");

        int epicId = fm.createEpic(epic);

        Subtask subtask1 = new Subtask("subtask1", "desc for subtask1", epicId);
        Subtask subtask2 = new Subtask("subtask2", "desc for subtask2", epicId);

        fm.createSubtask(subtask1);
        fm.createSubtask(subtask2);

        FileBackedTaskManager.loadFromFile(f);


        assertEquals(2, fm.getTasks().size());
        assertEquals(1, fm.getEpics().size());
        assertEquals(2, fm.getSubtasks().size());
    }

    @Test
    void testHistoryFromFile() {

        Task task1 = new Task("task 1", "desc for task1");
        Task task2 = new Task("task 2", "desc for task2");
        int task1Id = fm.createTask(task1);
        int task2Id = fm.createTask(task2);

        Epic epic1 = new Epic("epic 1", "desc for epic1");
        Epic epic2 = new Epic("epic 2", "desc for epic2");
        int epic1Id = fm.createEpic(epic1);
        int epic2Id = fm.createEpic(epic2);

        Subtask subtask1 = new Subtask("subtask1", "desc for subtask1", epic1Id);
        Subtask subtask2 = new Subtask("subtask2", "desc for subtask2", epic1Id);
        int subtask1Id = fm.createSubtask(subtask1);
        int subtask2Id = fm.createSubtask(subtask2);

        fm.getSubtask(subtask2Id);
        fm.getEpic(epic1Id);
        fm.getTask(task1Id);
        fm.getEpic(epic2Id);
        fm.getSubtask(subtask1Id);
        fm.getTask(task2Id);


        FileBackedTaskManager.loadFromFile(f);


        assertEquals(6, fm.getHistory().size());
    }

}