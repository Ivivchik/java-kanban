package HistoryManager;

import TasksManager.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> historyList = new ArrayList<>(10);

    @Override
    public void add(Task task) {
        if (historyList.size() < 10) {
            historyList.add(task);
        } else {
            historyList.remove(0);
            historyList.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyList);
    }
}
