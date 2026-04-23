package log;

import java.util.*;

/**
 * Потокобезопасный источник логов с ограниченным размером.
 */
public class LogWindowSource {

    /**
     * Список зарегистрированных слушателей
     */
    private final Set<LogChangeListener> listeners = Collections.newSetFromMap(new WeakHashMap<>());

    /**
     * Максимальный размер лога
     */
    private final int queueLength;

    /**
     * Потокобезопасное хранилище сообщений
     */
    private final LimitedList<LogEntry> messages;

    /**
     * Конструктор - создаёт источник лога с указанным максимальным размером.
     */
    public LogWindowSource(int iQueueLength) {
        queueLength = iQueueLength;
        messages = new LimitedList<>(queueLength);
    }

    /**
     * Регистрирует слушателя изменений лога
     */
    public void registerListener(LogChangeListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Удаляет слушателя
     */
    public void unregisterListener(LogChangeListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Добавляет сообщение в лог и уведомляет слушателей
     */
    public void append(LogLevel logLevel, String strMessage) {
        LogEntry entry = new LogEntry(logLevel, strMessage);

        messages.add(entry);

        List<LogChangeListener> activeListeners;

        synchronized (listeners) {
            activeListeners = new ArrayList<>(listeners);
        }

        for (LogChangeListener listener : activeListeners) {
            listener.onLogChanged();
        }
    }

    /**
     * Возвращает текущее количество сообщений
     */
    public int size() {
        return messages.size();
    }

    /**
     * Возвращает диапазон сообщений по индексам
     */
    public Iterable<LogEntry> range(int startFrom, int count) {
        return messages.range(startFrom, count);
    }

    /**
     * Возвращает все сообщения
     */
    public Iterable<LogEntry> all() {
        return messages.all();
    }
}
