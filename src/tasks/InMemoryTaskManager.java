package tasks;

import utils.Manager;
import history.HistoryManager;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager = Manager.getDefaultHistory();
    protected int cntId = 1;

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public int createTask(Task task) {
        task.setId(cntId);
        tasks.put(cntId, task);

        cntId++;

        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        epic.setId(cntId);
        epics.put(cntId, epic);

        cntId++;

        return epic.getId();
    }

    @Override
    public int createSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(cntId);
            subtasks.put(cntId, subtask);


            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubtaskId(cntId);
            calculateEpicStatus(epic);
            calculateEpicDuration(epic);

            cntId++;

            return subtask.getId();
        }

        return -1;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);

        return task;

    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);

        return epic;

    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);

        return subtask;
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);

    }

    @Override
    public void removeEpic(int id) {
        if (epics.containsKey(id)) {
            List<Integer> subtasksId = epics.get(id).getSubtasks();
            for (int subtaskId : subtasksId) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeSubtask(int id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            Epic epic = epics.get(epicId);
            epic.removeSubtask(id);
            calculateEpicStatus(epic);
            calculateEpicDuration(epic);

            subtasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeAllTask() {
        for (Task t : tasks.values()) {
            historyManager.remove(t.getId());
        }
        tasks.clear();
    }

    @Override
    public void removeAllSubtask() {
        for (Epic epic : epics.values()) {
            for (Integer subtaskId : epic.getSubtasks()) {
                historyManager.remove(subtaskId);
            }
            epic.removeAllSubtasks();
            epic.setStatus(Status.NEW);
        }
        subtasks.clear();
    }

    @Override
    public void removeAllEpic() {
        for (Epic epic : epics.values()) {
            for (Integer subtaskId : epic.getSubtasks()) {
                historyManager.remove(subtaskId);
            }
            historyManager.remove(epic.getId());
        }
        subtasks.clear();
        epics.clear();
    }

    @Override
    public List<Subtask> getTaskFromEpic(int epicId) {
        ArrayList<Subtask> certainEpicsSubtasks = new ArrayList<>();
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);

            for (int subtaskId : epic.getSubtasks()) {
                certainEpicsSubtasks.add(this.subtasks.get(subtaskId));
            }
        }
        return certainEpicsSubtasks;
    }

    @Override
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

    @Override
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

    @Override
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
                calculateEpicDuration(epic);
            }
        }
    }

    private void calculateEpicStatus(Epic epic) {

        List<Integer> subtasksId = epic.getSubtasks();

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

    private void calculateEpicDuration(Epic epic) {
        List<Integer> subtasksId = epic.getSubtasks();
        Instant startTime = Instant.MAX;
        Instant endTime = Instant.MIN;
        Duration duration = Duration.ZERO;

        for (int subtaskId : subtasksId) {
            Subtask subtask = subtasks.get(subtaskId);
            Duration subtaskDuration = subtask.getDuration();
            Instant subtaskStartTime = subtask.getStartTime();
            Instant subtaskEndTime = subtask.getEndTime();

            if (subtaskStartTime.isBefore(startTime)) {
                startTime = subtaskStartTime;
            }
            if (subtaskEndTime.isAfter(endTime)) {
                endTime = subtaskEndTime;
            }
            duration = duration.plus(subtaskDuration);
        }

        epic.setDuration(duration);
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);

    }
}
