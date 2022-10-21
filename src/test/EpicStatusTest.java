package test;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicStatusTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void shouldReturnEpicStatusWithoutSubtasks() {
        Epic epic = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic);

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void shouldReturnEpicStatusWithNewSubtasks() {
        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic1);
        SubTask subTask1 = TaskFactory.createSubTask(epic1.getId(),Status.NEW);
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = TaskFactory.createSubTask(epic1.getId(),Status.NEW);
        taskManager.createSubTask(subTask2);
        SubTask subTask3 = TaskFactory.createSubTask(epic1.getId(),Status.NEW);
        taskManager.createSubTask(subTask3);

        assertEquals(Status.NEW, epic1.getStatus());
    }

    @Test
    public void shouldReturnEpicStatusWithDoneSubtasks() {
        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic1);
        SubTask subTask1 = TaskFactory.createSubTask(epic1.getId(),Status.DONE);
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = TaskFactory.createSubTask(epic1.getId(),Status.DONE);
        taskManager.createSubTask(subTask2);
        SubTask subTask3 = TaskFactory.createSubTask(epic1.getId(),Status.DONE);
        taskManager.createSubTask(subTask3);
        assertEquals(Status.DONE, epic1.getStatus());
    }

    @Test
    public void shouldReturnEpicStatusWithNewAndDoneSubtasks() {
        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic1);
        SubTask subTask1 = TaskFactory.createSubTask(epic1.getId(),Status.NEW);
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = TaskFactory.createSubTask(epic1.getId(),Status.DONE);
        taskManager.createSubTask(subTask2);
        SubTask subTask3 = TaskFactory.createSubTask(epic1.getId(),Status.NEW);
        taskManager.createSubTask(subTask3);
        taskManager.removeSubTaskById(4);
        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }

    @Test
    public void shouldReturnEpicStatusWithInProgressSubtasks() {
        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic1);
        SubTask subTask1 = TaskFactory.createSubTask(epic1.getId(),Status.IN_PROGRESS);
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = TaskFactory.createSubTask(epic1.getId(),Status.IN_PROGRESS);
        taskManager.createSubTask(subTask2);
        SubTask subTask3 = TaskFactory.createSubTask(epic1.getId(),Status.IN_PROGRESS);
        taskManager.createSubTask(subTask3);
        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }

}