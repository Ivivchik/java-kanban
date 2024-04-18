package utils;

import tasks.FileBackedTaskManager;
import tasks.TaskManager;
import history.HistoryManager;
import tasks.InMemoryTaskManager;
import history.InMemoryHistoryManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Manager {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getFileTaskManager(String path) {

        File f = new File(path);
        if (!f.exists()) {
            try {
                Files.createFile(f.toPath());
            } catch (IOException e) {
                throw new ManagerSaveException(e.getMessage());
            }
        }
        return new FileBackedTaskManager(f);
    }
}
