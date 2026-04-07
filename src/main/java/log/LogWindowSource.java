package log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Потокобезопасный источник логов с ограниченным размером.
 */
public class LogWindowSource {

    /** Список зарегистрированных слушателей */
    private final ArrayList<LogChangeListener> listeners;

    /** Максимальный размер лога */
    private final int queueLength;

    /** Потокобезопасное хранилище сообщений */
    private final ArrayBlockingQueue<LogEntry> messages;

    /** Кэш слушателей для оптимизации уведомлений */
    private volatile LogChangeListener[] activeListeners;

    /**
     * Конструктор - создаёт источник лога с указанным максимальным размером.
     */
    public LogWindowSource(int iQueueLength) {
        queueLength = iQueueLength;
        messages = new ArrayBlockingQueue<>(queueLength);
        listeners = new ArrayList<>();
    }

    /** Регистрирует слушателя изменений лога */
    public void registerListener(LogChangeListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
            activeListeners = null;
        }
    }

    /** Удаляет слушателя */
    public void unregisterListener(LogChangeListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
            activeListeners = null;
        }
    }

    /** Добавляет сообщение в лог и уведомляет слушателей */
    public void append(LogLevel logLevel, String strMessage) {
        LogEntry entry = new LogEntry(logLevel, strMessage);

        if (!messages.offer(entry)) {
            messages.poll();
            messages.offer(entry);
        }

        LogChangeListener[] activeListeners = this.activeListeners;

        if (activeListeners == null) {
            synchronized (listeners) {
                if (this.activeListeners == null) {
                    activeListeners = listeners.toArray(new LogChangeListener[0]);
                    this.activeListeners = activeListeners;
                }
            }
        }

        for (LogChangeListener listener : activeListeners) {
            listener.onLogChanged();
        }
    }

    /** Возвращает текущее количество сообщений */
    public int size() {
        return messages.size();
    }

    /** Возвращает диапазон сообщений по индексам */
    public Iterable<LogEntry> range(int startFrom, int count) {
        if (startFrom < 0 || startFrom >= messages.size()) {
            return Collections.emptyList();
        }
        List<LogEntry> snapshot = new ArrayList<>(messages);
        int indexTo = Math.min(startFrom + count, snapshot.size());
        return snapshot.subList(startFrom, indexTo);
    }

    /** Возвращает все сообщения */
    public Iterable<LogEntry> all() {
        return new ArrayList<>(messages);
    }
}
