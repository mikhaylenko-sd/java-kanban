package task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private List<Integer> subTaskIds = new ArrayList<>();
    private LocalDateTime epicStartTime;
    private long epicDuration;
    private LocalDateTime epicEndTime;

    public Epic(String name, String description, Status status) {
        super(name, description, status, TaskType.EPIC);
    }

    public void addSubTaskId(int subTaskId) {
        subTaskIds.add(subTaskId);
    }

    public List<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTaskIds, epic.subTaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskIds);
    }

    public void setEpicDuration(long epicDuration) {
        this.epicDuration = epicDuration;
    }

    public void setEpicStartTime(LocalDateTime epicStartTime) {
        this.epicStartTime = epicStartTime;
    }

    public void setEpicEndTime(LocalDateTime epicEndTime) {
        this.epicEndTime = epicEndTime;
    }

    @Override
    public long getDuration() {
        return epicDuration;
    }

    @Override
    public LocalDateTime getStartTime() {
        return epicStartTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return epicEndTime;
    }
}
