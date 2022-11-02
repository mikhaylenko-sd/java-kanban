package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Gson gson = new Gson();
    private static TaskManager httpTaskManager;
    private static HttpServer httpServer;

    public static void start() throws IOException {
        httpTaskManager = Managers.getDefault("backupKey1");
        httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler());
        httpServer.start(); // запускаем сервер

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public static void stop() {
        System.out.println("Сервер остановлен.");
        httpServer.stop(1);
    }

    static class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String requestMethod = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            String query = httpExchange.getRequestURI().getQuery();
            if (query != null) {
                path = path + "?" + query;
            }
            String[] splitStrings = path.split("/");
            switch (requestMethod) {
                case "GET":
                    handleGetRequest(httpExchange, splitStrings);
                    break;
                case "DELETE":
                    handleDeleteRequest(httpExchange, splitStrings);
                    break;
                case "POST":
                    handlePostRequest(httpExchange, splitStrings);
                    break;
                default:
                    httpExchange.sendResponseHeaders(404, 0);
            }
            httpExchange.close();
        }
    }

    private static void handleGetRequest(HttpExchange httpExchange, String[] splitStrings) throws IOException {
        if (splitStrings.length == 2) {
            String gsonPrioritizedTasks = gson.toJson(httpTaskManager.getPrioritizedTasks());
            writeResponse(httpExchange, gsonPrioritizedTasks);
        } else if (splitStrings.length == 3) {
            switch (splitStrings[2]) {
                case "task":
                    String gsonAllTasks = gson.toJson(httpTaskManager.getAllTasks());
                    writeResponse(httpExchange, gsonAllTasks);
                    break;
                case "subtask":
                    String gsonAllSubTasks = gson.toJson(httpTaskManager.getAllSubTasks());
                    writeResponse(httpExchange, gsonAllSubTasks);
                    break;
                case "epic":
                    String gsonAllEpics = gson.toJson(httpTaskManager.getAllEpics());
                    writeResponse(httpExchange, gsonAllEpics);
                    break;
                case "history":
                    String gsonHistory = gson.toJson(httpTaskManager.getHistory());
                    writeResponse(httpExchange, gsonHistory);
                    break;
            }
        } else if (splitStrings.length == 4 && splitStrings[3].startsWith("?id=")) {
            int id = Integer.parseInt(splitStrings[3].substring(4));
            switch (splitStrings[2]) {
                case "task":
                    String gsonTaskById = gson.toJson(httpTaskManager.getTaskById(id));
                    writeResponse(httpExchange, gsonTaskById);
                    break;
                case "subtask":
                    String gsonSubTaskById = gson.toJson(httpTaskManager.getSubTaskById(id));
                    writeResponse(httpExchange, gsonSubTaskById);
                    break;
                case "epic":
                    String gsonEpicById = gson.toJson(httpTaskManager.getEpicById(id));
                    writeResponse(httpExchange, gsonEpicById);
                    break;
            }
        } else if (splitStrings.length == 5 && splitStrings[4].startsWith("?id=")) {
            int epicId = Integer.parseInt(splitStrings[4].substring(4));
            String gsonSubTaskByEpicId = gson.toJson(httpTaskManager.getSubTasksInTheEpic(epicId));
            writeResponse(httpExchange, gsonSubTaskByEpicId);
        }
    }

    private static void writeResponse(HttpExchange httpExchange, String request) throws IOException {
        httpExchange.sendResponseHeaders(200, 0);
        try (OutputStream outputStream = httpExchange.getResponseBody()) {
            outputStream.write(request.getBytes(DEFAULT_CHARSET));
        }
    }

    private static void handleDeleteRequest(HttpExchange httpExchange, String[] splitStrings) throws IOException {
        if (splitStrings.length == 4 && splitStrings[3].startsWith("?id=")) {
            int id = Integer.parseInt(splitStrings[3].substring(4));
            switch (splitStrings[2]) {
                case "task":
                    httpTaskManager.removeTaskById(id);
                    break;
                case "subtask":
                    httpTaskManager.removeSubTaskById(id);
                    break;
                case "epic":
                    httpTaskManager.removeEpicById(id);
                    break;
            }
        } else if (splitStrings.length == 3) {
            switch (splitStrings[2]) {
                case "task":
                    httpTaskManager.removeAllTasks();
                    break;
                case "subtask":
                    httpTaskManager.removeAllSubTasks();
                    break;
                case "epic":
                    httpTaskManager.removeAllEpics();
                    break;
            }
        }
        httpExchange.sendResponseHeaders(200, 0);
    }

    private static void handlePostRequest(HttpExchange httpExchange, String[] splitStrings) throws IOException {
        String body = new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        if (splitStrings.length == 3) {
            switch (splitStrings[2]) {
                case "task":
                    Task task = gson.fromJson(body, Task.class);
                    task.calculateEndTime();
                    if (httpTaskManager.contains(task)) {
                        httpTaskManager.updateTask(task);
                    } else {
                        httpTaskManager.createTask(task);
                    }
                    break;
                case "subtask":
                    SubTask subTask = gson.fromJson(body, SubTask.class);
                    subTask.calculateEndTime();
                    if (httpTaskManager.contains(subTask)) {
                        httpTaskManager.updateSubTask(subTask);

                    } else {
                        httpTaskManager.createSubTask(subTask);
                    }
                    break;
                case "epic":
                    Epic epic = gson.fromJson(body, Epic.class);
                    if (httpTaskManager.contains(epic)) {
                        httpTaskManager.updateEpic(epic);
                    } else {
                        httpTaskManager.createEpic(epic);
                    }
                    break;
            }
        }
        httpExchange.sendResponseHeaders(201, 0);
    }

}


