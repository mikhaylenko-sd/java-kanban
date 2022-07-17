package JavaHW3;

import java.util.HashMap;

public class Manager {

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, SubTask> subTasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    int identificationNumber = 0;

    //получить все задачи (задачи, подзадачи, эпики)
    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    public HashMap<Integer, SubTask> getAllSubTasks() {
        return subTasks;
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    //удалить все задачи (задачи, подзадачи, эпики)
    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubTasks() {
        subTasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    //получить все задачи по идентификатору (задачи, подзадачи, эпики)
    public Task getTaskByIdentificationNumber(int identificationNumber) {
        return tasks.get(identificationNumber);
    }

    public SubTask getSubTaskByIdentificationNumber(int identificationNumber) {
        return subTasks.get(identificationNumber);
    }

    public Epic getEpicByIdentificationNumber(int identificationNumber) {
        return epics.get(identificationNumber);
    }

    //создание задачи, подзадачи, эпика
    public void createTask(Task task) {
        identificationNumber = identificationNumber + 1;
        task.setIdentificationNum(identificationNumber);
        tasks.put(identificationNumber, task);
    }

    public void createSubTask(SubTask subTask) {
        identificationNumber = identificationNumber + 1;
        subTask.setIdentificationNum(identificationNumber);
        subTasks.put(identificationNumber, subTask);
    }

    public void createEpic(Epic epic) {
        identificationNumber = identificationNumber + 1;
        epic.setIdentificationNum(identificationNumber);
        epics.put(identificationNumber, epic);
    }

    //обновление задачи, подзадачи, эпика
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getIdentificationNum())) {
            tasks.put(task.getIdentificationNum(), task);
        }
    }

    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getIdentificationNum())) {
            subTasks.put(subTask.getIdentificationNum(), subTask);
            subTask.getEpic().getSubTasks().put(subTask.getIdentificationNum(), subTask);
            int newCounter = 0;
            int doneCounter = 0;
            for (SubTask sub : subTask.getEpic().getSubTasks().values()) {
                if (sub.getStatus() == Status.NEW) {
                    newCounter++;
                } else if (sub.getStatus() == Status.DONE) {
                    doneCounter++;
                }
            }
            if (newCounter == subTask.getEpic().getSubTasks().size()) {
                subTask.getEpic().setStatus(Status.NEW);
            } else if (doneCounter == subTask.getEpic().getSubTasks().size()) {
                subTask.getEpic().setStatus(Status.DONE);
            } else {
                subTask.getEpic().setStatus(Status.IN_PROGRESS);
            }
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getIdentificationNum())) {
            epics.put(epic.getIdentificationNum(), epic);
        }
    }

    //удаление по идентификатору задачи, подзадачи, эпика
    public void removeTaskByIdentificationNumber(int identificationNumber) {
        tasks.remove(identificationNumber);

    }

    public void removeSubTaskByIdentificationNumber(int identificationNumber) {
        SubTask subTask = subTasks.get(identificationNumber);
        subTask.getEpic().getSubTasks().remove(subTask.getIdentificationNum());
        subTasks.remove(identificationNumber);
    }

    public void removeEpicByIdentificationNumber(int identificationNumber) {
        Epic epic = epics.get(identificationNumber);
        for (Integer subTaskId : epic.getSubTasks().keySet()) {
            subTasks.remove(subTaskId);
        }
        epics.remove(identificationNumber);
    }

    public HashMap<Integer, SubTask> getSubTasksInTheEpic(Epic epic) {
        return epic.getSubTasks();
    }

}
