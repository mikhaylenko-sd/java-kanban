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
    private static final String URL = "http://localhost:8080/tasks/";
    private final Gson gson = new Gson();
    private Map<Integer, Task> tasks;
    private Map<Integer, SubTask> subTasks;
    private Map<Integer, Epic> epics;
    private GeneratorId generatorId;
    private HttpResponse<String> response;

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
    void putGetDeleteTasksTest() throws IOException, InterruptedException {
        Task task1 = TaskFactory.createTask(Status.NEW);
        Task task2 = TaskFactory.createTask(Status.DONE);
        Task task3 = TaskFactory.createTask(Status.NEW);
        String stringTask1 = gson.toJson(task1);
        String stringTask2 = gson.toJson(task2);
        String stringTask3 = gson.toJson(task3);
        task1.setId(generatorId.generate());
        task2.setId(generatorId.generate());
        task3.setId(generatorId.generate());
        HttpRequest request1 = createPostRequest("task/", stringTask1);
        HttpRequest request2 = createPostRequest("task/", stringTask2);
        HttpRequest request3 = createPostRequest("task/", stringTask3);
        httpClient.send(request1, HANDLER);
        httpClient.send(request2, HANDLER);
        httpClient.send(request3, HANDLER);

        request3 = createGetRequest("task/");
        response = httpClient.send(request3, HANDLER);
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
        int sizeBefore = tasks.size();
        assertEquals(3, sizeBefore);

        HttpRequest request4 = createGetRequest("task/?id=1");
        response = httpClient.send(request4, HANDLER);
        assertFalse(response.body().isEmpty());
        assertEquals(200, response.statusCode());
        jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Task task = gson.fromJson(jsonObject, Task.class);
        assertEquals(tasks.get(1), task);

        HttpRequest request5 = createDeleteRequest("task/?id=1");
        response = httpClient.send(request5, HANDLER);
        assertEquals(200, response.statusCode());
        request3 = createGetRequest("task/");
        response = httpClient.send(request3, HANDLER);
        jsonElement = JsonParser.parseString(response.body());
        jsonArray = jsonElement.getAsJsonArray();
        tasks.clear();
        fillMap(jsonArray);
        int sizeAfter = tasks.size();
        assertNotEquals(sizeBefore, sizeAfter);

        HttpRequest request6 = createDeleteRequest("task/");
        response = httpClient.send(request6, HANDLER);
        assertEquals(200, response.statusCode());
        request3 = createGetRequest("task/");
        response = httpClient.send(request3, HANDLER);
        jsonElement = JsonParser.parseString(response.body());
        jsonArray = jsonElement.getAsJsonArray();
        tasks.clear();
        fillMap(jsonArray);
        sizeAfter = tasks.size();
        assertEquals(0, sizeAfter);
    }

    @Test
    void putGetDeleteSubTasksTest() throws IOException, InterruptedException {
        Epic epic = TaskFactory.createEpic(Status.NEW);
        String stringEpic = gson.toJson(epic);
        epic.setId(generatorId.generate());
        SubTask subTask1 = TaskFactory.createSubTask(epic.getId(), Status.NEW);
        SubTask subTask2 = TaskFactory.createSubTask(epic.getId(), Status.DONE);
        SubTask subTask3 = TaskFactory.createSubTask(epic.getId(), Status.NEW);
        String stringSubTask1 = gson.toJson(subTask1);
        String stringSubTask2 = gson.toJson(subTask2);
        String stringSubTask3 = gson.toJson(subTask3);
        subTask1.setId(generatorId.generate());
        subTask2.setId(generatorId.generate());
        subTask3.setId(generatorId.generate());
        HttpRequest request = createPostRequest("epic/", stringEpic);
        HttpRequest request1 = createPostRequest("subtask/", stringSubTask1);
        HttpRequest request2 = createPostRequest("subtask/", stringSubTask2);
        HttpRequest request3 = createPostRequest("subtask/", stringSubTask3);
        httpClient.send(request, HANDLER);
        httpClient.send(request1, HANDLER);
        httpClient.send(request2, HANDLER);
        httpClient.send(request3, HANDLER);

        request3 = createGetRequest("subtask/");
        response = httpClient.send(request3, HANDLER);
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

        HttpRequest request4 = createGetRequest("subtask/?id=2");
        response = httpClient.send(request4, HANDLER);
        assertFalse(response.body().isEmpty());
        assertEquals(200, response.statusCode());
        jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        SubTask subTask = gson.fromJson(jsonObject, SubTask.class);
        assertEquals(subTasks.get(2), subTask);

        HttpRequest request7 = createGetRequest("subtask/epic/?id=1");
        response = httpClient.send(request7, HANDLER);
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


        HttpRequest request5 = createDeleteRequest("subtask/?id=2");
        response = httpClient.send(request5, HANDLER);
        assertEquals(200, response.statusCode());
        request3 = createGetRequest("subtask/");
        response = httpClient.send(request3, HANDLER);
        jsonElement = JsonParser.parseString(response.body());
        jsonArray = jsonElement.getAsJsonArray();
        subTasks.clear();
        fillMap(jsonArray);
        int sizeAfter = subTasks.size();
        assertNotEquals(sizeBefore, sizeAfter);

        HttpRequest request6 = createDeleteRequest("subtask/");
        response = httpClient.send(request6, HANDLER);
        assertEquals(200, response.statusCode());
        request3 = createGetRequest("subtask/");
        response = httpClient.send(request3, HANDLER);
        jsonElement = JsonParser.parseString(response.body());
        jsonArray = jsonElement.getAsJsonArray();
        subTasks.clear();
        fillMap(jsonArray);
        sizeAfter = subTasks.size();
        assertEquals(0, sizeAfter);
    }

    @Test
    void putGetDeleteEpicsTest() throws IOException, InterruptedException {
        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        Epic epic2 = TaskFactory.createEpic(Status.DONE);
        Epic epic3 = TaskFactory.createEpic(Status.NEW);
        String stringEpic1 = gson.toJson(epic1);
        String stringEpic2 = gson.toJson(epic2);
        String stringEpic3 = gson.toJson(epic3);
        epic1.setId(generatorId.generate());
        epic2.setId(generatorId.generate());
        epic3.setId(generatorId.generate());
        HttpRequest request1 = createPostRequest("epic/", stringEpic1);
        HttpRequest request2 = createPostRequest("epic/", stringEpic2);
        HttpRequest request3 = createPostRequest("epic/", stringEpic3);
        httpClient.send(request1, HANDLER);
        httpClient.send(request2, HANDLER);
        httpClient.send(request3, HANDLER);

        request3 = createGetRequest("epic/");
        response = httpClient.send(request3, HANDLER);
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
        int sizeBefore = epics.size();
        assertEquals(3, sizeBefore);

        HttpRequest request4 = createGetRequest("epic/?id=1");
        response = httpClient.send(request4, HANDLER);
        assertFalse(response.body().isEmpty());
        assertEquals(200, response.statusCode());
        jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Epic epic = gson.fromJson(jsonObject, Epic.class);
        assertEquals(epics.get(1), epic);

        HttpRequest request5 = createDeleteRequest("epic/?id=1");
        response = httpClient.send(request5, HANDLER);
        assertEquals(200, response.statusCode());
        request3 = createGetRequest("epic/");
        response = httpClient.send(request3, HANDLER);
        jsonElement = JsonParser.parseString(response.body());
        jsonArray = jsonElement.getAsJsonArray();
        epics.clear();
        fillMap(jsonArray);
        int sizeAfter = epics.size();
        assertNotEquals(sizeBefore, sizeAfter);

        HttpRequest request6 = createDeleteRequest("epic/");
        response = httpClient.send(request6, HANDLER);
        assertEquals(200, response.statusCode());
        request3 = createGetRequest("epic/");
        response = httpClient.send(request3, HANDLER);
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

        String stringTask1 = gson.toJson(task1);
        String stringTask2 = gson.toJson(task2);
        String stringTask3 = gson.toJson(task3);
        String stringEpic = gson.toJson(epic);
        String stringSubTask1 = gson.toJson(subTask1);
        String stringSubTask2 = gson.toJson(subTask2);

        task1.setId(generatorId.generate());
        task2.setId(generatorId.generate());
        task3.setId(generatorId.generate());
        epic.setId(generatorId.generate());
        subTask1.setId(generatorId.generate());
        subTask2.setId(generatorId.generate());

        HttpRequest request1 = createPostRequest("task/", stringTask1);
        HttpRequest request2 = createPostRequest("task/", stringTask2);
        HttpRequest request3 = createPostRequest("task/", stringTask3);
        HttpRequest request4 = createPostRequest("epic/", stringEpic);
        HttpRequest request5 = createPostRequest("subtask/", stringSubTask1);
        HttpRequest request6 = createPostRequest("subtask/", stringSubTask2);

        httpClient.send(request1, HANDLER);
        httpClient.send(request2, HANDLER);
        httpClient.send(request3, HANDLER);
        httpClient.send(request4, HANDLER);
        httpClient.send(request5, HANDLER);
        httpClient.send(request6, HANDLER);

        request4 = createGetRequest("epic/?id=4");
        response = httpClient.send(request4, HANDLER);
        assertFalse(response.body().isEmpty());
        assertEquals(200, response.statusCode());

        request2 = createGetRequest("task/?id=2");
        response = httpClient.send(request2, HANDLER);
        assertFalse(response.body().isEmpty());
        assertEquals(200, response.statusCode());

        HttpRequest request = createGetRequest("history/");
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

        request4 = createDeleteRequest("epic/?id=4");
        response = httpClient.send(request4, HANDLER);
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

        String stringTask1 = gson.toJson(task1);
        String stringTask2 = gson.toJson(task2);
        String stringEpic = gson.toJson(epic);
        String stringSubTask = gson.toJson(subTask);

        task1.setId(generatorId.generate());
        task2.setId(generatorId.generate());
        epic.setId(generatorId.generate());
        subTask.setId(generatorId.generate());

        HttpRequest request1 = createPostRequest("task/", stringTask1);
        HttpRequest request2 = createPostRequest("task/", stringTask2);
        HttpRequest request3 = createPostRequest("epic/", stringEpic);
        HttpRequest request4 = createPostRequest("subtask/", stringSubTask);

        httpClient.send(request1, HANDLER);
        httpClient.send(request2, HANDLER);
        httpClient.send(request3, HANDLER);
        httpClient.send(request4, HANDLER);

        Task task = null;
        Task[] tasks = new Task[4];

        HttpRequest request = createGetRequest("");
        response = httpClient.send(request, HANDLER);
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