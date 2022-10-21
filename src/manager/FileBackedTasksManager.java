package manager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;
import task.TaskType;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    public static void main(String[] args) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(Paths.get("file.csv").toFile());
        Task task1 = new Task("Переезд", "Собрать вещи", Status.NEW, TaskType.TASK, "10.01.22 14:00", 50);
        fileBackedTasksManager.createTask(task1);
        Task task2 = new Task("Выступление", "Подготовить речь", Status.NEW, TaskType.TASK, "10.01.22 12:00", 100);
        fileBackedTasksManager.createTask(task2);
        Epic epic1 = new Epic("Защитить диплом", "Подготовиться к защите", Status.NEW);
        fileBackedTasksManager.createEpic(epic1);
        SubTask subTask1 = new SubTask(epic1.getId(), "Выбрать тему", "Изучить литературу", Status.NEW, "01.01.22 10:00", 30);
        fileBackedTasksManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask(epic1.getId(), "Написать диплом", "Провести исследование; оформить главы", Status.NEW, "01.01.22 11:00", 100);
        fileBackedTasksManager.createSubTask(subTask2);
        SubTask subTask3 = new SubTask(epic1.getId(), "Успешно пройти предзащиту", "Рассказать презентацию", Status.NEW, "01.01.22 09:00", 70);
        fileBackedTasksManager.createSubTask(subTask3);
        Epic epic2 = new Epic("Отпраздновать выпускной", "Организовать праздник", Status.NEW);
        fileBackedTasksManager.createEpic(epic2);

        fileBackedTasksManager.getTaskById(1);
        fileBackedTasksManager.getSubTaskById(6);
        fileBackedTasksManager.getTaskById(2);
        System.out.println(fileBackedTasksManager.getHistory());
        System.out.println(fileBackedTasksManager.getTasks());
        System.out.println(fileBackedTasksManager.getSubTasks());
        System.out.println(fileBackedTasksManager.getEpics());
        System.out.println("--------------------");

        FileBackedTasksManager fileBackedTasksManager1 = loadFromFile(Paths.get("file.csv").toFile());
        System.out.println(fileBackedTasksManager1.getHistory());
        System.out.println(fileBackedTasksManager1.getTasks());
        System.out.println(fileBackedTasksManager1.getSubTasks());
        System.out.println(fileBackedTasksManager1.getEpics());
        Task task3 = new Task("ffffffff", "hhhhhh", Status.NEW, TaskType.TASK, "01.01.22 09:00", 70);
        fileBackedTasksManager1.createTask(task3);
        fileBackedTasksManager1.removeSubTaskById(6);
        Epic epic3 = new Epic("ggggg", "ggggggg", Status.NEW);
        fileBackedTasksManager1.createEpic(epic3);
    }

    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
        save();
    }

    private void save() {
        try (PrintWriter printWriter = new PrintWriter(file.getAbsolutePath())) {
            printWriter.write("id,type,name,status,description,epic, startTime, duration, endTime\n");
            for (Task task : getTasks().values()) {
                printWriter.write(toString(task) + "\n");
            }
            for (Epic epic : getEpics().values()) {
                printWriter.write(toString(epic) + "\n");
            }
            for (SubTask subTask : getSubTasks().values()) {
                printWriter.write(toString(subTask) + "\n");
            }
            printWriter.write("\n");
            printWriter.write(historyToString(getHistoryManager()));
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        List<String> allLines;
        try {
            allLines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        String history = null;
        int maxId = 0;
        for (int i = 1; i < allLines.size(); i++) {
            if (!allLines.get(i).isBlank()) {
                Task task = fromString(allLines.get(i));
                switch (task.getTaskType()) {
                    case TASK:
                        fileBackedTasksManager.getTasks().put(task.getId(), task);
                        break;
                    case EPIC:
                        fileBackedTasksManager.getEpics().put(task.getId(), (Epic) task);
                        break;
                    case SUBTASK:
                        fileBackedTasksManager.getSubTasks().put(task.getId(), (SubTask) task);
                        Epic epic = fileBackedTasksManager.getEpics().get(((SubTask)task).getEpicId());
                        epic.addSubTaskId(task.getId());
                        break;
                }
                if (task.getId() > maxId) {
                    maxId = task.getId();
                }
            } else {
                history = allLines.get(allLines.size() - 1);
                break;
            }
        }
        fileBackedTasksManager.getGeneratorId().setId(maxId);

        if (history != null && !history.isBlank()) {
            List<Integer> historyOfTasks = historyFromString(history);
            for (Integer taskHistory : historyOfTasks) {
                fileBackedTasksManager.getTaskById(taskHistory);
                fileBackedTasksManager.getEpicById(taskHistory);
                fileBackedTasksManager.getSubTaskById(taskHistory);
            }
        }
        return fileBackedTasksManager;
    }

    private String toString(Task task) {
        return task.getId() + "," +
                task.getTaskType() + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() +
                (task.getTaskType() == TaskType.SUBTASK ? "," + ((SubTask) task).getEpicId() : "") + "," +
                startTimeToString(task) + "," +
                task.getDuration() + "," +
                endTimeToString(task);
    }

    private String startTimeToString(Task task) {
        if (task.getStartTime() != null) {
            return task.getStartTime().format(FORMATTER);
        } else {
            return null;
        }
    }

    private String endTimeToString(Task task) {
        if (task.getEndTime() != null) {
            return task.getEndTime().format(FORMATTER);
        } else {
            return null;
        }

    }

    private static Task fromString(String value) {
        String[] taskParameters = value.split(",");
        Task task = null;
        switch (TaskType.valueOf(taskParameters[1])) {
            case TASK:
                task = new Task(taskParameters[2], taskParameters[4], Status.valueOf(taskParameters[3]), TaskType.TASK, taskParameters[5], Long.parseLong(taskParameters[6]));
                break;
            case EPIC:
                task = new Epic(taskParameters[2], taskParameters[4], Status.valueOf(taskParameters[3]));
                if (!taskParameters[5].equals("null") && !taskParameters[7].equals("null")) {
                    ((Epic) task).setEpicStartTime(LocalDateTime.parse(taskParameters[5], FORMATTER));
                    ((Epic) task).setEpicEndTime((LocalDateTime.parse(taskParameters[7], FORMATTER)));
                } else {
                    ((Epic) task).setEpicStartTime(null);
                    ((Epic) task).setEpicEndTime(null);
                }
                ((Epic) task).setEpicDuration(Long.parseLong(taskParameters[6]));
                break;
            case SUBTASK:
                task = new SubTask(Integer.parseInt(taskParameters[5]), taskParameters[2], taskParameters[4], Status.valueOf(taskParameters[3]), taskParameters[6], Long.parseLong(taskParameters[7]));
                break;
        }
        task.setId(Integer.parseInt(taskParameters[0]));
        return task;
    }

    private static String historyToString(HistoryManager manager) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Task task : manager.getHistory()) {
            stringBuilder.append(task.getId()).append(",");
        }
        if (stringBuilder.length() != 0) {
            stringBuilder.setLength(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    private static List<Integer> historyFromString(String value) {
        String[] ids = value.split(",");
        List<Integer> idNumbers = new ArrayList<>();
        for (String id : ids) {
            idNumbers.add(Integer.parseInt(id));
        }
        return idNumbers;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = super.getSubTaskById(id);
        save();
        return subTask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubTaskById(int id) {
        super.removeSubTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }


}

