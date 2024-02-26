import TasksManager.Status;
import TasksManager.Task;
import TasksManager.TaskManager;

public class Main {

    private static final TaskManager manager = new TaskManager();

    public static void main(String[] args) {

        manager.createTask("task1", "new task1");
        manager.createTask("task2", "new task2");
        manager.createEpic("epic1", "new epic1");
        manager.createSubtask("subtask1", "new subtask1 for epic1", manager.getEpic(3));
        manager.createSubtask("subtask2", "new subtask2 for epic1", manager.getEpic(3));
        manager.createEpic("epic2", "new epic2");
        manager.createSubtask("subtask1", "new subtask1 for epic2", manager.getEpic(6));

        System.out.println("Lists of tasks, epics, subtasks");
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());

        System.out.println();

        System.out.println("Get task, epic, subtask by id before update");
        System.out.println(manager.getTask(1));
        System.out.println(manager.getEpic(3));
        System.out.println(manager.getSubtask(5));

        manager.createEpic("epic3", "new epic3");

        manager.updateTask(1, null, null, Status.IN_PROGRESS, null);
        manager.updateTask(5, null, null, Status.DONE, manager.getEpic(8));
        manager.updateTask(4, null, null, Status.DONE, null);
        manager.createSubtask("subtask new for epic1", "subtask", manager.getEpic(3));

        System.out.println();

        System.out.println("Get task, epic, subtask by id after update");
        System.out.println(manager.getTask(1));
        System.out.println(manager.getEpic(3));
        System.out.println(manager.getSubtask(5));

        System.out.println();

        manager.removeTask(9);

        System.out.println("Lists of tasks, epics, subtasks");
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());


        System.out.println();

        System.out.println("Get list subtask from certain epic");
        System.out.println(manager.getTaskFromEpic(3));

        System.out.println();

        manager.removeAllTask();

        System.out.println("Lists of tasks, epics, subtasks after delete all");
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());



    }
}
