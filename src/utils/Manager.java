package utils;

import tasks.FileBackedTaskManager;
import tasks.TaskManager;
import history.HistoryManager;
import tasks.InMemoryTaskManager;
import history.InMemoryHistoryManager;

import java.io.File;

public class Manager {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getFileTaskManager(File f) {
        return new FileBackedTaskManager(f);
    }
}
