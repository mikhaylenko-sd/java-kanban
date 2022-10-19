package manager;

import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;


public class InMemoryTaskManager implements TaskManager {

    final private Map<Integer, Task> tasks = new HashMap<>();
    final private Map<Integer, SubTask> subTasks = new HashMap<>();
    final private Map<Integer, Epic> epics = new HashMap<>();
    final private GeneratorId generatorId = new GeneratorId();
    final private HistoryManager historyManager = Managers.getDefaultHistory();
    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
    final private TreeSet<Task> sortedTasks = new TreeSet<>();

    protected Map<Integer, Task> getTasks() {
        return tasks;
    }

    protected Map<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    protected Map<Integer, Epic> getEpics() {
        return epics;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public GeneratorId getGeneratorId() {
        return generatorId;
    }

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
                epic.setEpicStartTime(null);
                epic.setEpicEndTime(null);
                epic.setEpicDuration(0);
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
        if (isInvalidTask(task)) {
            return;
        }
        int id = generatorId.generate();
        task.setId(id);
        tasks.put(id, task);
        sortedTasks.add(task);

    }

    @Override
    public void createSubTask(SubTask subTask) {
        if (isInvalidTask(subTask)) {
            return;
        }
        if (epics.containsKey(subTask.getEpicId())) {
            int id = generatorId.generate();
            subTask.setId(id);
            subTasks.put(id, subTask);
            Epic epic = epics.get(subTask.getEpicId());
            epic.addSubTaskId(subTask.getId());
            recalculateEpicStatus(subTask.getEpicId());
            sortedTasks.add(subTask);
            recalculateEpicTime(subTask.getEpicId());
        }
    }

    @Override
    public void createEpic(Epic epic) {
        int id = generatorId.generate();
        epic.setId(id);
        epics.put(id, epic);
        sortedTasks.add(epic);
    }

    //обновление задачи, подзадачи, эпика
    @Override
    public void updateTask(Task task) {
        if (isInvalidTask(task)) {
            return;
        }
        if (tasks.containsKey(task.getId())) {
            sortedTasks.remove(tasks.get(task.getId()));
            tasks.put(task.getId(), task);
            sortedTasks.add(task);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (isInvalidTask(subTask)) {
            return;
        }
        if (subTasks.containsKey(subTask.getId())) {
            sortedTasks.remove(subTasks.get(subTask.getId()));
            subTasks.put(subTask.getId(), subTask);
            recalculateEpicStatus(subTask.getEpicId());
            recalculateEpicTime(subTask.getEpicId());
            sortedTasks.add(subTask);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            sortedTasks.remove(epics.get(epic.getId()));
            epics.put(epic.getId(), epic);
            recalculateEpicStatus(epic.getId());
            recalculateEpicTime(epic.getId());
            sortedTasks.add(epic);
        }
    }

    @Override
    //удаление по идентификатору задачи, подзадачи, эпика
    public void removeTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            sortedTasks.remove(task);
        }
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
            recalculateEpicTime(epic.getId());
            sortedTasks.remove(subTask);
        }
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (int subTaskId : epic.getSubTaskIds()) {
                sortedTasks.remove(subTasks.get(subTaskId));
                subTasks.remove(subTaskId);
                historyManager.remove(subTaskId);
            }
            sortedTasks.remove(epic);
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

    private void recalculateEpicTime(int epicId) {
        Epic epic = epics.get(epicId);
        sortedTasks.remove(epic);
        epic.setEpicStartTime(recalculateEpicStartTime(epicId));
        epic.setEpicEndTime(recalculateEpicEndTime(epicId));
        sortedTasks.add(epic);
        epic.setEpicDuration(recalculateEpicDuration(epicId));
    }

    private LocalDateTime recalculateEpicStartTime(int epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> epicSubtaskIds = epic.getSubTaskIds();
        return epicSubtaskIds.stream()
                .map(subTasks::get)
                .filter(Objects::nonNull)
                .map(Task::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    private long recalculateEpicDuration(int epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> epicSubtaskIds = epic.getSubTaskIds();
        long sum = 0;
        for (Integer epicSubtaskId : epicSubtaskIds) {
            sum = sum + subTasks.get(epicSubtaskId).getDuration();
        }
        return sum;
    }

    private LocalDateTime recalculateEpicEndTime(int epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> epicSubtaskIds = epic.getSubTaskIds();
        return epicSubtaskIds.stream()
                .map(subTasks::get)
                .filter(Objects::nonNull)
                .map(Task::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return sortedTasks;
    }

    private boolean isInvalidTask(Task task) {
        if (task.getClass().equals(Epic.class)) {
            return false;
        }
        Task prev = sortedTasks.lower(task);
        Task next = sortedTasks.higher(task);
        if (prev == null && next == null) {
            return false;
        } else if (prev == null) {
            return !task.getEndTime().isBefore(next.getStartTime());
        } else if (next == null) {
            return !task.getStartTime().isAfter(prev.getEndTime());
        } else {
            return (!task.getStartTime().isAfter(prev.getEndTime()) || !task.getEndTime().isBefore(next.getStartTime()));
        }
    }

}
