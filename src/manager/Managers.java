package manager;

import http.HTTPTaskManager;

import java.io.File;

public class Managers {

    public static HTTPTaskManager getDefault(String backupKey) {
        return new HTTPTaskManager("http://localhost:8078", backupKey);
    }

    public static TaskManager getInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedTasksManager(File file) {
        return new FileBackedTasksManager(file);
    }

}
