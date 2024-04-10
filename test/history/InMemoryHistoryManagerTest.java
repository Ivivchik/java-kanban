package history;

import tasks.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.Manager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static HistoryManager historyManager;

    @BeforeAll
    static void setUp() {
        historyManager = Manager.getDefaultHistory();
        assertNotNull(historyManager);
    }

    @Test
    void add() {
        Task task = new Task("new task name", "new task description");
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());
    }

    @Test
    void addMoreThan10Task() {
        Task task1 = new Task(1, "name 1", "description 1");
        Task task2 = new Task(2, "name 2", "description 2");
        Task task3 = new Task(3, "name 3", "description 3");
        Task task4 = new Task(4, "name 4", "description 4");
        Task task5 = new Task(5, "name 5", "description 5");
        Task task6 = new Task(6, "name 6", "description 6");
        Task task7 = new Task(7, "name 7", "description 7");
        Task task8 = new Task(8, "name 8", "description 8");
        Task task9 = new Task(9, "name 9", "description 9");
        Task task10 = new Task(10, "name 10", "description 10");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task5);
        historyManager.add(task6);
        historyManager.add(task7);
        historyManager.add(task8);
        historyManager.add(task9);
        historyManager.add(task10);

        assertEquals(10, historyManager.getHistory().size());
        assertEquals(task1, historyManager.getHistory().get(0));
        assertEquals(task10, historyManager.getHistory().get(9));

        Task task11 = new Task(11, "name 11", "description 11");

        historyManager.add(task11);

        assertEquals(10, historyManager.getHistory().size());
        assertEquals(task2, historyManager.getHistory().get(0));
        assertEquals(task11, historyManager.getHistory().get(9));
    }

}