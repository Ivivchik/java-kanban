package tasks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import utils.ManagerSaveException;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File path;

    public FileBackedTaskManager(String p) {
        path = new File(p);
        if (!path.exists()) {
            try {
                Files.createFile(path.toPath());
            } catch (IOException e) {
                throw new ManagerSaveException(e.getMessage());
            }
        }
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, StandardCharsets.UTF_8))) {

            bw.write("id,name,description,status,epicId\n");

            writeTaskToCsv(bw, getTasks());
            writeTaskToCsv(bw, getEpics());
            writeTaskToCsv(bw, getSubtasks());

            bw.write("\n");

            writeHistoryToCsv(bw, getHistory());


        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
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

    @Override
    public int createTask(Task task) {
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public int createEpic(Epic epic) {
        int id = super.createEpic(epic);
        save();
        return id;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        int id = super.createSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
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
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
    }

    @Override
    public void updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        super.updateSubtask(newSubtask);
        save();
    }

    @Override
    public Task getTask(int id) {
        return super.getTask(id);
    }

    @Override
    public Epic getEpic(int id) {
        return super.getEpic(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        return super.getSubtask(id);
    }
}
