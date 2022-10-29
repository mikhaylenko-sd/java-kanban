package manager;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;

import java.io.File;
import java.util.Arrays;

public class Managers {

    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }
    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
    public static TaskManager getFileBackedTasksManager(File file){
        return new FileBackedTasksManager(file);
    }

}
