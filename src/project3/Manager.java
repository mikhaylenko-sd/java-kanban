package project3;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.*;

public class Manager {

    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, SubTask> subTasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private GeneratorId generatorId = new GeneratorId();

    //получить все задачи (задачи, подзадачи, эпики)
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    //удалить все задачи (задачи, подзадачи, эпики)
    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            if (!epic.getSubTaskIds().isEmpty()) {
                epic.getSubTaskIds().clear();
                epic.setStatus(Status.DONE);
            }
        }
    }

    public void removeAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    //получить все задачи по идентификатору (задачи, подзадачи, эпики)
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    //создание задачи, подзадачи, эпика
    public void createTask(Task task) {
        int id = generatorId.generate();
        task.setId(id);
        tasks.put(id, task);
    }

    public void createSubTask(SubTask subTask) {
        if (epics.containsKey(subTask.getEpicId())) {
            int id = generatorId.generate();
            subTask.setId(id);
            subTasks.put(id, subTask);
            Epic epic = epics.get(subTask.getEpicId());
            epic.addSubTaskId(subTask.getId());
            recalculateEpicStatus(subTask.getEpicId());
        }
    }

    public void createEpic(Epic epic) {
        int id = generatorId.generate();
        epic.setId(id);
        epics.put(id, epic);
    }

    //обновление задачи, подзадачи, эпика
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
            recalculateEpicStatus(subTask.getEpicId());
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            recalculateEpicStatus(epic.getId());
        }
    }

    //удаление по идентификатору задачи, подзадачи, эпика
    public void removeTaskById(int id) {
        tasks.remove(id);

    }

    public void removeSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            Epic epic = epics.get(subTask.getEpicId());
            epic.getSubTaskIds().remove((Integer) id);
            subTasks.remove(id);
            recalculateEpicStatus(epic.getId());
        }
    }

    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        for (int subTaskId : epic.getSubTaskIds()) {
            subTasks.remove(subTaskId);
        }
        epics.remove(id);
    }

    public List<SubTask> getSubTasksInTheEpic(Epic epic) {
        List<SubTask> subTasksInTheEpic = new ArrayList<>();
        for (Integer id : epic.getSubTaskIds()) {
            subTasksInTheEpic.add(subTasks.get(id));
        }
        return subTasksInTheEpic;
    }

    public void recalculateEpicStatus(int epicId) {
        int newCounter = 0;
        int doneCounter = 0;
        Epic epic = epics.get(epicId);
        for (int subTaskId : epic.getSubTaskIds()) {
            SubTask sub = subTasks.get(subTaskId);
            if (sub.getStatus() == Status.NEW) {
                newCounter++;
            } else if (sub.getStatus() == Status.DONE) {
                doneCounter++;
            }
        }
        if (newCounter == epic.getSubTaskIds().size()) {
            epic.setStatus(Status.NEW);
        } else if (doneCounter == epic.getSubTaskIds().size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
