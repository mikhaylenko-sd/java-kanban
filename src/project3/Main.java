package project3;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();
        Task task1 = new Task("Переезд", "Собрать вещи", Status.NEW);
        Task task2 = new Task("Выступление", "Подготовить речь", Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic("Защитить диплом", "Подготовиться к защите", Status.NEW);
        manager.createEpic(epic1);

        SubTask subTask1 = new SubTask(epic1.getId(), "Выбрать тему", "Изучить литературу", Status.NEW);
        SubTask subTask2 = new SubTask(epic1.getId(), "Написать диплом", "Провести исследование, оформить главы", Status.NEW);
        SubTask subTask3 = new SubTask(epic1.getId(), "Успешно пройти предзащиту", "Рассказать презентацию", Status.NEW);
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);

        System.out.println(manager.getTaskById(2));
        System.out.println(manager.getSubTaskById(4));
        System.out.println(manager.getEpicById(3));

        manager.removeTaskById(1);
        manager.removeSubTaskById(5);
        //manager.removeEpicById(3);
        System.out.println();
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllSubTasks());
        System.out.println(manager.getAllEpics());

        Task updTsk2 = new Task("Отъезд", "Собрать вещи", Status.NEW);
        updTsk2.setId(2);
        updTsk2.setStatus(Status.DONE);
        manager.updateTask(updTsk2);
        System.out.println(manager.getTaskById(2));

        SubTask updSubTask3 = new SubTask(epic1.getId(), "Защитить ВКР", "Рассказать презентацию", Status.NEW);
        updSubTask3.setId(6);
        updSubTask3.setStatus(Status.DONE);
        manager.updateSubTask(updSubTask3);
        System.out.println(manager.getSubTaskById(6));

        System.out.println(epic1);


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
