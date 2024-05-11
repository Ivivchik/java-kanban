package http.handlers;

import utils.adapters.InstantAdapter;
import utils.adapters.DurationAdapter;
import utils.exceptions.TaskNotFoundException;
import utils.exceptions.TaskHasInteractionException;

import tasks.Task;
import tasks.TaskManager;

import http.Endpoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

abstract class BaseHttpTaskHandler<M extends TaskManager, T extends Task> implements HttpHandler {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final String typeTask;
    private final Class<T> typeClass;
    protected Gson gson;
    protected M taskManager;

    protected BaseHttpTaskHandler(M taskManager, String typeTask, Class<T> typeClass) {
        this.taskManager = taskManager;
        this.typeTask = typeTask;
        this.typeClass = typeClass;

        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
    }

    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals(typeTask)) {
            if (requestMethod.equals("GET")) return Endpoint.GET_TASKS;
            if (requestMethod.equals("POST")) return Endpoint.POST_TASKS;

        }
        if (pathParts.length == 3 && pathParts[1].equals(typeTask)) {
            if (requestMethod.equals("GET")) return Endpoint.GET_TASK_BY_ID;
            if (requestMethod.equals("DELETE")) return Endpoint.DELETE_TASK;
        }
        if (pathParts.length == 4 && pathParts[1].equals(typeTask) &&
                pathParts[3].equals("subtasks") &&
                requestMethod.equals("GET")) {
            return Endpoint.GET_EPIC_SUBTASKS;
        }
        return Endpoint.UNKNOWN;
    }

    protected void baseHandleGetTask(HttpExchange exchange, IntFunction<T> getFunction)
            throws IOException, TaskNotFoundException {

        Optional<Integer> idOpt = getTaskId(exchange);
        if (idOpt.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор задачи", 400);
            return;
        }

        int id = idOpt.get();
        String jsonTask = gson.toJson(getFunction.apply(id));
        writeResponse(exchange, jsonTask, 200);
    }

    protected void baseHandlePostTask(HttpExchange exchange,
                                      Consumer<T> updateFunction,
                                      ToIntFunction<T> createFunction)
            throws IOException, TaskNotFoundException, TaskHasInteractionException {

        T task = parseTask(exchange.getRequestBody());
        int taskId = task.getId();
        if (taskId == 0) {
            int id = createFunction.applyAsInt(task);
            writeResponse(exchange, "Задача с идентификатор id=" + id + " создана", 201);
        } else {
            updateFunction.accept(task);
            writeResponse(exchange, "Задача с идентификатор id=" + taskId + " обновлена", 201);
        }
    }

    protected void baseHandleDeleteTask(HttpExchange exchange, IntConsumer removeFunction)
            throws IOException, TaskNotFoundException {

        Optional<Integer> idOpt = getTaskId(exchange);
        if (idOpt.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор задачи", 400);
            return;
        }

        int id = idOpt.get();
        removeFunction.accept(id);
        writeResponse(exchange, "Задача с идентификатор id=" + id + " удалена", 201);
    }


    protected void writeResponse(HttpExchange exchange,
                                 String responseString,
                                 int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }

    protected Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private T parseTask(InputStream bodyInputStream) throws IOException {
        String body = new String(bodyInputStream.readAllBytes(), DEFAULT_CHARSET);
        return gson.fromJson(body, typeClass);
    }
}
