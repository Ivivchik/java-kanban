package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tasks.*;

import http.handlers.*;

import com.sun.net.httpserver.HttpServer;
import utils.adapters.DurationAdapter;
import utils.adapters.InstantAdapter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;


public class HttpTaskServer<T extends TaskManager> {
    private static final int PORT = 8080;
    private HttpServer httpServer;
    private final T taskManager;
    private Gson gson;

    public HttpTaskServer(T taskManager) {
        this.taskManager = taskManager;

        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
    }

    public Gson getGson() {
        return this.gson;
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/tasks", new HttpTasksHandler<>(taskManager, gson));
        httpServer.createContext("/subtasks", new HttpSubtasksHandler<>(taskManager, gson));
        httpServer.createContext("/epics", new HttpEpicsHandler<>(taskManager, gson));
        httpServer.createContext("/history", new HttpHistoryHandler<>(taskManager, gson));
        httpServer.createContext("/prioritized", new HttpPrioritizedTasksHandler<>(taskManager, gson));

        httpServer.start();
    }


    public void stop() {
        httpServer.stop(1);
    }

    public static void main(String[] args) {

    }
}
