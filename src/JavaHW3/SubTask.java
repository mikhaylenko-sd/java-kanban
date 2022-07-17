package JavaHW3;

public class SubTask extends Task {
    private Epic epic;

    public SubTask(Epic epic, String name, String description, Status status) {
        super(name, description, status);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }
}
