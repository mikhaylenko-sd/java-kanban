package manager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyHashMap = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getId());
        Node node = new Node(task);
        linkLast(node);
        historyHashMap.put(task.getId(), node);
    }

    @Override
    public void remove(int id) {
        Node node = historyHashMap.get(id);
        if (node != null) {
            removeNode(node);
            historyHashMap.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyTasks = new ArrayList<>();
        for (Node x = first; x != null; x = x.next) {
            historyTasks.add(x.data);
        }
        return historyTasks;
    }

    private void linkLast(Node node) {
        if (!historyHashMap.isEmpty()) {
            Node x = last;
            last = node;
            last.prev = x;
            x.next = last;
        } else {
            first = node;
            last = node;
        }
    }

    private void removeNode(Node node) {
        if (node.prev == null && node.next == null) {
            first = null;
            last = null;
        } else if (node.prev == null) {
            node.next.prev = null;
            first = node.next;
        } else if (node.next == null) {
            node.prev.next = null;
            last = node.prev;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }
}
