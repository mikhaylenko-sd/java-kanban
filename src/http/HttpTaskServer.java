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
    private static final String TASK = "task";
    private static final String SUBTASK = "subtask";
    private static final String EPIC = "epic";
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
                    System.out.println("По указанному адресу нет ресурса. Проверьте URL-адрес ресурса и повторите запрос.");
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
                case TASK:
                    String gsonAllTasks = gson.toJson(httpTaskManager.getAllTasks());
                    writeResponse(httpExchange, gsonAllTasks);
                    break;
                case SUBTASK:
                    String gsonAllSubTasks = gson.toJson(httpTaskManager.getAllSubTasks());
                    writeResponse(httpExchange, gsonAllSubTasks);
                    break;
                case EPIC:
                    String gsonAllEpics = gson.toJson(httpTaskManager.getAllEpics());
                    writeResponse(httpExchange, gsonAllEpics);
                    break;
                case "history":
                    String gsonHistory = gson.toJson(httpTaskManager.getHistory());
                    writeResponse(httpExchange, gsonHistory);
                    break;
            }
        } else if (splitStrings.length == 4 && splitStrings[3].startsWith("?id=")) {
            int id = getTaskId(splitStrings);
            switch (splitStrings[2]) {
                case TASK:
                    String gsonTaskById = gson.toJson(httpTaskManager.getTaskById(id));
                    writeResponse(httpExchange, gsonTaskById);
                    break;
                case SUBTASK:
                    String gsonSubTaskById = gson.toJson(httpTaskManager.getSubTaskById(id));
                    writeResponse(httpExchange, gsonSubTaskById);
                    break;
                case EPIC:
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
        try {
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream outputStream = httpExchange.getResponseBody()) {
                outputStream.write(request.getBytes(DEFAULT_CHARSET));
            }
        } catch (IOException e) {
            System.out.println("На стороне сервера произошла непредвиденная ошибка. Попробуйте повторить запрос позже.");
            httpExchange.sendResponseHeaders(500, 0);
        }
    }

    private static void handleDeleteRequest(HttpExchange httpExchange, String[] splitStrings) throws IOException {
        if (splitStrings.length == 4 && splitStrings[3].startsWith("?id=")) {
            int id = getTaskId(splitStrings);
            switch (splitStrings[2]) {
                case TASK:
                    httpTaskManager.removeTaskById(id);
                    break;
                case SUBTASK:
                    httpTaskManager.removeSubTaskById(id);
                    break;
                case EPIC:
                    httpTaskManager.removeEpicById(id);
                    break;
            }
        } else if (splitStrings.length == 3) {
            switch (splitStrings[2]) {
                case TASK:
                    httpTaskManager.removeAllTasks();
                    break;
                case SUBTASK:
                    httpTaskManager.removeAllSubTasks();
                    break;
                case EPIC:
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
                case TASK:
                    Task task = gson.fromJson(body, Task.class);
                    task.calculateEndTime();
                    if (httpTaskManager.contains(task)) {
                        httpTaskManager.updateTask(task);
                    } else {
                        httpTaskManager.createTask(task);
                    }
                    break;
                case SUBTASK:
                    SubTask subTask = gson.fromJson(body, SubTask.class);
                    subTask.calculateEndTime();
                    if (httpTaskManager.contains(subTask)) {
                        httpTaskManager.updateSubTask(subTask);

                    } else {
                        httpTaskManager.createSubTask(subTask);
                    }
                    break;
                case EPIC:
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

    private static int getTaskId(String[] splitStrings) {
        String query = "?id=";
        return Integer.parseInt(splitStrings[3].substring(query.length()));
    }

}


