package log;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Потокобезопасная структура с ограниченным размером
 * @param <T>
 */
public class LimitedList<T> {

    /**
     * Максимальный размер
     */
    private final int limit;

    /**
     * Список
     */
    private final LinkedList<T> list;

    /**
    * Конструктор класса
    */
    public LimitedList(int limit) {
        this.limit = limit;
        this.list = new LinkedList<>();
    }

    /**
     * Добавляет сообщение в лог и уведомляет слушателей
     */
    public synchronized void add(T element) {
        while (list.size() > limit) {
            list.removeFirst();
        }
        list.add(element);
    }

    /**
     * Возвращает текущее количество сообщений
     */
    public synchronized int size() {
        return list.size();
    }


    /**
     * Возвращает все сообщения
     */
    public synchronized Iterable<T> all() {
        return new ArrayList<>(list);
    }

    /**
     * Возвращает диапазон сообщений по индексам
     */
    public synchronized Iterable<T> range(int startFrom, int count) {
        return new ArrayList<>(list).subList(startFrom, startFrom + count);
    }

}
