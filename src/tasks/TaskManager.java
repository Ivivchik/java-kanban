package tasks;

import utils.exceptions.EpicMatchException;
import utils.exceptions.TaskNotFoundException;
import utils.exceptions.TaskHasInteractionException;
import utils.exceptions.EpicIllegalArgumentException;

import java.util.List;
import java.util.SortedSet;

public interface TaskManager {
    List<Epic> getEpics();

    List<Task> getTasks();

    List<Subtask> getSubtasks();

    int createTask(Task task) throws TaskHasInteractionException;

    int createEpic(Epic epic) throws EpicIllegalArgumentException;

    int createSubtask(Subtask subtask) throws TaskNotFoundException, TaskHasInteractionException;

    Task getTask(int id) throws TaskNotFoundException;

    Epic getEpic(int id) throws TaskNotFoundException;

    Subtask getSubtask(int id) throws TaskNotFoundException;

    void removeTask(int id) throws TaskNotFoundException;

    void removeEpic(int id) throws TaskNotFoundException;

    void removeSubtask(int id) throws TaskNotFoundException;

    void removeAllTask();

    void removeAllSubtask();

    void removeAllEpic();

    List<Subtask> getTaskFromEpic(int epicId) throws TaskNotFoundException;

    void updateTask(Task newTask) throws TaskNotFoundException, TaskHasInteractionException;

    void updateEpic(Epic newEpic) throws TaskNotFoundException;

    void updateSubtask(Subtask newSubtask) throws TaskNotFoundException, TaskHasInteractionException, EpicMatchException;

    List<Task> getHistory();

    SortedSet<Task> getPrioritizedTasks();
}
