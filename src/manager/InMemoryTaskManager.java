package manager;

import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryTaskManager implements TaskManager {

    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, SubTask> subTasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private GeneratorId generatorId = new GeneratorId();
    private HistoryManager historyManager = Managers.getDefaultHistory();


    //получить все задачи (задачи, подзадачи, эпики)
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    //удалить все задачи (задачи, подзадачи, эпики)
    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            if (!epic.getSubTaskIds().isEmpty()) {
                epic.getSubTaskIds().clear();
                epic.setStatus(Status.NEW);
            }
        }
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    //получить все задачи по идентификатору (задачи, подзадачи, эпики)
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    //создание задачи, подзадачи, эпика
    @Override
    public void createTask(Task task) {
        int id = generatorId.generate();
        task.setId(id);
        tasks.put(id, task);
    }

    @Override
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

    @Override
    public void createEpic(Epic epic) {
        int id = generatorId.generate();
        epic.setId(id);
        epics.put(id, epic);
    }

    //обновление задачи, подзадачи, эпика
    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
            recalculateEpicStatus(subTask.getEpicId());
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            recalculateEpicStatus(epic.getId());
        }
    }

    @Override
    //удаление по идентификатору задачи, подзадачи, эпика
    public void removeTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            Epic epic = epics.get(subTask.getEpicId());
            epic.getSubTaskIds().remove((Integer) id);
            subTasks.remove(id);
            historyManager.remove(id);
            recalculateEpicStatus(epic.getId());
        }
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        for (int subTaskId : epic.getSubTaskIds()) {
            subTasks.remove(subTaskId);
            historyManager.remove(subTaskId);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public List<SubTask> getSubTasksInTheEpic(Epic epic) {
        List<SubTask> subTasksInTheEpic = new ArrayList<>();
        for (Integer id : epic.getSubTaskIds()) {
            subTasksInTheEpic.add(subTasks.get(id));
        }
        return subTasksInTheEpic;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void recalculateEpicStatus(int epicId) {
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
