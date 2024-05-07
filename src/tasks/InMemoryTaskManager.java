package tasks;

import utils.Manager;
import history.HistoryManager;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager = Manager.getDefaultHistory();
    protected final SortedSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(t -> t.getStartTime().orElseThrow()));
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
    public SortedSet<Task> getPrioritizedTasks() {
        return new TreeSet<>(prioritizedTasks);
    }

    @Override
    public int createTask(Task task) {
        if (checkTaskTime(task)) {
            task.setId(cntId);

            tasks.put(cntId, task);
            task.getStartTime().ifPresent(i -> prioritizedTasks.add(task));

            cntId++;

            return task.getId();
        } else {
            return -1;
        }
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
        if (epics.containsKey(subtask.getEpicId()) && (checkTaskTime(subtask))) {
            subtask.setId(cntId);

            subtasks.put(cntId, subtask);
            subtask.getStartTime().ifPresent(i -> prioritizedTasks.add(subtask));

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
        Task removedTask = tasks.remove(id);
        removedTask.getStartTime().ifPresent(i -> prioritizedTasks.remove(removedTask));
        historyManager.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        if (epics.containsKey(id)) {
            epics.get(id).getSubtasks().forEach(i -> {
                Subtask removedSubtask = subtasks.remove(i);
                removedSubtask.getStartTime().ifPresent(inst -> prioritizedTasks.remove(removedSubtask));
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

            Subtask removedSubtask = subtasks.remove(id);
            removedSubtask.getStartTime().ifPresent(i -> prioritizedTasks.remove(removedSubtask));
            historyManager.remove(id);
        }
    }

    @Override
    public void removeAllTask() {
        tasks.values().forEach(t -> {
            t.getStartTime().ifPresent(i -> prioritizedTasks.remove(t));
            historyManager.remove(t.getId());
        });
        tasks.clear();
    }

    @Override
    public void removeAllSubtask() {
        epics.values().forEach(epic -> {
            epic.getSubtasks().forEach(i -> {
                Subtask removedSubtask = subtasks.remove(i);
                removedSubtask.getStartTime().ifPresent(inst -> prioritizedTasks.remove(removedSubtask));
                historyManager.remove(i);
            });
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
            e.getSubtasks().forEach(i -> {
                Subtask removedSubtask = subtasks.remove(i);
                removedSubtask.getStartTime().ifPresent(inst -> prioritizedTasks.remove(removedSubtask));
                historyManager.remove(i);
            });
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
        if (tasks.containsKey(taskId) && checkTaskTime(newTask)) {
            Task task = tasks.get(taskId);

            String name = newTask.getName();
            String description = newTask.getDescription();
            Status status = newTask.getStatus();
            Optional<Duration> duration = newTask.getDuration();
            Optional<Instant> startTime = newTask.getStartTime();

            if (name != null) {
                task.setName(name);
            }
            if (description != null) {
                task.setDescription(description);
            }
            if (status != null) {
                task.setStatus(status);
            }
            duration.ifPresent(task::setDuration);
            startTime.ifPresent(i -> {
                task.setStartTime(i);
                prioritizedTasks.add(task);
            });
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
        if (subtasks.containsKey(subtaskId) && checkTaskTime(newSubtask)) {
            Subtask subtask = subtasks.get(subtaskId);

            if (subtask.getEpicId() == newSubtask.getEpicId()) {

                String name = newSubtask.getName();
                String description = newSubtask.getDescription();
                Status status = newSubtask.getStatus();
                Optional<Duration> duration = newSubtask.getDuration();
                Optional<Instant> startTime = newSubtask.getStartTime();

                if (name != null) {
                    subtask.setName(name);
                }
                if (description != null) {
                    subtask.setDescription(description);
                }
                if (status != null) {
                    subtask.setStatus(status);
                }
                duration.ifPresent(subtask::setDuration);
                startTime.ifPresent(i -> {
                    subtask.setStartTime(i);
                    prioritizedTasks.add(subtask);
                });

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

        epic.setDuration(duration.equals(Duration.ZERO) ? null : duration);
        epic.setStartTime(startTime.equals(Instant.MAX) ? null : startTime);
        epic.setEndTime(endTime.equals(Instant.MIN) ? null : endTime);
    }

    public boolean isIntersect(Task task, Task otherTask) {

        Optional<Instant> taskST = task.getStartTime();
        Optional<Instant> taskET = task.getEndTime();

        Optional<Instant> otherST = otherTask.getStartTime();
        Optional<Instant> otherET = otherTask.getEndTime();

        if (taskST.isPresent() && taskET.isPresent() && otherST.isPresent() && otherET.isPresent()) {
            boolean startBeforeEnd = (otherST.get().isAfter(taskST.get()) || otherST.get().equals(taskST.get())) &&
                    otherST.get().isBefore(taskET.get());
            boolean endAfterStart = otherET.get().isAfter(taskST.get()) &&
                    (otherET.get().isBefore(taskET.get()) || otherET.get().equals(taskET.get()));

            return startBeforeEnd || endAfterStart;
        } else {
            return false;
        }
    }

    private boolean checkTaskTime(Task task) {
        Optional<Duration> durationOpt = task.getDuration().filter(Duration::isNegative);
        Optional<Task> invalidTaskOpt = prioritizedTasks.stream()
                .filter(t -> t.getId() != task.getId())
                .filter(t -> isIntersect(t, task))
                .findFirst();

        return durationOpt.isEmpty() && invalidTaskOpt.isEmpty();
    }
}
