package JavaHW3;

import java.util.HashMap;

public class Epic extends Task {

    private HashMap<Integer, SubTask> subTasks = new HashMap<>();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public void addSubTask(SubTask subTask){
        subTasks.put(subTask.getIdentificationNum(),subTask);
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }
}
