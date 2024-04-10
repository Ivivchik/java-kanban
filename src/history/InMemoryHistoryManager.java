package history;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

class Node {
    Node prev;
    Node next;
    Task data;
}

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

    @Override
    public void remove(int id) {
        for (Task task : historyList) {
            if (task.getId() == id) {
                historyList.remove(task);
                break;
            }
        }
    }
}
