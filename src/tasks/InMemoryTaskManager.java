package tasks;

import utils.Manager;
import history.HistoryManager;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        Optional<Task> invalidTask = getPrioritizedTasks().stream().filter(t -> isIntersect(t, task)).findFirst();
        if (invalidTask.isPresent()) {
            return -1;
        }
        task.setId(cntId);
        tasks.put(cntId, task);

        cntId++;

        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        Optional<Task> invalidEpic = getPrioritizedTasks().stream().filter(t -> isIntersect(t, epic)).findFirst();
        if (invalidEpic.isPresent()) {
            return -1;
        }
        epic.setId(cntId);
        epics.put(cntId, epic);

        cntId++;

        return epic.getId();
    }

    @Override
    public int createSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            Optional<Task> invalidSubtask = getPrioritizedTasks().stream().filter(t -> isIntersect(t, subtask)).findFirst();
            if (invalidSubtask.isPresent()) {
                return -1;
            }

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
            epics.get(id).getSubtasks().forEach(i -> {
                subtasks.remove(i);
                historyManager.remove(i);
            });
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
        tasks.values().forEach(t -> historyManager.remove(t.getId()));
        tasks.clear();
    }

    @Override
    public void removeAllSubtask() {
        epics.values().forEach(epic -> {
            epic.getSubtasks().forEach(historyManager::remove);
            epic.removeAllSubtasks();
            epic.setStatus(Status.NEW);
            epic.setEndTime(null);
            epic.setStartTime(null);
            epic.setDuration(null);
        });
        subtasks.clear();
    }

    @Override
    public void removeAllEpic() {
        epics.values().forEach(e -> {
            e.getSubtasks().forEach(historyManager::remove);
            historyManager.remove(e.getId());
        });
        subtasks.clear();
        epics.clear();
    }

    @Override
    public List<Subtask> getTaskFromEpic(int epicId) {
        return epics.containsKey(epicId)
                ? epics.get(epicId).getSubtasks().stream()
                .map(subtasks::get)
                .collect(Collectors.toList())
                : new ArrayList<>();
    }

    @Override
    public void updateTask(Task newTask) {
        int taskId = newTask.getId();
        if (tasks.containsKey(taskId)) {
            Task task = tasks.get(taskId);

            String name = newTask.getName();
            String description = newTask.getDescription();
            Status status = newTask.getStatus();
            Optional<Duration> duration = newTask.getDuration();
            Optional<Instant> startTime = newTask.getStartTime();
            Optional<Instant> endTime = newTask.getEndTime();

            if (name != null) {
                task.setName(name);
            }
            if (description != null) {
                task.setDescription(description);
            }
            if (status != null) {
                task.setStatus(status);
            }
            duration.filter(x -> !x.isNegative()).ifPresent(task::setDuration);
            startTime.filter(x -> endTime.map(x::isBefore).orElse(true)).ifPresent(task::setStartTime);
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
                Optional<Duration> duration = newSubtask.getDuration();
                Optional<Instant> startTime = newSubtask.getStartTime();
                Optional<Instant> endTime = newSubtask.getEndTime();

                if (name != null) {
                    subtask.setName(name);
                }
                if (description != null) {
                    subtask.setDescription(description);
                }
                if (status != null) {
                    subtask.setStatus(status);
                }
                duration.filter(x -> !x.isNegative()).ifPresent(subtask::setDuration);
                startTime.filter(x -> endTime.map(x::isBefore).orElse(true)).ifPresent(subtask::setStartTime);

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
            Duration subtaskDuration = subtask.getDuration().orElse(duration);
            Instant subtaskStartTime = subtask.getStartTime().orElse(startTime);
            Instant subtaskEndTime = subtask.getEndTime().orElse(endTime);

            if (subtaskStartTime.isBefore(startTime)) {
                startTime = subtaskStartTime;
            }
            if (subtaskEndTime.isAfter(endTime)) {
                endTime = subtaskEndTime;
            }
            duration = duration.plus(subtaskDuration);
        }

        if (duration.equals(Duration.ZERO)) {
            epic.setDuration(null);
        } else {
            epic.setDuration(duration);
        }

        if (startTime.equals(Instant.MAX)) {
            epic.setStartTime(null);
        } else {
            epic.setStartTime(startTime);
        }

        if (endTime.equals(Instant.MIN)) {
            epic.setEndTime(null);
        } else {
            epic.setEndTime(endTime);
        }
    }

    private boolean isIntersect(Task task, Task otherTask) {

        Optional<Instant> taskST = task.getStartTime();
        Optional<Instant> taskET = task.getEndTime();

        Optional<Instant> otherST = otherTask.getStartTime();
        Optional<Instant> otherET = otherTask.getEndTime();

        Optional<Boolean> startBeforeEnd = otherST.flatMap(
                ost -> taskST.map(ost::isAfter).flatMap(res1 -> taskET.map(ost::isBefore).map(res2 -> res1 && res2)));
        Optional<Boolean> endAfterStart = otherET.flatMap(
                oet -> taskST.map(oet::isAfter).flatMap(res1 -> taskET.map(oet::isBefore).map(res2 -> res1 && res2)));

        Optional<Boolean> result = startBeforeEnd.flatMap(sbe -> endAfterStart.map(eaf -> sbe || eaf));

        return result.orElse(false);
    }

    @Override
    public SortedSet<Task> getPrioritizedTasks() {
        TreeSet<Task> sortedTasks = new TreeSet<>(Comparator.comparing(x -> x.getStartTime().get()));
        Predicate<Task> p = t -> t.getStartTime().isPresent();

        sortedTasks.addAll(getTasks().stream().filter(p).collect(Collectors.toList()));
        sortedTasks.addAll(getEpics().stream().filter(p).collect(Collectors.toList()));
        sortedTasks.addAll(getSubtasks().stream().filter(p).collect(Collectors.toList()));

        return sortedTasks;
    }
}
