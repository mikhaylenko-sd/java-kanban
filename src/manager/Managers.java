package manager;

import http.HTTPTaskManager;

import java.io.File;

public class Managers {

    public static TaskManager getDefault(String backupKey){
        return new HTTPTaskManager(backupKey);
    }
    public static TaskManager getInMemoryTaskManager(){
        return new InMemoryTaskManager();
    }
    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
    public static TaskManager getFileBackedTasksManager(File file){
        return new FileBackedTasksManager(file);
    }

}
