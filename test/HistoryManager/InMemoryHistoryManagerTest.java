package HistoryManager;

import TasksManager.Task;
import org.junit.jupiter.api.Test;
import utils.Manager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    static HistoryManager historyManager = Manager.getDefaultHistory();

    @Test
    void add() {
        Task task = new Task("new task name", "new task description");
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());
    }

}