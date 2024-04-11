package history;

import tasks.Task;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> historyMap = new HashMap<>();
    private Node first;
    private Node last;

    private class Node {
        Node prev;
        Node next;
        Task data;

        Node(Node prev, Task data, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    private Node linkLast(Node newNode) {
        Node l = last;
        last = newNode;
        if (l == null) {
            first = newNode;
        } else {
            newNode.prev = l;
            l.next = newNode;

        }
        return newNode;
    }

    private List<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node f = first;
        while (f != null) {
            tasks.add(f.data);
            f = f.next;
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        Node p = node.prev;
        Node n = node.next;

        node.prev = null;
        node.next = null;

        if (p != null) {
            p.next = n;
        } else {
            first = n;
        }
        if (n != null) {
            n.prev = p;
        } else {
            last = p;
        }
    }


    @Override
    public void add(Task task) {
        int taskId = task.getId();
        if (historyMap.containsKey(taskId)) {
            Node n = historyMap.get(taskId);
            removeNode(n);
            linkLast(n);
        } else {
            Node newNode = new Node(null, task, null);
            historyMap.put(taskId, linkLast(newNode));
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node n = historyMap.get(id);
        removeNode(n);
        historyMap.remove(id);
    }
}
