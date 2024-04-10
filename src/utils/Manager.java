package utils;

import history.HistoryManager;
import history.InMemoryHistoryManager;
import tasks.InMemoryTaskManager;
import tasks.TaskManager;

public class Manager {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
