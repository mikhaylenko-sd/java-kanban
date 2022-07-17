package JavaHW3;

public class Task {
    private String name;
    private String description;
    private int identificationNum;
    private Status status;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setIdentificationNum(int identificationNum) {
        this.identificationNum = identificationNum;
    }

    public int getIdentificationNum() {
        return identificationNum;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", identificationNum=" + identificationNum +
                ", status='" + status + '\'' +
                '}';
    }
}