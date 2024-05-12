package utils.exceptions;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String typeTask, final int id) {
        super(String.format("%s с идентификатором id=%d не найдено", typeTask, id));
    }
}
