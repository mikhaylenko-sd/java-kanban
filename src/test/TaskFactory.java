package test;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;
import task.TaskType;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

public class TaskFactory {
    static LocalDateTime startTime = LocalDateTime.of(2022, Month.AUGUST, 15, 10, 0);
    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
    static int id;

    public static Task createTask(Status status) {
        return new Task("Переезд", "Собрать вещи", status, TaskType.TASK, generateNewDataAndTime(), 0);
    }

    public static Epic createEpic(Status status) {
        return new Epic("Защитить диплом", "Подготовиться к защите", status);
    }

    public static SubTask createSubTask(int epicId, Status status) {
        return new SubTask(epicId, "Выбрать тему", "Изучить литературу", status, generateNewDataAndTime(), 0);
    }

    public static String generateNewDataAndTime() {
        startTime = startTime.plusMinutes(10);
        return startTime.format(FORMATTER);
    }

    public static Task createTaskWithId() {
        Task task = new Task("task", "description", Status.NEW, TaskType.TASK, generateNewDataAndTime(), 0);
        task.setId(generateId());
        return task;
    }

    public static Epic createEpicWithId() {
        Epic epic = new Epic("epic", "description", Status.NEW);
        epic.setId(generateId());
        return epic;
    }

    public static SubTask createSubTaskWithId(int epicId) {
        SubTask subTask = new SubTask(epicId, "subtask", "description", Status.NEW, generateNewDataAndTime(), 0);
        subTask.setId(generateId());
        return subTask;
    }

    public static int generateId() {
        id = id + 1;
        return id;
    }

    public static Task createTaskWithTime(String startTime, long duration) {
        return new Task("task", "description", Status.NEW, TaskType.TASK, startTime, duration);
    }

    public static Epic createEpicWithTime() {
        return new Epic("epic", "description", Status.NEW);
    }

    public static SubTask createSubTaskWithTime(int epicId, String startTime, long duration) {
        return new SubTask(epicId, "subtask", "description", Status.NEW, startTime, duration);
    }

}
