import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.TaskType;
import task.SubTask;
import task.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getInMemoryTaskManager();
        Task task1 = new Task("Переезд", "Собрать вещи", Status.NEW, TaskType.TASK, "10.01.22 14:00", 50);
        Task task2 = new Task("Выступление", "Подготовить речь", Status.NEW, TaskType.TASK, "10.01.22 12:00", 100);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        System.out.println(taskManager.getPrioritizedTasks());

        Epic epic1 = new Epic("Защитить диплом", "Подготовиться к защите", Status.NEW);
        taskManager.createEpic(epic1);
        System.out.println(taskManager.getPrioritizedTasks());

        SubTask subTask1 = new SubTask(epic1.getId(), "Выбрать тему", "Изучить литературу", Status.NEW, "01.01.22 10:00", 30);
        SubTask subTask2 = new SubTask(epic1.getId(), "Написать диплом", "Провести исследование, оформить главы", Status.NEW, "01.01.22 10:31", 100);
        SubTask subTask3 = new SubTask(epic1.getId(), "Успешно пройти предзащиту", "Рассказать презентацию", Status.NEW, "01.01.22 09:00", 70);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);
        System.out.println(taskManager.getPrioritizedTasks());

        Epic epic2 = new Epic("Отпраздновать выпускной", "Организовать праздник", Status.NEW);
        taskManager.createEpic(epic2);

        System.out.println(taskManager.getPrioritizedTasks());
        System.out.println("____________________________________");

        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getEpicById(3);
        taskManager.getSubTaskById(4);
        taskManager.getSubTaskById(5);
        taskManager.getSubTaskById(4);
        taskManager.getEpicById(7);
        System.out.println(taskManager.getHistory());
        System.out.println("____________________________________");

        taskManager.getTaskById(2);
        taskManager.getEpicById(7);
        System.out.println(taskManager.getHistory());
        System.out.println("____________________________________");

        taskManager.removeTaskById(2);
        System.out.println(taskManager.getHistory());
        System.out.println("____________________________________");
        taskManager.removeEpicById(3);
        System.out.println(taskManager.getHistory());
        System.out.println("____________________________________");

        taskManager.getEpicById(3);

        /*taskManager.removeTaskById(1);
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
*/
    }
}
