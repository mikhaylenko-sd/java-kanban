package manager;

import task.Epic;
import task.SubTask;
import task.Task;

import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    //получить все задачи (задачи, подзадачи, эпики)
    List<Task> getAllTasks();

    List<SubTask> getAllSubTasks();

    List<Epic> getAllEpics();

    //удалить все задачи (задачи, подзадачи, эпики)
    void removeAllTasks();

    void removeAllSubTasks();

    void removeAllEpics();

    //получить все задачи по идентификатору (задачи, подзадачи, эпики)
    Task getTaskById(int id);

    SubTask getSubTaskById(int id);

    Epic getEpicById(int id);

    //создание задачи, подзадачи, эпика
    void createTask(Task task);

    void createSubTask(SubTask subTask);

    void createEpic(Epic epic);

    //обновление задачи, подзадачи, эпика
    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    //удаление по идентификатору задачи, подзадачи, эпика
    void removeTaskById(int id);

    void removeSubTaskById(int id);

    void removeEpicById(int id);

    List<Task> getHistory();

    List<SubTask> getSubTasksInTheEpic(Epic epic);

    TreeSet<Task> getPrioritizedTasks();//////////////////////////////////////////////////////////

}
