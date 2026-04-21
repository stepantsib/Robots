package log;

import java.util.ArrayList;
import java.util.LinkedList;

public class LimitedList<T> {

    private final int limit;
    private final LinkedList<T> list;

    public LimitedList(int limit) {
        this.limit = limit;
        this.list = new LinkedList<>();
    }

    public synchronized void add(T element) {
        while (list.size() > limit) {
            list.removeFirst();
        }
        list.add(element);
    }

    public synchronized int size() {
        return list.size();
    }

    public synchronized Iterable<T> all() {
        return new ArrayList<>(list);
    }

    public synchronized Iterable<T> range(int startFrom, int count) {
        return new ArrayList<>(list).subList(startFrom, startFrom + count);
    }

}
