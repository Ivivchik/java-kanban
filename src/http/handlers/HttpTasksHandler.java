package http.handlers;

import utils.exceptions.TaskNotFoundException;
import utils.exceptions.TaskHasInteractionException;

import tasks.Task;
import tasks.TaskManager;

import http.Endpoint;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class HttpTasksHandler<M extends TaskManager> extends BaseHttpTaskHandler<M, Task> {

    public HttpTasksHandler(M taskManager) {
        super(taskManager, "tasks", Task.class);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        try {
            switch (endpoint) {
                case GET_TASKS:
                    writeResponse(exchange, gson.toJson(taskManager.getTasks()), 200);
                    break;
                case GET_TASK_BY_ID:
                    handleGetTask(exchange);
                    break;
                case POST_TASKS:
                    handlePostTask(exchange);
                    break;
                case DELETE_TASK:
                    handleDeleteTask(exchange);
                    break;
                default:
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        } catch (TaskNotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        } catch (TaskHasInteractionException e) {
            writeResponse(exchange, e.getMessage(), 406);
        }
    }

    private void handleGetTask(HttpExchange exchange)
            throws IOException, TaskNotFoundException {

        baseHandleGetTask(exchange, taskManager::getTask);
    }

    private void handlePostTask(HttpExchange exchange)
            throws IOException, TaskNotFoundException, TaskHasInteractionException {

        baseHandlePostTask(exchange, taskManager::updateTask, taskManager::createTask);
    }

    private void handleDeleteTask(HttpExchange exchange)
            throws IOException, TaskNotFoundException {

        baseHandleDeleteTask(exchange, taskManager::removeTask);
    }

}
