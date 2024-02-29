import TasksManager.*;

public class Main {

    private static final TaskManager manager = new TaskManager();

    public static void main(String[] args) {

        Task task1 = new Task("task1", "desc for task1");
        Task task2 = new Task("task2", "desc for task2");

        int task1Id = manager.createTask(task1);
        int task2Id = manager.createTask(task2);
        System.out.println("task1Id: " + task1Id + " task2Id: " + task2Id);

        Epic epic1 = new Epic("epic1", "desc for epic1");
        Epic epic2 = new Epic("epic2", "desc for epic2");
        int epic1Id = manager.createEpic(epic1);
        int epic2Id = manager.createEpic(epic2);
        System.out.println("epic1Id: " + epic1Id + " epic2Id: " + epic2Id);

        Subtask subtask1 = new Subtask("subtask1", "desc for subtask1 epic3", 3);
        Subtask subtask2 = new Subtask("subtask2", "desc for subtask2 epic3", 3);
        int subtask1Id = manager.createSubtask(subtask1);
        int subtask2Id = manager.createSubtask(subtask2);

        System.out.println("subtask1Id: " + subtask1Id + " subtask2Id: " + subtask2Id);

        Subtask subtask3 = new Subtask("subtask1", "desc for subtask1 epic4", 4);
        int subtask3Id = manager.createSubtask(subtask3);
        System.out.println("subtask3Id: " + subtask3Id);

        System.out.println();

        System.out.println("Get Task, Epic, Subtask by id");
        System.out.println(manager.getTask(2));
        System.out.println(manager.getEpic(3));
        System.out.println(manager.getSubtask(5));

        System.out.println();

        System.out.println("Get list Tasks, Epics, Subtasks before update");
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());

        System.out.println();

        Task newTask1 = new Task(1, "task1", "new desc for task1", Status.IN_PROGRESS);
        manager.updateTask(newTask1);

        Epic newEpic1 = new Epic(3, "epic1", "new desc for epic1");
        manager.updateEpic(newEpic1);

        Subtask newSubtask1 = new Subtask(5, "subtask 111", "", Status.DONE);
        manager.updateSubtask(newSubtask1);

        System.out.println("Get list Tasks, Epics, Subtasks after update");
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());

        System.out.println();

        System.out.println("Get Task, Epic, Subtask by id after update");
        System.out.println(manager.getTask(1));
        System.out.println(manager.getEpic(3));
        System.out.println(manager.getSubtask(5));

        System.out.println();

        manager.removeSubtask(5);
        System.out.println("Get Epic after remove subtask");
        System.out.println(manager.getEpic(3));
        System.out.println(manager.getSubtask(5));

        System.out.println();

        System.out.println("Get list Tasks, Epics, Subtasks after remove subtask");
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());

        System.out.println();

        manager.removeTask(2);
        manager.removeEpic(4);

        System.out.println("Get list Tasks, Epics, Subtasks after remove task and epic");
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());

        System.out.println();

        System.out.println("Get list Subtask of Epic");
        System.out.println(manager.getTaskFromEpic(3));

        System.out.println();

        manager.removeAllTask();
        manager.removeAllEpic();
        manager.removeAllSubtask();
        System.out.println("Get list Tasks, Epics, Subtasks after remove all");
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());

    }
}
