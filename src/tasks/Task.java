package tasks;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private Status status;
    private TaskType taskType;

    public Task(String name, String description, Status status, TaskType taskType) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.taskType = taskType;
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
    public String toString() {
        return "{id = " + id +
                ", type = " + taskType +
                ", name = " + name +
                ", status = " + status +
                ", description = " + description + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }
}