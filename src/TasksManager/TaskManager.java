package TasksManager;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int cntId = 1;

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }


    public void createTask(String taskName, String taskDescription) {
        tasks.put(cntId, new Task(cntId, taskName, taskDescription));
        cntId++;
    }

    public void createEpic(String taskName, String taskDescription) {
        epics.put(cntId, new Epic(cntId, taskName, taskDescription));
        cntId++;
    }


    public void createSubtask(String taskName, String taskDescription, Epic epic) {
        Subtask subtask = new Subtask(cntId, taskName, taskDescription, epic);

        subtasks.put(cntId, subtask);

        epic.addSubtask(subtask);
        ArrayList<Subtask> epicSubtask = epic.getSubtasks();
        Status epicStatus = calculateEpicStatus(epicSubtask);
        updateEpic(epic.getId(), null, null, epicStatus, null);

        cntId++;
    }

    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }
        return null;
    }

    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        }
        return null;
    }

    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        }
        return null;
    }


    public void removeTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        }
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            Epic subtaskEpic = subtask.getEpic();
            subtaskEpic.removeSubtask(subtask);
            ArrayList<Subtask> epicSubtask = subtaskEpic.getSubtasks();
            Status epicStatus = calculateEpicStatus(epicSubtask);
            updateEpic(subtaskEpic.getId(), null, null, epicStatus, null);
            subtasks.remove(id);
        }
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            epic.clearListSubtask();
            epics.remove(id);
        }
    }

    public void removeAllTask() {
        tasks.clear();
        subtasks.clear();
        epics.forEach((key, value) -> value.clearListSubtask());
        epics.clear();
    }

    public ArrayList<Subtask> getTaskFromEpic(int epicId) {
        Epic epic = getEpic(epicId);
        return epic.getSubtasks();
    }

    public void updateTask(int id, String newName, String newDescription, Status newStatus, Epic newEpic) {
        if (tasks.containsKey(id)) {
            updateTask(id, newName, newDescription, newStatus);
        }
        if (subtasks.containsKey(id)) {
            updateSubtask(id, newName, newDescription, newStatus, newEpic);
        }
        if (epics.containsKey(id)) {
            updateEpic(id, newName, newDescription, null, null);
        }
    }

    private void updateTask(int id, String newName, String newDescription, Status newStatus) {

        Task task = tasks.get(id);
        if (newName == null) {
            newName = task.getName();
        }
        if (newDescription == null) {
            newDescription = task.getDescription();
        }
        if (newStatus == null) {
            newStatus = task.getStatus();
        }
        Task newTask = new Task(id, newName, newDescription, newStatus);
        tasks.put(id, newTask);

    }

    private void updateSubtask(int id, String newName, String newDescription, Status newStatus, Epic newEpic) {

        Subtask subtask = subtasks.get(id);
        Subtask newSubtask;
        ArrayList<Subtask> epicSubtask;


        subtask.getEpic().removeSubtask(subtask);

        if (newName == null) {
            newName = subtask.getName();
        }
        if (newDescription == null) {
            newDescription = subtask.getDescription();
        }
        if (newStatus == null) {
            newStatus = subtask.getStatus();
        }

        if (newEpic == null) {
            newEpic = subtask.getEpic();
            epicSubtask = newEpic.getSubtasks();
            epicSubtask.remove(subtask);
        } else {
            epicSubtask = newEpic.getSubtasks();
        }

        int newEpicId = newEpic.getId();
        newSubtask = new Subtask(id, newName, newDescription, newStatus, newEpic);
        epicSubtask.add(newSubtask);
        Status newEpicStatus = calculateEpicStatus(epicSubtask);
        updateEpic(newEpicId, null, null, newEpicStatus, epicSubtask);
        newEpic = epics.get(newEpicId);
        newSubtask = new Subtask(id, newName, newDescription, newStatus, newEpic);


        subtasks.put(id, newSubtask);

    }

    private void updateEpic(int id, String newName, String newDescription, Status newStatus, ArrayList<Subtask> newSubtasks) {
        Epic epic = epics.get(id);

        if (newName == null) {
            newName = epic.getName();
        }
        if (newDescription == null) {
            newDescription = epic.getDescription();
        }
        if (newStatus == null) {
            newStatus = epic.getStatus();
        }
        if (newSubtasks == null) {
            newSubtasks = epic.getSubtasks();
        }
        Epic newEpic = new Epic(id, newName, newDescription, newStatus, newSubtasks);
        epics.put(id, newEpic);
    }

    private Status calculateEpicStatus(ArrayList<Subtask> subtasks) {

        boolean isAllSubtasksNew = true;
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != Status.NEW) {
                isAllSubtasksNew = false;
                break;
            }
        }

        if (isAllSubtasksNew) {
            return Status.NEW;
        }

        boolean isAllSubtasksDone = true;
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != Status.DONE) {
                isAllSubtasksDone = false;
                break;
            }
        }

        if (isAllSubtasksDone) {
            return Status.DONE;
        } else {
            return Status.IN_PROGRESS;
        }

    }

}

