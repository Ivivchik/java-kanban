package history;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;

import tasks.Task;
import utils.Manager;

class InMemoryHistoryManagerTest {

    private static HistoryManager historyManager;

    int[] fromListToArray(List<Task> arr) {
        int[] res = new int[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            res[i] = arr.get(i).getId();
        }

        return res;
    }

    @BeforeAll
    static void setUp() {
        historyManager = Manager.getDefaultHistory();
        assertNotNull(historyManager);

        Task task1 = new Task(1, "name 1", "description 1");
        Task task2 = new Task(2, "name 2", "description 2");
        Task task3 = new Task(3, "name 3", "description 3");
        Task task4 = new Task(4, "name 4", "description 4");
        Task task5 = new Task(5, "name 5", "description 5");

        historyManager.add(task2);
        historyManager.add(task4);
        historyManager.add(task1);
        historyManager.add(task5);
        historyManager.add(task3);
    }

    @Test
    void checkSizeHistory() {
        assertEquals(5, historyManager.getHistory().size());
    }

    @Test
    void rewatchHistory() {
        Task task1 = new Task(1, "name 1", "description 1");
        Task task2 = new Task(2, "name 2", "description 2");
        Task task3 = new Task(3, "name 3", "description 3");

        historyManager.add(task3);
        historyManager.add(task2);
        historyManager.add(task1);

        int[] etalonOrder = {4, 5, 3, 2, 1};
        int[] calcOrder = fromListToArray(historyManager.getHistory());

        assertArrayEquals(etalonOrder, calcOrder);
    }

    @Test
    void deleteTaskFromHistory() {
        historyManager.remove(3);
        historyManager.remove(2);
        historyManager.remove(1);

        int[] etalonOrder = {4, 5};
        int[] calcOrder = fromListToArray(historyManager.getHistory());

        assertArrayEquals(etalonOrder, calcOrder);
    }

}