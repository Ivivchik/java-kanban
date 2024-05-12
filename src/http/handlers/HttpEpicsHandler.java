package http.handlers;

import com.google.gson.Gson;
import utils.exceptions.ManagerSaveException;
import utils.exceptions.TaskNotFoundException;
import utils.exceptions.TaskHasInteractionException;
import utils.exceptions.EpicIllegalArgumentException;

import tasks.Epic;
import tasks.Subtask;
import tasks.TaskManager;

import http.Endpoint;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class HttpEpicsHandler<M extends TaskManager> extends BaseHttpTaskHandler<M, Epic> {

    public HttpEpicsHandler(M taskManager, Gson gson) {
        super(taskManager, "epics", Epic.class, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        try {
            switch (endpoint) {

                case GET_TASKS:
                    writeResponse(exchange, gson.toJson(taskManager.getEpics()), 200);
                    break;
                case GET_TASK_BY_ID:
                    handleGetEpic(exchange);
                    break;
                case POST_TASKS:
                    handlePostEpic(exchange);
                    break;
                case DELETE_TASK:
                    handleDeleteEpic(exchange);
                    break;
                case GET_EPIC_SUBTASKS:
                    handleGetEpicSubtasks(exchange);
                    break;
                default:
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        } catch (TaskNotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        } catch (TaskHasInteractionException | EpicIllegalArgumentException e) {
            writeResponse(exchange, e.getMessage(), 406);
        } catch (ManagerSaveException e) {
            writeResponse(exchange, e.getMessage(), 500);
        }
    }

    private void handleGetEpic(HttpExchange exchange)
            throws IOException, TaskNotFoundException {

        baseHandleGetTask(exchange, taskManager::getEpic);
    }

    private void handlePostEpic(HttpExchange exchange)
            throws IOException, TaskNotFoundException, TaskHasInteractionException {

        baseHandlePostTask(exchange, taskManager::updateEpic, taskManager::createEpic);
    }

    private void handleDeleteEpic(HttpExchange exchange)
            throws IOException, TaskNotFoundException {

        baseHandleDeleteTask(exchange, taskManager::removeEpic);
    }

    private void handleGetEpicSubtasks(HttpExchange exchange)
            throws IOException, TaskNotFoundException {

        Optional<Integer> idOpt = getTaskId(exchange);
        if (idOpt.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор задачи", 400);
            return;
        }

        int id = idOpt.get();
        List<Subtask> subtasks = taskManager.getTaskFromEpic(id);
        writeResponse(exchange, gson.toJson(subtasks), 200);
    }
}
