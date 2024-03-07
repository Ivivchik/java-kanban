package utils;

import HistoryManager.HistoryManager;
import HistoryManager.InMemoryHistoryManager;
import TasksManager.InMemoryTaskManager;
import TasksManager.TaskManager;

public class Manager {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
