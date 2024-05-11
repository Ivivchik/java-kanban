package utils.exceptions;

public class TaskHasInteractionException extends RuntimeException {
    public TaskHasInteractionException() {
        super("Задача пересекается с существующими");
    }
}
