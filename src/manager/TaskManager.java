package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

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

    List<SubTask> getSubTasksInTheEpic(Epic epic);

    //просмотр задач (получение списка 10 последних просмотренных задач)
    List<Task> getHistory();


}