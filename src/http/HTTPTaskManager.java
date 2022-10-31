package http;

import manager.FileBackedTasksManager;

import java.util.Arrays;
import java.util.List;

public class HTTPTaskManager extends FileBackedTasksManager {
    private static final String URL = "http://localhost:8078";
    private static final KVTaskClient KV_TASK_CLIENT = new KVTaskClient(URL);
    private final String backupKey;

    public HTTPTaskManager(String backupKey) {
        super(null);
        this.backupKey = backupKey;
        save();
    }

    @Override
    protected void save() {
        if (backupKey != null) {
            KV_TASK_CLIENT.save(backupKey, calculateTaskManagerCondition());
        }
    }

    public static HTTPTaskManager loadFromKVServer(String key) {
        String value = KV_TASK_CLIENT.load(key);
        List<String> allLines;
        allLines = Arrays.asList(value.split("\n"));
        HTTPTaskManager httpTaskManager = new HTTPTaskManager(key);
        httpTaskManager.calculateTaskManagerBackup(allLines);
        httpTaskManager.save();
        return httpTaskManager;
    }

}
