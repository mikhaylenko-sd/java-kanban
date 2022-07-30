import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Переезд", "Собрать вещи", Status.NEW);
        Task task2 = new Task("Выступление", "Подготовить речь", Status.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Защитить диплом", "Подготовиться к защите", Status.NEW);
        taskManager.createEpic(epic1);

        SubTask subTask1 = new SubTask(epic1.getId(), "Выбрать тему", "Изучить литературу", Status.NEW);
        SubTask subTask2 = new SubTask(epic1.getId(), "Написать диплом", "Провести исследование, оформить главы", Status.NEW);
        SubTask subTask3 = new SubTask(epic1.getId(), "Успешно пройти предзащиту", "Рассказать презентацию", Status.NEW);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.getTaskById(2);
        taskManager.getSubTaskById(4);
        taskManager.getEpicById(3);
        System.out.println("____________________________________");
        System.out.println(taskManager.getHistory());

        taskManager.removeTaskById(1);
        taskManager.removeSubTaskById(5);
        //taskManager.removeEpicById(3);
        System.out.println();
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubTasks());
        System.out.println(taskManager.getAllEpics());

        Task updTsk2 = new Task("Отъезд", "Собрать вещи", Status.NEW);
        updTsk2.setId(2);
        updTsk2.setStatus(Status.DONE);
        taskManager.updateTask(updTsk2);
        taskManager.getTaskById(2);

        SubTask updSubTask3 = new SubTask(epic1.getId(), "Защитить ВКР", "Рассказать презентацию", Status.NEW);
        updSubTask3.setId(6);
        updSubTask3.setStatus(Status.DONE);
        taskManager.updateSubTask(updSubTask3);
        taskManager.getSubTaskById(6);
        System.out.println(epic1);

        System.out.println("____________________________________");
        System.out.println(taskManager.getHistory());
        Task task3 = new Task("Попасть в историю", "Новый элемент", Status.NEW);
        taskManager.createTask(task3);

        taskManager.getTaskById(1);
        taskManager.getSubTaskById(1);
        taskManager.getEpicById(3);
        taskManager.getSubTaskById(2);
        taskManager.getSubTaskById(3);
        taskManager.getTaskById(7);

        System.out.println("____________________________________");
        System.out.println(taskManager.getHistory());

    }

    public static void printAllTasks(HashMap<Integer, Task> allTasks) {
        if (allTasks.size() != 0) {
            for (Task task : allTasks.values()) {
                System.out.println(task);
            }
        } else {
            System.out.println("В трекере нет ни одной задачи");
        }
    }

    public static void printAllSubTasks(HashMap<Integer, SubTask> allTasks) {
        if (allTasks.size() != 0) {
            for (Task task : allTasks.values()) {
                System.out.println(task);
            }
        } else {
            System.out.println("В трекере нет ни одной задачи");
        }
    }

    public static void printAllEpics(HashMap<Integer, Epic> allTasks) {
        if (allTasks.size() != 0) {
            for (Task task : allTasks.values()) {
                System.out.println(task);
            }
        } else {
            System.out.println("В трекере нет ни одной задачи");
        }
    }
}
