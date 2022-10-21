package test;

import manager.FileBackedTasksManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    File file;

    @BeforeEach
    void setUp() {
        file = new File("file.csv");
        taskManager = new FileBackedTasksManager(file);
    }

    @AfterEach
    void cleanUp() {
        file.delete();
    }

    @Test
    void shouldSaveAndLoadTasksSubtasksEpics() {
        Task task1 = TaskFactory.createTask(Status.NEW);
        taskManager.createTask(task1);
        Task task2 = TaskFactory.createTask(Status.DONE);
        taskManager.createTask(task2);
        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic1);
        SubTask subTask1 = TaskFactory.createSubTask(epic1.getId(), Status.NEW);
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = TaskFactory.createSubTask(epic1.getId(), Status.DONE);
        taskManager.createSubTask(subTask2);
        SubTask subTask3 = TaskFactory.createSubTask(epic1.getId(), Status.NEW);
        taskManager.createSubTask(subTask3);
        Epic epic2 = TaskFactory.createEpic(Status.DONE);
        taskManager.createEpic(epic2);
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getEpicById(epic2.getId());

        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);
        assertEquals(taskManager.getTaskById(task1.getId()), fileBackedTasksManager.getTaskById(task1.getId()));
        assertEquals(taskManager.getTaskById(task2.getId()), fileBackedTasksManager.getTaskById(task2.getId()));
        assertEquals(taskManager.getEpicById(epic1.getId()), fileBackedTasksManager.getEpicById(epic1.getId()));
        assertEquals(taskManager.getSubTaskById(subTask1.getId()), fileBackedTasksManager.getSubTaskById(subTask1.getId()));
        assertEquals(taskManager.getSubTaskById(subTask2.getId()), fileBackedTasksManager.getSubTaskById(subTask2.getId()));
        assertEquals(taskManager.getSubTaskById(subTask3.getId()), fileBackedTasksManager.getSubTaskById(subTask3.getId()));
        assertEquals(taskManager.getEpicById(epic2.getId()), fileBackedTasksManager.getEpicById(epic2.getId()));
        assertEquals(taskManager.getHistory().size(), fileBackedTasksManager.getHistory().size());

        taskManager.removeAllTasks();
        taskManager.removeAllSubTasks();
        taskManager.removeAllEpics();
        assertFalse(fileBackedTasksManager.getAllTasks().isEmpty());
        assertFalse(fileBackedTasksManager.getAllSubTasks().isEmpty());
        assertFalse(fileBackedTasksManager.getAllEpics().isEmpty());
    }

    @Test
    void shouldSaveAndLoadEmptyTasksList() {
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);
        assertTrue(fileBackedTasksManager.getAllTasks().isEmpty());
        assertTrue(fileBackedTasksManager.getAllSubTasks().isEmpty());
        assertTrue(fileBackedTasksManager.getAllEpics().isEmpty());
        assertEquals(taskManager.getHistory().size(), fileBackedTasksManager.getHistory().size());
    }

    @Test
    void shouldSaveAndLoadTasksAndEpics() {
        Task task1 = TaskFactory.createTask(Status.NEW);
        taskManager.createTask(task1);
        Task task2 = TaskFactory.createTask(Status.DONE);
        taskManager.createTask(task2);
        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic1);
        Epic epic2 = TaskFactory.createEpic(Status.DONE);
        taskManager.createEpic(epic2);
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getEpicById(epic2.getId());

        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);
        assertEquals(taskManager.getTaskById(task1.getId()), fileBackedTasksManager.getTaskById(task1.getId()));
        assertEquals(taskManager.getTaskById(task2.getId()), fileBackedTasksManager.getTaskById(task2.getId()));
        assertEquals(taskManager.getEpicById(epic1.getId()), fileBackedTasksManager.getEpicById(epic1.getId()));
        assertEquals(taskManager.getEpicById(epic2.getId()), fileBackedTasksManager.getEpicById(epic2.getId()));
        assertEquals(taskManager.getHistory().size(), fileBackedTasksManager.getHistory().size());

        taskManager.removeAllTasks();
        taskManager.removeAllEpics();
        assertFalse(fileBackedTasksManager.getAllTasks().isEmpty());
        assertFalse(fileBackedTasksManager.getAllEpics().isEmpty());
    }

    @Test
    void shouldSaveAndLoadWithoutHistory() {
        Task task1 = TaskFactory.createTask(Status.NEW);
        taskManager.createTask(task1);
        Task task2 = TaskFactory.createTask(Status.DONE);
        taskManager.createTask(task2);
        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic1);
        SubTask subTask1 = TaskFactory.createSubTask(epic1.getId(), Status.NEW);
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = TaskFactory.createSubTask(epic1.getId(), Status.DONE);
        taskManager.createSubTask(subTask2);
        SubTask subTask3 = TaskFactory.createSubTask(epic1.getId(), Status.NEW);
        taskManager.createSubTask(subTask3);
        Epic epic2 = TaskFactory.createEpic(Status.DONE);
        taskManager.createEpic(epic2);

        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);
        assertTrue(taskManager.getHistory().isEmpty());
        assertTrue(fileBackedTasksManager.getHistory().isEmpty());

    }

}