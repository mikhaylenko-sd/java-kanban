package http;

import manager.FileBackedTasksManager;

import java.util.Arrays;
import java.util.List;

public class HTTPTaskManager extends FileBackedTasksManager {
    private final String url;
    private final KVTaskClient kvTaskClient;
    private final String backupKey;

    public HTTPTaskManager(String url, String backupKey) {
        super(null);
        this.url = url;
        kvTaskClient = new KVTaskClient(url);
        this.backupKey = backupKey;
        save();
    }

    @Override
    protected void save() {
        if (backupKey != null) {
            kvTaskClient.save(backupKey, calculateTaskManagerCondition());
        }
    }

    public static HTTPTaskManager loadFromKVServer(String url, String backupKey) {
        KVTaskClient kvTaskClient = new KVTaskClient(url);
        String value = kvTaskClient.load(backupKey);
        List<String> allLines;
        allLines = Arrays.asList(value.split("\n"));
        HTTPTaskManager httpTaskManager = new HTTPTaskManager(url, backupKey);
        httpTaskManager.calculateTaskManagerBackup(allLines);
        httpTaskManager.save();
        return httpTaskManager;
    }

}
