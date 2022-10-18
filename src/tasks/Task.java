package tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task implements Comparable<Task> {
    private String name;
    private String description;
    private int id;
    private Status status;
    private TaskType taskType;
    private long duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public Task(String name, String description, Status status, TaskType taskType) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.taskType = taskType;
    }

    public Task(String name, String description, Status status, TaskType taskType, String startTime, long duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.taskType = taskType;
        this.startTime = LocalDateTime.parse(startTime, FORMATTER);
        this.duration = duration;
        this.endTime = getEndTime(this.startTime, duration);
    }

    public LocalDateTime getEndTime(LocalDateTime startTime, long duration) {
        return startTime.plusMinutes(duration);
    }

    @Override
    public int compareTo(Task task) {
        if (getStartTime() != null && task.getStartTime() != null) {
            if (isEpicForTask(this, task)) {
                return -1;
            }
            if (isEpicForTask(task, this)) {
                return 1;
            }
            return getStartTime().compareTo(task.getStartTime());

        } else if (getStartTime() == null && task.getStartTime() == null) {
            return task.getId() - getId();
        } else if (task.getStartTime() == null) {
            return -1;
        } else {
            return 1;
        }
    }

    private boolean isEpicForTask(Task task1, Task task2) {
        return task2.getClass().equals(SubTask.class) && ((SubTask) task2).getEpicId() == task1.getId();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public long getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && duration == task.duration && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status && taskType == task.taskType && Objects.equals(startTime, task.startTime) && Objects.equals(endTime, task.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status, taskType, duration, startTime, endTime);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", taskType=" + taskType +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                '}' + '\n';
    }

}