package tasks;

import org.junit.jupiter.api.Test;
import utils.Manager;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File f;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            f = File.createTempFile("test", "fileBackedManager");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Manager.getFileTaskManager(f);
    }

    @Test
    void testLoadTaskToFile() {

        Task task = new Task("task", "desc for task");
        int taskId = taskManager.createTask(task);

        FileBackedTaskManager fbtm = FileBackedTaskManager.loadFromFile(f);

        Task taskFromFile = fbtm.getTask(taskId);

        assertEquals(task, taskFromFile);

    }

    @Test
    void testRemoveFromFile() {

        Task task = new Task("task", "desc for task");
        int taskId = taskManager.createTask(task);
        taskManager.removeTask(taskId);

        FileBackedTaskManager fbtm = FileBackedTaskManager.loadFromFile(f);

        assertTrue(fbtm.getTasks().isEmpty());
    }

    @Test
    void testReadEmptyFile() {

        FileBackedTaskManager fbtm = FileBackedTaskManager.loadFromFile(f);

        assertTrue(fbtm.getTasks().isEmpty());
        assertTrue(fbtm.getSubtasks().isEmpty());
        assertTrue(fbtm.getEpics().isEmpty());
        assertTrue(fbtm.getHistory().isEmpty());
    }


    @Test
    void testReadSomeTaskFromFile() {

        Task task1 = new Task("task 1", "desc for task1");
        Task task2 = new Task("task 2", "desc for task2");

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic = new Epic("epic 1", "desc for epic");

        int epicId = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("subtask1", "desc for subtask1", epicId);
        Subtask subtask2 = new Subtask("subtask2", "desc for subtask2", epicId);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        FileBackedTaskManager fbtm = FileBackedTaskManager.loadFromFile(f);

        assertEquals(2, fbtm.getTasks().size());
        assertEquals(1, fbtm.getEpics().size());
        assertEquals(2, fbtm.getSubtasks().size());
    }

    @Test
    void testReadHistoryFromFile() {

        Task task1 = new Task("task 1", "desc for task1");
        Task task2 = new Task("task 2", "desc for task2");
        int task1Id = taskManager.createTask(task1);
        int task2Id = taskManager.createTask(task2);

        Epic epic1 = new Epic("epic 1", "desc for epic1");
        Epic epic2 = new Epic("epic 2", "desc for epic2");
        int epic1Id = taskManager.createEpic(epic1);
        int epic2Id = taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("subtask1", "desc for subtask1", epic1Id);
        Subtask subtask2 = new Subtask("subtask2", "desc for subtask2", epic1Id);
        int subtask1Id = taskManager.createSubtask(subtask1);
        int subtask2Id = taskManager.createSubtask(subtask2);

        taskManager.getSubtask(subtask2Id);
        taskManager.getEpic(epic1Id);
        taskManager.getTask(task1Id);
        taskManager.getEpic(epic2Id);
        taskManager.getSubtask(subtask1Id);
        taskManager.getTask(task2Id);

        FileBackedTaskManager fbtm = FileBackedTaskManager.loadFromFile(f);

        assertEquals(6, fbtm.getHistory().size());
    }

    @Test
    void testUpdateCountId() {

        Task task1 = new Task("task 1", "desc for task1");
        Task task2 = new Task("task 2", "desc for task2");
        int task1Id = taskManager.createTask(task1);
        int task2Id = taskManager.createTask(task2);

        Epic epic1 = new Epic("epic 1", "desc for epic1");
        Epic epic2 = new Epic("epic 2", "desc for epic2");
        int epic1Id = taskManager.createEpic(epic1);
        int epic2Id = taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("subtask1", "desc for subtask1", epic1Id);
        Subtask subtask2 = new Subtask("subtask2", "desc for subtask2", epic1Id);
        int subtask1Id = taskManager.createSubtask(subtask1);
        int subtask2Id = taskManager.createSubtask(subtask2);

        taskManager.getSubtask(subtask2Id);
        taskManager.getEpic(epic1Id);
        taskManager.getTask(task1Id);
        taskManager.getEpic(epic2Id);
        taskManager.getSubtask(subtask1Id);
        taskManager.getTask(task2Id);

        FileBackedTaskManager fbtm = FileBackedTaskManager.loadFromFile(f);

        assertEquals(7, fbtm.cntId);
    }

    @Test
    void testReadEpicFromFile() {


        Epic epic1 = new Epic("epic 1", "desc for epic1");
        Epic epic2 = new Epic("epic 2", "desc for epic2");
        int epic1Id = taskManager.createEpic(epic1);
        int epic2Id = taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("subtask1", "desc for subtask1", epic1Id);
        Subtask subtask2 = new Subtask("subtask2", "desc for subtask2", epic1Id);
        int subtask1Id = taskManager.createSubtask(subtask1);
        int subtask2Id = taskManager.createSubtask(subtask2);

        FileBackedTaskManager fbtm = FileBackedTaskManager.loadFromFile(f);

        Integer[] subtasksIdEtalon = {subtask1Id, subtask2Id};
        Integer[] subtasksIdFromFile = {};
        subtasksIdFromFile = fbtm.getEpic(epic1Id).getSubtasks().toArray(subtasksIdFromFile);

        assertTrue(fbtm.getEpic(epic2Id).getSubtasks().isEmpty());
        assertArrayEquals(subtasksIdEtalon, subtasksIdFromFile);
    }
}