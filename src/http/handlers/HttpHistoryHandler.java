package http.handlers;

import tasks.Task;
import tasks.TaskManager;

import http.Endpoint;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class HttpHistoryHandler<M extends TaskManager> extends BaseHttpTaskHandler<M, Task> {
    public HttpHistoryHandler(M taskManager) {
        super(taskManager, "history", Task.class);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS:
                writeResponse(exchange, gson.toJson(taskManager.getHistory()), 200);
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }
}
