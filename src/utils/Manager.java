package utils;

import tasks.TaskManager;
import history.HistoryManager;
import tasks.InMemoryTaskManager;
import history.InMemoryHistoryManager;

public class Manager {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
