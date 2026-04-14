package log;

import java.util.ArrayList;

/**
 * Потокобезопасный источник логов с ограниченным размером.
 */
public class LogWindowSource {

    /**
     * Список зарегистрированных слушателей
     */
    private final ArrayList<LogChangeListener> listeners;

    /**
     * Максимальный размер лога
     */
    private final int queueLength;

    /**
     * Потокобезопасное хранилище сообщений
     */
    private final LimitedList<LogEntry> messages;

    /**
     * Кэш слушателей для оптимизации уведомлений
     */
    private volatile LogChangeListener[] activeListeners;

    /**
     * Конструктор - создаёт источник лога с указанным максимальным размером.
     */
    public LogWindowSource(int iQueueLength) {
        queueLength = iQueueLength;
        messages = new LimitedList<>(queueLength);
        listeners = new ArrayList<>();
    }

    /**
     * Регистрирует слушателя изменений лога
     */
    public void registerListener(LogChangeListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
            activeListeners = null;
        }
    }

    /**
     * Удаляет слушателя
     */
    public void unregisterListener(LogChangeListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
            activeListeners = null;
        }
    }

    /**
     * Добавляет сообщение в лог и уведомляет слушателей
     */
    public void append(LogLevel logLevel, String strMessage) {
        LogEntry entry = new LogEntry(logLevel, strMessage);

        messages.add(entry);

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
