package tasks;

import java.util.List;
import java.util.SortedSet;

public interface TaskManager {
    List<Epic> getEpics();

    List<Task> getTasks();

    List<Subtask> getSubtasks();

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

    List<Subtask> getTaskFromEpic(int epicId);

    void updateTask(Task newTask);

    void updateEpic(Epic newEpic);

    void updateSubtask(Subtask newSubtask);

    List<Task> getHistory();

    SortedSet<Task> getPrioritizedTasks();
}
