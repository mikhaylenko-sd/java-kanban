package JavaHW3;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();
        Task task1 = new Task("Переезд", "Собрать вещи", Status.NEW);
        Task task2 = new Task("Выступление", "Подготовить речь", Status.NEW);
        Epic epic1 = new Epic("Защитить диплом", "Подготовиться к защите", Status.NEW);
        SubTask subTask1 = new SubTask(epic1, "Выбрать тему", "Изучить литературу", Status.NEW);
        SubTask subTask2 = new SubTask(epic1, "Написать диплом", "Провести исследование, оформить главы", Status.NEW);
        SubTask subTask3 = new SubTask(epic1, "Успешно пройти предзащиту", "Рассказать презентацию", Status.NEW);

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);
        epic1.addSubTask(subTask1);
        epic1.addSubTask(subTask2);
        epic1.addSubTask(subTask3);

        System.out.println(manager.getTaskByIdentificationNumber(2));
        System.out.println(manager.getSubTaskByIdentificationNumber(4));
        System.out.println(manager.getEpicByIdentificationNumber(3));

        manager.removeTaskByIdentificationNumber(1);
        manager.removeSubTaskByIdentificationNumber(5);
        manager.removeEpicByIdentificationNumber(3);
        System.out.println();
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllSubTasks());
        System.out.println(manager.getAllEpics());

        Task updTsk2 = new Task("Отъезд", "Собрать вещи", Status.NEW);
        updTsk2.setIdentificationNum(2);
        updTsk2.setStatus(Status.DONE);
        manager.updateTask(updTsk2);

        SubTask updSubTask3 = new SubTask(epic1, "Защитить ВКР", "Рассказать презентацию", Status.NEW);
        updSubTask3.setIdentificationNum(6);
        updSubTask3.setStatus(Status.DONE);
        manager.updateSubTask(updSubTask3);

        System.out.println(epic1.getStatus());


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
