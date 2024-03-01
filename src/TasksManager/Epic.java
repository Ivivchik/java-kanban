package TasksManager;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public void addSubtaskId(Integer subtaskId) {
        subtasksId.add(subtaskId);
    }

    public ArrayList<Integer> getSubtasks() {
        return new ArrayList<>(subtasksId);
    }

    public void removeSubtask(Integer subtaskId) {
        subtasksId.remove(subtaskId);
    }

    public void removeAllSubtasks(){
        subtasksId.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", id=" + this.getId() +
                ", status=" + this.getStatus() +
                ", subtask=" + subtasksId +
                '}';
    }
}
