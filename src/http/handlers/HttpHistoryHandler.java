package http.handlers;

import com.google.gson.Gson;
import tasks.Task;
import tasks.TaskManager;

import http.Endpoint;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class HttpHistoryHandler<M extends TaskManager> extends BaseHttpTaskHandler<M, Task> {
    public HttpHistoryHandler(M taskManager, Gson gson) {
        super(taskManager, "history", Task.class, gson);
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
