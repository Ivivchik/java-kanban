package tasks;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private Instant endTime;

    private ArrayList<Integer> subtasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    protected Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    @Override
    public Instant getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public void addSubtaskId(Integer subtaskId) {
        subtasksId.add(subtaskId);
    }

    public List<Integer> getSubtasks() {
        return new ArrayList<>(subtasksId);
    }

    public void removeSubtask(Integer subtaskId) {
        subtasksId.remove(subtaskId);
    }

    public void removeAllSubtasks() {
        subtasksId.clear();
    }
}
