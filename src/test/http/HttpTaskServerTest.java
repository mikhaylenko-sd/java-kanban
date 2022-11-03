package test.http;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import http.HttpTaskServer;
import http.KVServer;
import manager.GeneratorId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;
import test.TaskFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskServerTest {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private static final HttpResponse.BodyHandler<String> HANDLER = HttpResponse.BodyHandlers.ofString();
    private static final String TASK_PATH = "task/";
    private static final String SUBTASK_PATH = "subtask/";
    private static final String EPIC_PATH = "epic/";
    private static final String URL = "http://localhost:8080/tasks/";
    private final Gson gson = new Gson();
    private Map<Integer, Task> tasks;
    private Map<Integer, SubTask> subTasks;
    private Map<Integer, Epic> epics;
    private GeneratorId generatorId;

    private KVServer kvServer;

    @BeforeEach
    void setUp() throws IOException {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
        generatorId = new GeneratorId();
        kvServer = new KVServer();
        kvServer.start();
        HttpTaskServer.start();
    }

    @AfterEach
    void cleanUp() {
        kvServer.stop();
        HttpTaskServer.stop();
    }

    @Test
    void putTasksTest() throws IOException, InterruptedException {
        HttpRequest request = createPostRequest(TASK_PATH, createStringTask(TaskFactory.createTask(Status.NEW)));
        HttpResponse<String> response = httpClient.send(request, HANDLER);
        assertEquals(201, response.statusCode());

        request = createPostRequest(TASK_PATH, createStringTask(TaskFactory.createTask(Status.DONE)));
        response = httpClient.send(request, HANDLER);
        assertEquals(201, response.statusCode());
    }

    @Test
    void getTasksTest() throws IOException, InterruptedException {
        Task task1 = TaskFactory.createTask(Status.NEW);
        Task task2 = TaskFactory.createTask(Status.DONE);
        Task task3 = TaskFactory.createTask(Status.NEW);
        HttpRequest request = createPostRequest(TASK_PATH, createStringTask(task1));
        httpClient.send(request, HANDLER);
        request = createPostRequest(TASK_PATH, createStringTask(task2));
        httpClient.send(request, HANDLER);
        request = createPostRequest(TASK_PATH, createStringTask(task3));
        httpClient.send(request, HANDLER);

        request = createGetRequest(TASK_PATH);
        HttpResponse<String> response = httpClient.send(request, HANDLER);
        assertFalse(response.body().isEmpty());
        assertEquals(200, response.statusCode());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertFalse(jsonArray.isEmpty());
        fillMap(jsonArray);
        assertEquals(task1, tasks.get(task1.getId()));
        assertEquals(task2, tasks.get(task2.getId()));
        assertEquals(task3, tasks.get(task3.getId()));
        int sizeAfter = tasks.size();
        assertEquals(3, sizeAfter);

        request = createGetRequest(TASK_PATH + "?id=1");
        response = httpClient.send(request, HANDLER);
        assertFalse(response.body().isEmpty());
        assertEquals(200, response.statusCode());
        jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Task task = gson.fromJson(jsonObject, Task.class);
        assertEquals(tasks.get(1), task);
    }

    @Test
    void deleteTasksTest() throws IOException, InterruptedException {
        HttpRequest request = createPostRequest(TASK_PATH, createStringTask(TaskFactory.createTask(Status.NEW)));
        httpClient.send(request, HANDLER);
        request = createPostRequest(TASK_PATH, createStringTask(TaskFactory.createTask(Status.DONE)));
        httpClient.send(request, HANDLER);
        request = createPostRequest(TASK_PATH, createStringTask(TaskFactory.createTask(Status.NEW)));
        httpClient.send(request, HANDLER);


        request = createDeleteRequest(TASK_PATH + "?id=1");
        HttpResponse<String> response = httpClient.send(request, HANDLER);
        assertEquals(200, response.statusCode());
        request = createGetRequest(TASK_PATH);
        response = httpClient.send(request, HANDLER);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        tasks.clear();
        fillMap(jsonArray);
        int sizeAfter = tasks.size();
        assertNotEquals(3, sizeAfter);

        request = createDeleteRequest(TASK_PATH);
        response = httpClient.send(request, HANDLER);
        assertEquals(200, response.statusCode());
        request = createGetRequest(TASK_PATH);
        response = httpClient.send(request, HANDLER);
        jsonElement = JsonParser.parseString(response.body());
        jsonArray = jsonElement.getAsJsonArray();
        tasks.clear();
        fillMap(jsonArray);
        sizeAfter = tasks.size();
        assertEquals(0, sizeAfter);
    }

    @Test
    void putSubTasksTest() throws IOException, InterruptedException {
        Epic epic = TaskFactory.createEpic(Status.NEW);
        HttpRequest request = createPostRequest(EPIC_PATH, createStringTask(epic));
        HttpResponse<String> response = httpClient.send(request, HANDLER);
        assertEquals(201, response.statusCode());
        request = createPostRequest(SUBTASK_PATH, createStringTask(TaskFactory.createSubTask(epic.getId(), Status.DONE)));
        response = httpClient.send(request, HANDLER);
        assertEquals(201, response.statusCode());
        request = createPostRequest(SUBTASK_PATH, createStringTask(TaskFactory.createSubTask(epic.getId(), Status.NEW)));
        response = httpClient.send(request, HANDLER);
        assertEquals(201, response.statusCode());
    }

    @Test
    void getSubTasksTest() throws IOException, InterruptedException {
        Epic epic = TaskFactory.createEpic(Status.NEW);
        HttpRequest request = createPostRequest(EPIC_PATH, createStringTask(epic));
        httpClient.send(request, HANDLER);
        SubTask subTask1 = TaskFactory.createSubTask(epic.getId(), Status.DONE);
        SubTask subTask2 = TaskFactory.createSubTask(epic.getId(), Status.NEW);
        SubTask subTask3 = TaskFactory.createSubTask(epic.getId(), Status.DONE);
        request = createPostRequest(SUBTASK_PATH, createStringTask(subTask1));
        httpClient.send(request, HANDLER);
        request = createPostRequest(SUBTASK_PATH, createStringTask(subTask2));
        httpClient.send(request, HANDLER);
        request = createPostRequest(SUBTASK_PATH, createStringTask(subTask3));
        httpClient.send(request, HANDLER);

        request = createGetRequest(SUBTASK_PATH);
        HttpResponse<String> response = httpClient.send(request, HANDLER);
        assertFalse(response.body().isEmpty());
        assertEquals(200, response.statusCode());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertFalse(jsonArray.isEmpty());
        fillMap(jsonArray);
        assertEquals(subTask1, subTasks.get(subTask1.getId()));
        assertEquals(subTask2, subTasks.get(subTask2.getId()));
        assertEquals(subTask3, subTasks.get(subTask3.getId()));
        int sizeBefore = subTasks.size();
        assertEquals(3, sizeBefore);

        request = createGetRequest(SUBTASK_PATH + "?id=2");
        response = httpClient.send(request, HANDLER);
        assertFalse(response.body().isEmpty());
        assertEquals(200, response.statusCode());
        jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        SubTask subTask = gson.fromJson(jsonObject, SubTask.class);
        assertEquals(subTasks.get(2), subTask);

        request = createGetRequest(SUBTASK_PATH + "epic/?id=1");
        response = httpClient.send(request, HANDLER);
        subTasks.clear();
        assertFalse(response.body().isEmpty());
        assertEquals(200, response.statusCode());
        jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray());
        jsonArray = jsonElement.getAsJsonArray();
        fillMap(jsonArray);
        assertEquals(sizeBefore, subTasks.size());
        assertEquals(subTask1.getEpicId(), subTasks.get(2).getEpicId());
        assertEquals(subTask2.getEpicId(), subTasks.get(3).getEpicId());
        assertEquals(subTask3.getEpicId(), subTasks.get(4).getEpicId());
    }

    @Test
    void deleteSubTasksTest() throws IOException, InterruptedException {
        Epic epic = TaskFactory.createEpic(Status.NEW);
        HttpRequest request = createPostRequest(EPIC_PATH, createStringTask(epic));
        httpClient.send(request, HANDLER);
        request = createPostRequest(SUBTASK_PATH, createStringTask(TaskFactory.createSubTask(epic.getId(), Status.DONE)));
        httpClient.send(request, HANDLER);
        request = createPostRequest(SUBTASK_PATH, createStringTask(TaskFactory.createSubTask(epic.getId(), Status.NEW)));
        httpClient.send(request, HANDLER);

        request = createDeleteRequest(SUBTASK_PATH + "?id=2");
        HttpResponse<String> response = httpClient.send(request, HANDLER);
        assertEquals(200, response.statusCode());
        request = createGetRequest(SUBTASK_PATH);
        response = httpClient.send(request, HANDLER);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        subTasks.clear();
        fillMap(jsonArray);
        int sizeAfter = subTasks.size();
        assertNotEquals(3, sizeAfter);

        request = createDeleteRequest(SUBTASK_PATH);
        response = httpClient.send(request, HANDLER);
        assertEquals(200, response.statusCode());
        request = createGetRequest(SUBTASK_PATH);
        response = httpClient.send(request, HANDLER);
        jsonElement = JsonParser.parseString(response.body());
        jsonArray = jsonElement.getAsJsonArray();
        subTasks.clear();
        fillMap(jsonArray);
        sizeAfter = subTasks.size();
        assertEquals(0, sizeAfter);
    }

    @Test
    void putEpicsTest() throws IOException, InterruptedException {
        HttpRequest request = createPostRequest(EPIC_PATH, createStringTask(TaskFactory.createEpic(Status.NEW)));
        HttpResponse<String> response = httpClient.send(request, HANDLER);
        assertEquals(201, response.statusCode());

        request = createPostRequest(EPIC_PATH, createStringTask(TaskFactory.createEpic(Status.DONE)));
        response = httpClient.send(request, HANDLER);
        assertEquals(201, response.statusCode());
    }

    @Test
    void getEpicsTest() throws IOException, InterruptedException {
        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        Epic epic2 = TaskFactory.createEpic(Status.DONE);
        Epic epic3 = TaskFactory.createEpic(Status.NEW);
        HttpRequest request = createPostRequest(EPIC_PATH, createStringTask(epic1));
        httpClient.send(request, HANDLER);
        request = createPostRequest(EPIC_PATH, createStringTask(epic2));
        httpClient.send(request, HANDLER);
        request = createPostRequest(EPIC_PATH, createStringTask(epic3));
        httpClient.send(request, HANDLER);

        request = createGetRequest(EPIC_PATH);
        HttpResponse<String> response = httpClient.send(request, HANDLER);
        assertFalse(response.body().isEmpty());
        assertEquals(200, response.statusCode());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertFalse(jsonArray.isEmpty());
        fillMap(jsonArray);
        assertEquals(epic1, epics.get(epic1.getId()));
        assertEquals(epic2, epics.get(epic2.getId()));
        assertEquals(epic3, epics.get(epic3.getId()));
        int sizeAfter = epics.size();
        assertEquals(3, sizeAfter);

        request = createGetRequest(EPIC_PATH + "?id=1");
        response = httpClient.send(request, HANDLER);
        assertFalse(response.body().isEmpty());
        assertEquals(200, response.statusCode());
        jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Epic epic = gson.fromJson(jsonObject, Epic.class);
        assertEquals(epics.get(1), epic);
    }

    @Test
    void deleteEpicsTest() throws IOException, InterruptedException {
        HttpRequest request = createPostRequest(EPIC_PATH, createStringTask(TaskFactory.createEpic(Status.NEW)));
        httpClient.send(request, HANDLER);
        request = createPostRequest(EPIC_PATH, createStringTask(TaskFactory.createEpic(Status.DONE)));
        httpClient.send(request, HANDLER);
        request = createPostRequest(EPIC_PATH, createStringTask(TaskFactory.createEpic(Status.NEW)));
        httpClient.send(request, HANDLER);


        request = createDeleteRequest(EPIC_PATH + "?id=1");
        HttpResponse<String> response = httpClient.send(request, HANDLER);
        assertEquals(200, response.statusCode());
        request = createGetRequest(EPIC_PATH);
        response = httpClient.send(request, HANDLER);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        epics.clear();
        fillMap(jsonArray);
        int sizeAfter = epics.size();
        assertNotEquals(3, sizeAfter);

        request = createDeleteRequest(EPIC_PATH);
        response = httpClient.send(request, HANDLER);
        assertEquals(200, response.statusCode());
        request = createGetRequest(EPIC_PATH);
        response = httpClient.send(request, HANDLER);
        jsonElement = JsonParser.parseString(response.body());
        jsonArray = jsonElement.getAsJsonArray();
        epics.clear();
        fillMap(jsonArray);
        sizeAfter = epics.size();
        assertEquals(0, sizeAfter);
    }


    @Test
    void getHistoryTest() throws IOException, InterruptedException {
        Task task1 = TaskFactory.createTask(Status.NEW);
        Task task2 = TaskFactory.createTask(Status.DONE);
        Task task3 = TaskFactory.createTask(Status.NEW);
        Epic epic = TaskFactory.createEpic(Status.NEW);
        SubTask subTask1 = TaskFactory.createSubTask(epic.getId(), Status.NEW);
        SubTask subTask2 = TaskFactory.createSubTask(epic.getId(), Status.DONE);

        HttpRequest request = createPostRequest(TASK_PATH, createStringTask(task1));
        httpClient.send(request, HANDLER);
        request = createPostRequest(TASK_PATH, createStringTask(task2));
        httpClient.send(request, HANDLER);
        request = createPostRequest(TASK_PATH, createStringTask(task3));
        httpClient.send(request, HANDLER);
        request = createPostRequest(EPIC_PATH, createStringTask(epic));
        httpClient.send(request, HANDLER);
        request = createPostRequest(SUBTASK_PATH, createStringTask(subTask1));
        httpClient.send(request, HANDLER);
        request = createPostRequest(SUBTASK_PATH, createStringTask(subTask2));
        httpClient.send(request, HANDLER);

        request = createGetRequest(EPIC_PATH + "?id=4");
        HttpResponse<String> response = httpClient.send(request, HANDLER);
        assertFalse(response.body().isEmpty());
        assertEquals(200, response.statusCode());

        request = createGetRequest(TASK_PATH + "?id=2");
        response = httpClient.send(request, HANDLER);
        assertFalse(response.body().isEmpty());
        assertEquals(200, response.statusCode());

        request = createGetRequest("history/");
        response = httpClient.send(request, HANDLER);
        assertFalse(response.body().isEmpty());
        assertEquals(200, response.statusCode());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertFalse(jsonArray.isEmpty());
        assertEquals(2, jsonArray.size());

        int id1 = jsonArray.get(0).getAsJsonObject().get("id").getAsInt();
        int id2 = jsonArray.get(1).getAsJsonObject().get("id").getAsInt();
        assertEquals(epic.getId(), id1);
        assertEquals(task2.getId(), id2);

        request = createDeleteRequest(EPIC_PATH + "?id=4");
        response = httpClient.send(request, HANDLER);
        assertEquals(200, response.statusCode());
        assertTrue(response.body().isEmpty());

        request = createGetRequest("history/");
        response = httpClient.send(request, HANDLER);
        assertFalse(response.body().isEmpty());
        assertEquals(200, response.statusCode());
        jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray());
        jsonArray = jsonElement.getAsJsonArray();
        assertFalse(jsonArray.isEmpty());
        assertEquals(1, jsonArray.size());
        id2 = jsonArray.get(0).getAsJsonObject().get("id").getAsInt();
        assertEquals(task2.getId(), id2);
    }

    @Test
    void getPrioritizedTasksTest() throws IOException, InterruptedException {
        Task task1 = TaskFactory.createTaskWithTime("02.11.22 18:00", 30);
        Task task2 = TaskFactory.createTaskWithTime("02.11.22 17:00", 10);
        Epic epic = TaskFactory.createEpicWithTime();
        SubTask subTask = TaskFactory.createSubTaskWithTime(3, "02.11.22 13:00", 60);
        HttpRequest request = createPostRequest(TASK_PATH, createStringTask(task1));
        httpClient.send(request, HANDLER);
        request = createPostRequest(TASK_PATH, createStringTask(task2));
        httpClient.send(request, HANDLER);
        request = createPostRequest(EPIC_PATH, createStringTask(epic));
        httpClient.send(request, HANDLER);
        request = createPostRequest(SUBTASK_PATH, createStringTask(subTask));
        httpClient.send(request, HANDLER);

        Task task = null;
        Task[] tasks = new Task[4];

        request = createGetRequest("");
        HttpResponse<String> response = httpClient.send(request, HANDLER);
        assertFalse(response.body().isEmpty());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertFalse(jsonArray.isEmpty());
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            if (jsonObject.get("taskType").getAsString().equals("TASK")) {
                task = gson.fromJson(jsonObject, Task.class);
            } else if (jsonObject.get("taskType").getAsString().equals("SUBTASK")) {
                task = gson.fromJson(jsonObject, SubTask.class);
            } else if (jsonObject.get("taskType").getAsString().equals("EPIC")) {
                task = gson.fromJson(jsonObject, Epic.class);
            }
            tasks[i] = task;
        }
        assertNotNull(tasks[0]);
        assertEquals(epic.getId(), tasks[0].getId());
        assertEquals(subTask, tasks[1]);
        assertEquals(task2, tasks[2]);
        assertEquals(task1, tasks[3]);
    }

    private String createStringTask(Task task) {
        String stringTask = gson.toJson(task);
        task.setId(generatorId.generate());
        return stringTask;
    }

    private HttpRequest createGetRequest(String path) {
        return HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(URL + path))
                .build();
    }

    private HttpRequest createPostRequest(String path, String value) {
        return HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(value))
                .uri(URI.create(URL + path))
                .build();
    }

    private HttpRequest createDeleteRequest(String path) {
        return HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(URL + path))
                .build();
    }

    private void fillMap(JsonArray jsonArray) {
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            if (jsonObject.get("taskType").getAsString().equals("TASK")) {
                Task task = gson.fromJson(jsonObject, Task.class);
                tasks.put(task.getId(), task);
            } else if (jsonObject.get("taskType").getAsString().equals("SUBTASK")) {
                SubTask subTask = gson.fromJson(jsonObject, SubTask.class);
                subTasks.put(subTask.getId(), subTask);
            } else if (jsonObject.get("taskType").getAsString().equals("EPIC")) {
                Epic epic = gson.fromJson(jsonObject, Epic.class);
                epics.put(epic.getId(), epic);
            }
        }
    }

}