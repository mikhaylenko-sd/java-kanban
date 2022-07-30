package manager;

import tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public static final int SIZE_OF_MEMORY = 10;
    private final List<Task> historyOfTasks = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (historyOfTasks.size() >= SIZE_OF_MEMORY) {
            historyOfTasks.remove(0);
        }
        historyOfTasks.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyOfTasks;
    }
}
