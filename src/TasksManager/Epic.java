package TasksManager;

import java.util.ArrayList;

class Epic extends Task {

    private ArrayList<Subtask> subtasks = new ArrayList<>();

    protected Epic(int id, String name, String description) {
        super(id, name, description);
    }

    protected Epic(int id, String name, String description, Status status, ArrayList<Subtask> subtasks) {
        super(id, name, description, status);
        this.subtasks = subtasks;
    }

    protected void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    protected ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    protected void clearListSubtask() {
        subtasks.clear();
    }

    protected void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", id=" + this.getId() +
                ", status=" + this.getStatus() +
                ", subtask=" + subtasks +
                '}';
    }
}
