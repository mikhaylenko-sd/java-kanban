package test.http;

import http.HTTPTaskManager;
import http.KVServer;
import manager.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;
import test.TaskFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HTTPTaskManagerTest {

    private HTTPTaskManager httpTaskManager;
    private KVServer kvServer;
    private static final String BACKUP_KEY = "backupKey1";
    private static final String URL = "http://localhost:8078";

    @BeforeEach
    void setUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        String backupKey = "backupKey1";
        httpTaskManager = Managers.getDefault(backupKey);
    }

    @AfterEach
    void cleanUp() {
        kvServer.stop();
    }

    @Test
    void shouldSaveAndLoadTasksSubtasksEpics() {
        Task task1 = TaskFactory.createTask(Status.NEW);
        httpTaskManager.createTask(task1);
        Task task2 = TaskFactory.createTask(Status.DONE);
        httpTaskManager.createTask(task2);
        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        httpTaskManager.createEpic(epic1);
        SubTask subTask1 = TaskFactory.createSubTask(epic1.getId(), Status.NEW);
        httpTaskManager.createSubTask(subTask1);
        SubTask subTask2 = TaskFactory.createSubTask(epic1.getId(), Status.DONE);
        httpTaskManager.createSubTask(subTask2);
        SubTask subTask3 = TaskFactory.createSubTask(epic1.getId(), Status.NEW);
        httpTaskManager.createSubTask(subTask3);
        Epic epic2 = TaskFactory.createEpic(Status.DONE);
        httpTaskManager.createEpic(epic2);
        httpTaskManager.getTaskById(task2.getId());
        httpTaskManager.getTaskById(task2.getId());
        httpTaskManager.getEpicById(epic2.getId());

        HTTPTaskManager loadedHttpTaskManager = HTTPTaskManager.loadFromKVServer(URL, BACKUP_KEY);
        assertEquals(httpTaskManager.getTaskById(task1.getId()), loadedHttpTaskManager.getTaskById(task1.getId()));
        assertEquals(httpTaskManager.getTaskById(task2.getId()), loadedHttpTaskManager.getTaskById(task2.getId()));
        assertEquals(httpTaskManager.getEpicById(epic1.getId()), loadedHttpTaskManager.getEpicById(epic1.getId()));
        assertEquals(httpTaskManager.getSubTaskById(subTask1.getId()), loadedHttpTaskManager.getSubTaskById(subTask1.getId()));
        assertEquals(httpTaskManager.getSubTaskById(subTask2.getId()), loadedHttpTaskManager.getSubTaskById(subTask2.getId()));
        assertEquals(httpTaskManager.getSubTaskById(subTask3.getId()), loadedHttpTaskManager.getSubTaskById(subTask3.getId()));
        assertEquals(httpTaskManager.getEpicById(epic2.getId()), loadedHttpTaskManager.getEpicById(epic2.getId()));
        assertEquals(httpTaskManager.getHistory().size(), loadedHttpTaskManager.getHistory().size());

        httpTaskManager.removeAllTasks();
        httpTaskManager.removeAllSubTasks();
        httpTaskManager.removeAllEpics();
        assertFalse(loadedHttpTaskManager.getAllTasks().isEmpty());
        assertFalse(loadedHttpTaskManager.getAllSubTasks().isEmpty());
        assertFalse(loadedHttpTaskManager.getAllEpics().isEmpty());
    }

    @Test
    void shouldSaveAndLoadEmptyTasksList() {
        HTTPTaskManager loadedHttpTaskManager = HTTPTaskManager.loadFromKVServer(URL, BACKUP_KEY);
        assertTrue(loadedHttpTaskManager.getAllTasks().isEmpty());
        assertTrue(loadedHttpTaskManager.getAllSubTasks().isEmpty());
        assertTrue(loadedHttpTaskManager.getAllEpics().isEmpty());
        assertEquals(httpTaskManager.getHistory().size(), loadedHttpTaskManager.getHistory().size());
    }

    @Test
    void shouldSaveAndLoadTasksAndEpics() {
        Task task1 = TaskFactory.createTask(Status.NEW);
        httpTaskManager.createTask(task1);
        Task task2 = TaskFactory.createTask(Status.DONE);
        httpTaskManager.createTask(task2);
        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        httpTaskManager.createEpic(epic1);
        Epic epic2 = TaskFactory.createEpic(Status.DONE);
        httpTaskManager.createEpic(epic2);
        httpTaskManager.getTaskById(task2.getId());
        httpTaskManager.getTaskById(task2.getId());
        httpTaskManager.getEpicById(epic2.getId());

        HTTPTaskManager loadedHttpTaskManager = HTTPTaskManager.loadFromKVServer(URL, BACKUP_KEY);
        assertEquals(httpTaskManager.getTaskById(task1.getId()), loadedHttpTaskManager.getTaskById(task1.getId()));
        assertEquals(httpTaskManager.getTaskById(task2.getId()), loadedHttpTaskManager.getTaskById(task2.getId()));
        assertEquals(httpTaskManager.getEpicById(epic1.getId()), loadedHttpTaskManager.getEpicById(epic1.getId()));
        assertEquals(httpTaskManager.getEpicById(epic2.getId()), loadedHttpTaskManager.getEpicById(epic2.getId()));
        assertEquals(httpTaskManager.getHistory().size(), loadedHttpTaskManager.getHistory().size());

        httpTaskManager.removeAllTasks();
        httpTaskManager.removeAllEpics();
        assertFalse(loadedHttpTaskManager.getAllTasks().isEmpty());
        assertFalse(loadedHttpTaskManager.getAllEpics().isEmpty());
    }

    @Test
    void shouldSaveAndLoadWithoutHistory() {
        Task task1 = TaskFactory.createTask(Status.NEW);
        httpTaskManager.createTask(task1);
        Task task2 = TaskFactory.createTask(Status.DONE);
        httpTaskManager.createTask(task2);
        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        httpTaskManager.createEpic(epic1);
        SubTask subTask1 = TaskFactory.createSubTask(epic1.getId(), Status.NEW);
        httpTaskManager.createSubTask(subTask1);
        SubTask subTask2 = TaskFactory.createSubTask(epic1.getId(), Status.DONE);
        httpTaskManager.createSubTask(subTask2);
        SubTask subTask3 = TaskFactory.createSubTask(epic1.getId(), Status.NEW);
        httpTaskManager.createSubTask(subTask3);
        Epic epic2 = TaskFactory.createEpic(Status.DONE);
        httpTaskManager.createEpic(epic2);

        HTTPTaskManager loadedHttpTaskManager = HTTPTaskManager.loadFromKVServer(URL, BACKUP_KEY);
        assertTrue(httpTaskManager.getHistory().isEmpty());
        assertTrue(loadedHttpTaskManager.getHistory().isEmpty());
    }


}