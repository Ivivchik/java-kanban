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


    public int createTask(Task task) {
        task.setId(cntId);
        tasks.put(cntId, task);

        cntId++;

        return task.getId();
    }

    public int createEpic(Epic epic) {
        epic.setId(cntId);
        epics.put(cntId, epic);

        cntId++;

        return epic.getId();
    }


    public int createSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(cntId);
            subtasks.put(cntId, subtask);

            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubtaskId(cntId);
            calculateEpicStatus(epic);

            cntId++;

            return subtask.getId();
        }

        return -1;
    }

    public Task getTask(int id) {
        return tasks.get(id);

    }

    public Epic getEpic(int id) {
        return epics.get(id);

    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeEpic(int id) {
        if (epics.containsKey(id)) {
            ArrayList<Integer> subtasksId = epics.get(id).getSubtasks();
            for (int subtaskId : subtasksId) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    public void removeSubtask(int id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            Epic epic = epics.get(epicId);
            epic.removeSubtask(id);
            calculateEpicStatus(epic);

            subtasks.remove(id);
        }
    }

    public void removeAllTask() {
        tasks.clear();
    }

    public void removeAllSubtask() {
        for (Epic epic : epics.values()){
            epic.removeAllSubtasks();
            epic.setStatus(Status.NEW);
        }
        subtasks.clear();
    }

    public void removeAllEpic() {
        subtasks.clear();
        epics.clear();
    }

    public ArrayList<Subtask> getTaskFromEpic(int epicId) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);

            for (int subtaskId : epic.getSubtasks()) {
                subtasks.add(this.subtasks.get(subtaskId));
            }
        }

        return subtasks;
    }

    public void updateTask(Task newTask) {
        int taskId = newTask.getId();
        if (tasks.containsKey(taskId)) {
            Task task = tasks.get(taskId);

            String name = newTask.getName();
            String description = newTask.getDescription();
            Status status = newTask.getStatus();

            if (name != null) {
                task.setName(name);
            }
            if (description != null) {
                task.setDescription(description);
            }
            if (status != null) {
                task.setStatus(status);
            }
        }
    }

    public void updateEpic(Epic newEpic) {
        int epicId = newEpic.getId();
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);

            String name = newEpic.getName();
            String description = newEpic.getDescription();

            if (name != null) {
                epic.setName(name);
            }
            if (description != null) {
                epic.setDescription(description);
            }
        }
    }

    public void updateSubtask(Subtask newSubtask) {
        int subtaskId = newSubtask.getId();
        if (subtasks.containsKey(subtaskId)) {
            Subtask subtask = subtasks.get(subtaskId);

            if (subtask.getEpicId() == newSubtask.getEpicId()) {

                String name = newSubtask.getName();
                String description = newSubtask.getDescription();
                Status status = newSubtask.getStatus();

                if (name != null) {
                    subtask.setName(name);
                }
                if (description != null) {
                    subtask.setDescription(description);
                }
                if (status != null) {
                    subtask.setStatus(status);
                }

                Epic epic = epics.get(subtask.getEpicId());
                calculateEpicStatus(epic);
            }
        }
    }

    private void calculateEpicStatus(Epic epic) {

        ArrayList<Integer> subtasksId = epic.getSubtasks();

        if (subtasksId.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean isAllSubtasksNew = true;
        boolean isAllSubtasksDone = true;
        for (int subtaskId : subtasksId) {
            Status status = subtasks.get(subtaskId).getStatus();
            if (status != Status.NEW) {
                isAllSubtasksNew = false;
            }
            if (status != Status.DONE) {
                isAllSubtasksDone = false;
            }
        }

        if (isAllSubtasksDone) {
            epic.setStatus(Status.DONE);
        } else if (isAllSubtasksNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

}

