package http.handlers;

import com.google.gson.Gson;
import utils.exceptions.EpicMatchException;
import utils.exceptions.ManagerSaveException;
import utils.exceptions.TaskNotFoundException;
import utils.exceptions.TaskHasInteractionException;

import tasks.Subtask;
import tasks.TaskManager;

import http.Endpoint;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class HttpSubtasksHandler<M extends TaskManager> extends BaseHttpTaskHandler<M, Subtask> {
    public HttpSubtasksHandler(M taskManager, Gson gson) {
        super(taskManager, "subtasks", Subtask.class, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        try {
            switch (endpoint) {
                case GET_TASKS:
                    writeResponse(exchange, gson.toJson(taskManager.getSubtasks()), 200);
                    break;
                case GET_TASK_BY_ID:
                    handleGetSubtask(exchange);
                    break;
                case POST_TASKS:
                    handlePostSubtask(exchange);
                    break;
                case DELETE_TASK:
                    handleDeleteSubtask(exchange);
                    break;
                default:
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        } catch (TaskNotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        } catch (TaskHasInteractionException | EpicMatchException e) {
            writeResponse(exchange, e.getMessage(), 406);
        } catch (ManagerSaveException e) {
            writeResponse(exchange, e.getMessage(), 500);
        }
    }

    private void handleGetSubtask(HttpExchange exchange) throws IOException, TaskNotFoundException {

        baseHandleGetTask(exchange, taskManager::getSubtask);
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException, TaskNotFoundException, TaskHasInteractionException, EpicMatchException {

        baseHandlePostTask(exchange, taskManager::updateSubtask, taskManager::createSubtask);
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException, TaskNotFoundException {
        baseHandleDeleteTask(exchange, taskManager::removeSubtask);
    }
}
