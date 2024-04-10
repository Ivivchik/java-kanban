import tasks.Epic;
import tasks.Task;
import tasks.Subtask;
import utils.Manager;
import tasks.TaskManager;

public class Main {

    private static TaskManager taskManager = Manager.getDefault();

    public static void main(String[] args) {

        Task task1 = new Task("task 1", "desc for task1");
        Task task2 = new Task("task 2", "desc for task2");

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("epic 1", "desc for task1");

        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("subtask1", "desc for subtask1", epic1.getId());
        Subtask subtask2 = new Subtask("subtask2", "desc for subtask2", epic1.getId());
        Subtask subtask3 = new Subtask("subtask3", "desc for subtask3", epic1.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        Epic epic2 = new Epic("epic 2", "desc for task2");

        taskManager.createEpic(epic2);

        taskManager.getTask(task2.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getTask(task1.getId());
        taskManager.getEpic(epic2.getId());
        taskManager.getSubtask(subtask3.getId());
        taskManager.getSubtask(subtask2.getId());

        System.out.println("History");
        printHistory();

        System.out.println("History after delete task 2");
        taskManager.removeTask(task2.getId());
        printHistory();

        System.out.println("History after delete epic 1");
        taskManager.removeEpic(epic1.getId());
        printHistory();

    }

    public static void printHistory() {
        for (Task t : taskManager.getHistory()) {
            System.out.println(t);
        }
        System.out.println();
    }
}
