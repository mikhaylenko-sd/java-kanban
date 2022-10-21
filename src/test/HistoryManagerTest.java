package test;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @DisplayName("shouldAddTasks")
    @Test
    void add() {
        assertTrue(historyManager.getHistory().isEmpty());

        Task task1 = TaskFactory.createTaskWithId();
        Task task2 = TaskFactory.createTaskWithId();
        Epic epic1 = TaskFactory.createEpicWithId();
        SubTask subtask1 = TaskFactory.createSubTaskWithId(epic1.getId());
        Task task3 = TaskFactory.createTaskWithId();
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic1);
        historyManager.add(subtask1);
        historyManager.add(task3);
        int before = historyManager.getHistory().size();
        assertEquals(5,before);
        assertTrue(historyManager.getHistory().contains(task1));
        assertTrue(historyManager.getHistory().contains(task2));
        assertTrue(historyManager.getHistory().contains(epic1));
        assertTrue(historyManager.getHistory().contains(subtask1));
        assertTrue(historyManager.getHistory().contains(task3));

        historyManager.add(epic1);
        assertEquals(before,historyManager.getHistory().size());
        assertEquals(historyManager.getHistory().size()-1,historyManager.getHistory().indexOf(epic1));
    }

    @DisplayName("shouldRemoveTasks")
    @Test
    void remove() {
        assertTrue(historyManager.getHistory().isEmpty());

        Task task1 = TaskFactory.createTaskWithId();
        Task task2 = TaskFactory.createTaskWithId();
        Epic epic1 = TaskFactory.createEpicWithId();
        SubTask subtask1 = TaskFactory.createSubTaskWithId(epic1.getId());
        Task task3 = TaskFactory.createTaskWithId();
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic1);
        historyManager.add(subtask1);
        historyManager.add(task3);
        assertNotEquals(6,historyManager.getHistory().size());

        historyManager.remove(task1.getId());
        assertEquals(4,historyManager.getHistory().size());
        assertFalse(historyManager.getHistory().contains(task1));

        historyManager.remove(subtask1.getId());
        assertEquals(3,historyManager.getHistory().size());
        assertFalse(historyManager.getHistory().contains(subtask1));

        historyManager.remove(task3.getId());
        assertEquals(2,historyManager.getHistory().size());
        assertFalse(historyManager.getHistory().contains(task3));
    }

}