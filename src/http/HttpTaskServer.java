package http;

import tasks.*;

import http.handlers.*;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer<T extends TaskManager> {
    private static final int PORT = 8080;
    private HttpServer httpServer;
    private final T taskManager;

    public HttpTaskServer(T taskManager) {
        this.taskManager = taskManager;
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/tasks", new HttpTasksHandler<>(taskManager));
        httpServer.createContext("/subtasks", new HttpSubtasksHandler<>(taskManager));
        httpServer.createContext("/epics", new HttpEpicsHandler<>(taskManager));
        httpServer.createContext("/history", new HttpHistoryHandler<>(taskManager));
        httpServer.createContext("/prioritized", new HttpPrioritizedTasksHandler<>(taskManager));

        httpServer.start();
    }

    public void stop() {
        httpServer.stop(1);
    }

    public static void main(String[] args) {

    }
}
