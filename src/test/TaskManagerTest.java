package test;

import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;
import task.TaskType;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @DisplayName("shouldCreateTaskOrThrowExceptionWhenCreateNullTask")
    @Test
    void createTask() {
        Task task = TaskFactory.createTask(Status.NEW);
        taskManager.createTask(task);
        assertTrue(taskManager.getAllTasks().contains(task));
        assertEquals(1, task.getId());

        assertThrows(NullPointerException.class, () -> taskManager.createTask(null));
    }

    @DisplayName("shouldCreateSubtaskOrThrowExceptionWhenCreateNullSubtask")
    @Test
    void createSubTask() {
        Epic epic = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic);
        SubTask subTask = TaskFactory.createSubTask(epic.getId(), Status.NEW);
        taskManager.createSubTask(subTask);
        assertTrue(taskManager.getAllSubTasks().contains(subTask));
        assertEquals(2, subTask.getId());

        assertThrows(NullPointerException.class, () -> taskManager.createSubTask(null));
    }

    @Test
    void shouldNotCreateSubtaskWithNotExistEpic() {
        SubTask subTask = TaskFactory.createSubTask(5, Status.NEW);
        taskManager.createSubTask(subTask);
        Assertions.assertFalse(taskManager.getAllSubTasks().contains(subTask));
    }

    @DisplayName("shouldCreateEpicOrThrowExceptionWhenCreateNullEpic")
    @Test
    void createEpic() {
        Epic epic = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic);
        assertTrue(taskManager.getAllEpics().contains(epic));
        assertEquals(1, epic.getId());

        assertThrows(NullPointerException.class, () -> taskManager.createEpic(null));
    }

    @DisplayName("shouldUpdateTaskOrThrowExceptionWhenUpdateNullTask")
    @Test
    void updateTask() {
        assertThrows(NullPointerException.class, () -> taskManager.updateTask(null));

        Task task1 = new Task("name1", "description1", Status.NEW, TaskType.TASK, "21.01.22 12:06", 20);
        taskManager.createTask(task1);
        int id = task1.getId();
        Task task2 = new Task("name2", "description2", Status.DONE, TaskType.TASK, "22.02.22 17:12", 40);
        task2.setId(id);
        taskManager.updateTask(task2);

        Task task = taskManager.getTaskById(id);
        assertNotEquals(task1, task);
        assertEquals(task2, task);
        assertNotEquals(task1.getName(), task.getName());
        assertNotEquals(task1.getDescription(), task.getDescription());
        assertNotEquals(task1.getStatus(), task.getStatus());
        assertNotEquals(task1.getStartTime(), task.getStartTime());
        assertNotEquals(task1.getDuration(), task.getDuration());
    }

    @DisplayName("shouldUpdateSubtaskOrThrowExceptionWhenUpdateNullSubtask")
    @Test
    void updateSubTask() {
        assertThrows(NullPointerException.class, () -> taskManager.updateSubTask(null));

        Epic epic = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic);
        SubTask subTask1 = new SubTask(epic.getId(), "name1", "description1", Status.NEW, "21.01.22 12:06", 20);
        taskManager.createSubTask(subTask1);
        int id = subTask1.getId();
        SubTask subTask2 = new SubTask(epic.getId(), "name2", "description2", Status.DONE, "23.02.22 17:12", 50);
        subTask2.setId(id);
        taskManager.updateSubTask(subTask2);

        SubTask subTask = taskManager.getSubTaskById(id);
        assertNotEquals(subTask1, subTask);
        assertEquals(subTask2, subTask);
        assertNotEquals(subTask1.getName(), subTask.getName());
        assertNotEquals(subTask1.getDescription(), subTask.getDescription());
        assertNotEquals(subTask1.getStatus(), subTask.getStatus());
        assertNotEquals(subTask1.getStartTime(), subTask.getStartTime());
        assertNotEquals(subTask1.getDuration(), subTask.getDuration());
    }

    @DisplayName("shouldUpdateEpicOrThrowExceptionWhenUpdateNullEpic")
    @Test
    void updateEpic() {
        assertThrows(NullPointerException.class, () -> taskManager.updateEpic(null));

        Epic epic1 = new Epic("name1", "description1", Status.NEW);
        taskManager.createEpic(epic1);
        int id = epic1.getId();
        Epic epic2 = new Epic("name2", "description2", Status.NEW);
        epic2.setId(id);
        taskManager.updateEpic(epic2);

        Epic epic = taskManager.getEpicById(id);
        assertNotEquals(epic1, epic);
        assertEquals(epic2, epic);
        assertNotEquals(epic1.getName(), epic.getName());
        assertNotEquals(epic1.getDescription(), epic.getDescription());
    }

    @DisplayName("shouldRemoveAllTasks")
    @Test
    void removeAllTasks() {
        Task task1 = TaskFactory.createTask(Status.NEW);
        taskManager.createTask(task1);
        Task task2 = TaskFactory.createTask(Status.DONE);
        taskManager.createTask(task2);
        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic1);
        Task task3 = TaskFactory.createTask(Status.IN_PROGRESS);
        taskManager.createTask(task3);

        taskManager.removeAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @DisplayName("shouldRemoveAllSubtasks")
    @Test
    void removeAllSubTasks() {
        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic1);
        Epic epic2 = TaskFactory.createEpic(Status.DONE);
        taskManager.createEpic(epic2);
        Task task1 = TaskFactory.createTask(Status.IN_PROGRESS);
        taskManager.createTask(task1);
        SubTask subTask1 = TaskFactory.createSubTask(epic1.getId(), Status.DONE);
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = TaskFactory.createSubTask(epic1.getId(), Status.NEW);
        taskManager.createSubTask(subTask2);
        SubTask subTask3 = TaskFactory.createSubTask(epic2.getId(), Status.IN_PROGRESS);
        taskManager.createSubTask(subTask3);

        taskManager.removeAllSubTasks();
        assertTrue(taskManager.getAllSubTasks().isEmpty());
        assertTrue(taskManager.getEpicById(epic1.getId()).getSubTaskIds().isEmpty());
        assertTrue(taskManager.getEpicById(epic2.getId()).getSubTaskIds().isEmpty());

    }

    @DisplayName("shouldRemoveAllEpics")
    @Test
    void removeAllEpics() {
        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic1);
        Epic epic2 = TaskFactory.createEpic(Status.DONE);
        taskManager.createEpic(epic2);
        Task task1 = TaskFactory.createTask(Status.IN_PROGRESS);
        taskManager.createTask(task1);
        Epic epic3 = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic3);
        SubTask subTask1 = TaskFactory.createSubTask(epic3.getId(), Status.DONE);
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = TaskFactory.createSubTask(epic3.getId(), Status.NEW);
        taskManager.createSubTask(subTask2);

        taskManager.removeAllEpics();
        assertTrue(taskManager.getAllEpics().isEmpty());
        assertTrue(taskManager.getAllSubTasks().isEmpty());
    }

    @DisplayName("shouldGetAllTasks")
    @Test
    void getAllTasks() {
        assertTrue(taskManager.getAllTasks().isEmpty());
        Task task1 = TaskFactory.createTask(Status.NEW);
        taskManager.createTask(task1);
        Task task2 = TaskFactory.createTask(Status.DONE);
        taskManager.createTask(task2);
        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic1);
        Task task3 = TaskFactory.createTask(Status.IN_PROGRESS);
        taskManager.createTask(task3);

        assertEquals(3, taskManager.getAllTasks().size());
        assertArrayEquals(new Task[]{task1, task2, task3}, taskManager.getAllTasks().toArray());

    }

    @DisplayName("shouldGetAllSubtasks")
    @Test
    void getAllSubTasks() {
        assertTrue(taskManager.getAllSubTasks().isEmpty());

        SubTask subTask1 = TaskFactory.createSubTask(0, Status.DONE);
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = TaskFactory.createSubTask(4, Status.NEW);
        taskManager.createSubTask(subTask2);
        SubTask subTask3 = TaskFactory.createSubTask(6, Status.IN_PROGRESS);
        taskManager.createSubTask(subTask3);
        assertTrue(taskManager.getAllSubTasks().isEmpty());

        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic1);
        SubTask subTask4 = TaskFactory.createSubTask(epic1.getId(), Status.DONE);
        taskManager.createSubTask(subTask4);
        SubTask subTask5 = TaskFactory.createSubTask(epic1.getId(), Status.NEW);
        taskManager.createSubTask(subTask5);
        assertEquals(2, taskManager.getAllSubTasks().size());
        assertArrayEquals(new SubTask[]{subTask4, subTask5}, taskManager.getAllSubTasks().toArray());

    }

    @DisplayName("shouldGetAllEpics")
    @Test
    void getAllEpics() {
        assertTrue(taskManager.getAllEpics().isEmpty());

        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic1);
        Epic epic2 = TaskFactory.createEpic(Status.DONE);
        taskManager.createEpic(epic2);
        Task task1 = TaskFactory.createTask(Status.IN_PROGRESS);
        taskManager.createTask(task1);
        SubTask subTask1 = TaskFactory.createSubTask(epic1.getId(), Status.DONE);
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = TaskFactory.createSubTask(epic1.getId(), Status.NEW);
        taskManager.createSubTask(subTask2);
        SubTask subTask3 = TaskFactory.createSubTask(epic2.getId(), Status.IN_PROGRESS);
        taskManager.createSubTask(subTask3);
        assertEquals(2, taskManager.getAllEpics().size());
        assertArrayEquals(new Epic[]{epic1, epic2}, taskManager.getAllEpics().toArray());
    }

    @DisplayName("shouldGetTaskById")
    @Test
    void getTaskById() {
        assertNull(taskManager.getTaskById(3));

        Task task1 = TaskFactory.createTask(Status.NEW);
        taskManager.createTask(task1);
        Task task2 = TaskFactory.createTask(Status.DONE);
        taskManager.createTask(task2);
        Task task3 = TaskFactory.createTask(Status.IN_PROGRESS);
        taskManager.createTask(task3);
        assertEquals(task1, taskManager.getTaskById(task1.getId()));
        assertEquals(task2, taskManager.getTaskById(task2.getId()));
        assertEquals(task3, taskManager.getTaskById(task3.getId()));
    }

    @DisplayName("shouldGetSubtaskById")
    @Test
    void getSubTaskById() {
        assertNull(taskManager.getSubTaskById(3));
        SubTask subTask0 = TaskFactory.createSubTask(5, Status.NEW);
        taskManager.createSubTask(subTask0);
        assertNull(taskManager.getSubTaskById(subTask0.getId()));

        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic1);
        SubTask subTask1 = TaskFactory.createSubTask(epic1.getId(), Status.DONE);
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = TaskFactory.createSubTask(epic1.getId(), Status.NEW);
        taskManager.createSubTask(subTask2);
        SubTask subTask3 = TaskFactory.createSubTask(epic1.getId(), Status.IN_PROGRESS);
        taskManager.createSubTask(subTask3);
        assertEquals(subTask1, taskManager.getSubTaskById(subTask1.getId()));
        assertEquals(subTask2, taskManager.getSubTaskById(subTask2.getId()));
        assertEquals(subTask3, taskManager.getSubTaskById(subTask3.getId()));
    }

    @DisplayName("shouldGetEpicById")
    @Test
    void getEpicById() {
        SubTask subTask0 = TaskFactory.createSubTask(5, Status.NEW);
        taskManager.createSubTask(subTask0);
        assertNull(taskManager.getEpicById(subTask0.getEpicId()));

        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic1);
        Epic epic2 = TaskFactory.createEpic(Status.DONE);
        taskManager.createEpic(epic2);
        SubTask subTask1 = TaskFactory.createSubTask(epic1.getId(), Status.DONE);
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = TaskFactory.createSubTask(epic2.getId(), Status.NEW);
        taskManager.createSubTask(subTask2);
        assertEquals(epic1, taskManager.getEpicById(epic1.getId()));
        assertEquals(epic2, taskManager.getEpicById(epic2.getId()));
        assertEquals(epic1, taskManager.getEpicById(subTask1.getEpicId()));
        assertEquals(epic2, taskManager.getEpicById(subTask2.getEpicId()));
    }

    @DisplayName("shouldRemoveTaskById")
    @Test
    void removeTaskById() {
        int before = taskManager.getAllTasks().size();
        taskManager.removeTaskById(8);
        assertEquals(before, taskManager.getAllTasks().size());

        Task task1 = TaskFactory.createTask(Status.NEW);
        taskManager.createTask(task1);
        Task task2 = TaskFactory.createTask(Status.DONE);
        taskManager.createTask(task2);
        Task task3 = TaskFactory.createTask(Status.IN_PROGRESS);
        taskManager.createTask(task3);
        before = taskManager.getAllTasks().size();

        taskManager.removeTaskById(8);
        assertEquals(before, taskManager.getAllTasks().size());

        taskManager.removeTaskById(task2.getId());
        assertNotEquals(before, taskManager.getAllTasks().size());
    }

    @DisplayName("shouldRemoveSubTaskById")
    @Test
    void removeSubTaskById() {
        int before = taskManager.getAllSubTasks().size();
        taskManager.removeSubTaskById(8);
        assertEquals(before, taskManager.getAllSubTasks().size());

        SubTask subTask1 = TaskFactory.createSubTask(0, Status.DONE);
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = TaskFactory.createSubTask(4, Status.NEW);
        taskManager.createSubTask(subTask2);
        SubTask subTask3 = TaskFactory.createSubTask(6, Status.IN_PROGRESS);
        taskManager.createSubTask(subTask3);
        before = taskManager.getAllSubTasks().size();

        taskManager.removeSubTaskById(subTask2.getId());
        assertEquals(before, taskManager.getAllSubTasks().size());

        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic1);
        SubTask subTask4 = TaskFactory.createSubTask(epic1.getId(), Status.DONE);
        taskManager.createSubTask(subTask4);
        SubTask subTask5 = TaskFactory.createSubTask(epic1.getId(), Status.NEW);
        taskManager.createSubTask(subTask5);
        before = taskManager.getAllSubTasks().size();

        taskManager.removeSubTaskById(subTask4.getId());
        assertNotEquals(before, taskManager.getAllSubTasks().size());
    }

    @DisplayName("shouldRemoveEpicById")
    @Test
    void removeEpicById() {
        int before = taskManager.getAllEpics().size();
        taskManager.removeEpicById(8);
        assertEquals(before, taskManager.getAllTasks().size());

        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic1);
        Epic epic2 = TaskFactory.createEpic(Status.DONE);
        taskManager.createEpic(epic2);
        SubTask subTask1 = TaskFactory.createSubTask(epic1.getId(), Status.DONE);
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = TaskFactory.createSubTask(epic1.getId(), Status.NEW);
        taskManager.createSubTask(subTask2);
        before = taskManager.getAllEpics().size();

        taskManager.removeEpicById(10);
        assertEquals(before, taskManager.getAllEpics().size());

        taskManager.removeEpicById(epic1.getId());
        assertNotEquals(before, taskManager.getAllEpics().size());
    }

    @DisplayName("shouldReturnHistoryOfActions")
    @Test
    void getHistory() {
        assertTrue(taskManager.getHistory().isEmpty());
        Task task1 = TaskFactory.createTask(Status.NEW);
        Task task2 = TaskFactory.createTask(Status.DONE);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic1);
        SubTask subTask1 = TaskFactory.createSubTask(epic1.getId(), Status.DONE);
        SubTask subTask2 = TaskFactory.createSubTask(epic1.getId(), Status.NEW);
        SubTask subTask3 = TaskFactory.createSubTask(epic1.getId(), Status.NEW);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);
        Epic epic2 = TaskFactory.createEpic(Status.DONE);
        taskManager.createEpic(epic2);

        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        assertEquals(1, taskManager.getHistory().size());
        taskManager.getEpicById(3);
        taskManager.getSubTaskById(4);
        taskManager.getSubTaskById(5);
        taskManager.getSubTaskById(4);
        taskManager.getEpicById(7);
        assertEquals(5, taskManager.getHistory().size());

        taskManager.removeTaskById(1);
        taskManager.removeEpicById(7);
        assertEquals(3, taskManager.getHistory().size());
    }

    @DisplayName("shouldReturnListOfSubTasksInTheEpic")
    @Test
    void getSubTasksInTheEpic() {
        Epic epic1 = TaskFactory.createEpic(Status.NEW);
        taskManager.createEpic(epic1);
        SubTask subTask1 = TaskFactory.createSubTask(epic1.getId(), Status.DONE);
        SubTask subTask2 = TaskFactory.createSubTask(epic1.getId(), Status.NEW);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        assertEquals(2, taskManager.getSubTasksInTheEpic(epic1.getId()).size());

        Epic epic2 = TaskFactory.createEpic(Status.DONE);
        taskManager.createEpic(epic2);
        assertEquals(0, taskManager.getSubTasksInTheEpic(epic2.getId()).size());
    }

}
