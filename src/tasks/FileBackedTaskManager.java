package tasks;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;

import utils.exceptions.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File path;

    public FileBackedTaskManager(File path) {
        this.path = path;
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, StandardCharsets.UTF_8))) {

            bw.write("id,type,name,description,status,startTime,duration,epicId\n");

            writeTaskToCsv(bw, getTasks());
            writeTaskToCsv(bw, getEpics());
            writeTaskToCsv(bw, getSubtasks());

            bw.write("\n");

            writeHistoryToCsv(bw, getHistory());


        } catch (IOException e) {
            throw new ManagerSaveException("При записи задач произошшла ошибка: " + e.getMessage());
        }
    }

    private void writeTaskToCsv(BufferedWriter bw, List<? extends Task> tasks) throws IOException {
        for (Task t : tasks) {
            bw.write(t.toString() + "\n");
        }
    }

    private void writeHistoryToCsv(BufferedWriter bw, List<Task> history) throws IOException {
        List<String> historyId = new ArrayList<>();
        for (Task t : history) {
            historyId.add(String.valueOf(t.getId()));
        }

        bw.write(String.join(",", historyId));
    }

    private Task taskFromString(String value) {
        String[] strArr = value.split(",");

        String type = strArr[1];
        int id = Integer.parseInt(strArr[0]);
        String name = strArr[2];
        String description = strArr[3];
        Status status = statusFromString(strArr[4]);
        Instant startTime = null;
        Duration duration = null;

        if (strArr.length > 5) {
            String st = strArr[5];
            String d = strArr[6];
            if (!st.isEmpty()) {
                startTime = Instant.parse(st);
            }
            if (!d.isEmpty()) {
                duration = Duration.ofMinutes(Long.parseLong(d));
            }
        }

        switch (type) {
            case "Task":
                return new Task(id, name, description, status, startTime, duration);
            case "Epic":
                return new Epic(id, name, description, status, startTime, duration);
            case "Subtask":
                return new Subtask(id, name, description, status, startTime, duration, Integer.parseInt(strArr[7]));
            default:
                return null;
        }
    }

    private Status statusFromString(String value) {
        switch (value) {
            case "NEW":
                return Status.NEW;
            case "DONE":
                return Status.DONE;
            case "IN_PROGRESS":
                return Status.IN_PROGRESS;
            default:
                return null;
        }
    }

    private static void addTask(Task task, FileBackedTaskManager fm) {
        if (task.getClass() == Epic.class) {
            fm.epics.put(task.getId(), (Epic) task);
        } else if (task.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) task;
            fm.subtasks.put(task.getId(), subtask);

            int subtaskId = subtask.getId();
            int epicId = subtask.getEpicId();
            Epic e = fm.epics.get(epicId);
            if (e != null) {
                e.addSubtaskId(subtaskId);
            }

        } else {
            fm.tasks.put(task.getId(), task);
        }
    }

    private static void addHistoryTask(String value, FileBackedTaskManager fm) {
        if (value != null) {
            String[] indicators = value.split(",");
            for (String indicator : indicators) {
                int id = Integer.parseInt(indicator);

                Task t = fm.tasks.get(id);
                Epic e = fm.epics.get(id);
                Subtask s = fm.subtasks.get(id);

                if (t != null) {
                    fm.historyManager.add(t);
                }
                if (e != null) {
                    fm.historyManager.add(e);
                }
                if (s != null) {
                    fm.historyManager.add(s);
                }
            }
        }
    }

    static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fm = new FileBackedTaskManager(file);

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {

            int lastTaskId = 0;

            br.readLine(); // read header
            String str;
            while ((str = br.readLine()) != null && (!str.equals(""))) {
                Task task = fm.taskFromString(str);
                if (task != null) {
                    addTask(task, fm);

                    int taskId = task.getId();
                    if (taskId > lastTaskId) {
                        lastTaskId = taskId;
                    }
                }
            }

            fm.cntId = lastTaskId + 1;

            String history = br.readLine();
            addHistoryTask(history, fm);

        } catch (IOException e) {
            throw new ManagerSaveException("При чтении задач произошшла ошибка: " + e.getMessage());
        }

        return fm;
    }

    @Override
    public int createTask(Task task) throws TaskHasInteractionException {
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public int createEpic(Epic epic) throws EpicIllegalArgumentException {
        int id = super.createEpic(epic);
        save();
        return id;
    }

    @Override
    public int createSubtask(Subtask subtask) throws TaskNotFoundException, TaskHasInteractionException {
        int id = super.createSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void removeTask(int id) throws TaskNotFoundException {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) throws TaskNotFoundException {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(int id) throws TaskNotFoundException {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeAllTask() {
        super.removeAllTask();
        save();
    }

    @Override
    public void removeAllSubtask() {
        super.removeAllSubtask();
        save();
    }

    @Override
    public void removeAllEpic() {
        super.removeAllEpic();
        save();
    }

    @Override
    public void updateTask(Task newTask) throws TaskNotFoundException, TaskHasInteractionException {
        super.updateTask(newTask);
        save();
    }

    @Override
    public void updateEpic(Epic newEpic) throws TaskNotFoundException {
        super.updateEpic(newEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask newSubtask)
            throws TaskNotFoundException, TaskHasInteractionException, EpicMatchException {

        super.updateSubtask(newSubtask);
        save();
    }

    @Override
    public Task getTask(int id) throws TaskNotFoundException {
        Task t = super.getTask(id);
        save();
        return t;
    }

    @Override
    public Epic getEpic(int id) throws TaskNotFoundException {
        Epic e = super.getEpic(id);
        save();
        return e;
    }

    @Override
    public Subtask getSubtask(int id) throws TaskNotFoundException {
        Subtask s = super.getSubtask(id);
        save();
        return s;
    }
}
