package TasksManager;

import HistoryManager.HistoryManager;

import java.util.ArrayList;

public interface TaskManager {
    ArrayList<Epic> getEpics();

    ArrayList<Task> getTasks();

    ArrayList<Subtask> getSubtasks();

    int createTask(Task task);

    int createEpic(Epic epic);

    int createSubtask(Subtask subtask);

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    void removeTask(int id);

    void removeEpic(int id);

    void removeSubtask(int id);

    void removeAllTask();

    void removeAllSubtask();

    void removeAllEpic();

    ArrayList<Subtask> getTaskFromEpic(int epicId);

    void updateTask(Task newTask);

    void updateEpic(Epic newEpic);

    void updateSubtask(Subtask newSubtask);

    HistoryManager getHistoryManager();
}
