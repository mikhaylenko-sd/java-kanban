package test;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DateAndTimeTest {
    TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @DisplayName("shouldAddTasksWithNotIntersectedTime")
    @Test
    void add() {
        Task task1 = TaskFactory.createTaskWithTime("01.10.22 09:00", 60);
        taskManager.createTask(task1);
        Task task2 = TaskFactory.createTaskWithTime("01.10.22 17:00", 120);
        taskManager.createTask(task2);
        Epic epic1 = TaskFactory.createEpicWithTime();
        taskManager.createEpic(epic1);
        SubTask subtask1 = TaskFactory.createSubTaskWithTime(epic1.getId(), "01.10.22 11:00", 30);
        taskManager.createSubTask(subtask1);
        SubTask subtask2 = TaskFactory.createSubTaskWithTime(epic1.getId(), "01.10.22 11:31", 30);
        taskManager.createSubTask(subtask2);

        Task task3 = TaskFactory.createTaskWithTime("01.10.22 12:30", 10);
        taskManager.createTask(task3);
        Epic epic2 = TaskFactory.createEpicWithTime();
        taskManager.createEpic(epic2);

        assertEquals(7, taskManager.getPrioritizedTasks().size());
        assertEquals(epic1.getStartTime(), subtask1.getStartTime());
        assertEquals(epic1.getEndTime(), subtask2.getEndTime());
        assertNull(epic2.getStartTime());
        assertNull(epic2.getEndTime());
        List<Task> prioritizedTasks = new ArrayList<>(taskManager.getPrioritizedTasks());
        assertEquals(task1, prioritizedTasks.get(0));
        assertEquals(epic2, prioritizedTasks.get(6));
        assertEquals(subtask1, prioritizedTasks.get(2));
    }

    @DisplayName("shouldReturnEmptyTaskList")
    @Test
    void add1() {
        assertEquals(0, taskManager.getPrioritizedTasks().size());
    }

    @DisplayName("shouldNotAddTasksWithIntersectedTime")
    @Test
    void add2() {
        Task task1 = TaskFactory.createTaskWithTime("01.10.22 09:00", 60);
        taskManager.createTask(task1);
        Task task2 = TaskFactory.createTaskWithTime("01.10.22 17:00", 120);
        taskManager.createTask(task2);
        Epic epic1 = TaskFactory.createEpicWithTime();
        taskManager.createEpic(epic1);
        SubTask subtask1 = TaskFactory.createSubTaskWithTime(epic1.getId(), "01.10.22 17:00", 120);
        taskManager.createSubTask(subtask1);
        SubTask subtask2 = TaskFactory.createSubTaskWithTime(epic1.getId(), "01.10.22 11:31", 30);
        taskManager.createSubTask(subtask2);

        assertNotEquals(5, taskManager.getPrioritizedTasks().size());
        assertEquals(epic1.getStartTime(), subtask2.getStartTime());
        assertEquals(epic1.getEndTime(), subtask2.getEndTime());

        taskManager.removeTaskById(task2.getId());
        assertFalse(taskManager.getPrioritizedTasks().contains(task2));

        taskManager.createSubTask(subtask1);
        assertEquals(epic1.getStartTime(), subtask2.getStartTime());
        assertEquals(epic1.getEndTime(), subtask1.getEndTime());

        taskManager.removeEpicById(epic1.getId());
        assertFalse(taskManager.getPrioritizedTasks().contains(epic1));
        assertFalse(taskManager.getPrioritizedTasks().contains(subtask1));
        assertFalse(taskManager.getPrioritizedTasks().contains(subtask2));
    }

}