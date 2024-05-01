package tasks;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    protected Epic(int id, String name, String description, Status status, Instant startTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
    }

    @Override
    public Optional<Instant> getEndTime() {
        return Optional.ofNullable(this.endTime);
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
