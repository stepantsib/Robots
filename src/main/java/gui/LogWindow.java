package gui;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Внутреннее окно отображения протокола работы.
 * Показывает сообщения логирования и поддерживает
 * сохранение и восстановление своего состояния.
 */
public class LogWindow extends JInternalFrame implements LogChangeListener, SaveAndRestoreState {

    /**
     * Источник логов, из которого получаются сообщения.
     */
    private LogWindowSource logSource;

    /**
     * Текстовая область для отображения логов.
     */
    private TextArea logContent;

    /**
     * Конструктор - создаёт окно логирования и регистрирует слушателя изменений.
     */
    public LogWindow(LogWindowSource logSource) {
        super("Протокол работы", true, true, true, true);
        this.logSource = logSource;
        this.logSource.registerListener(this);
        this.logContent = new TextArea("");
        this.logContent.setSize(200, 500);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
    }

    @Override
    public String getStatePrefix() {
        return "log";
    }

    @Override
    public void saveState(Map<String, String> map) {
        map.put("x", String.valueOf(getX()));
        map.put("y", String.valueOf(getY()));
        map.put("w", String.valueOf(getWidth()));
        map.put("h", String.valueOf(getHeight()));
        map.put("icon", String.valueOf(isIcon()));
        map.put("max", String.valueOf(isMaximum()));
    }

    @Override
    public void loadState(Map<String, String> map) {
        try {
            int x = Integer.parseInt(map.get("x"));
            int y = Integer.parseInt(map.get("y"));
            int w = Integer.parseInt(map.get("w"));
            int h = Integer.parseInt(map.get("h"));

            setBounds(x, y, w, h);

            if (Boolean.parseBoolean(map.get("max"))) {
                setMaximum(true);
            }

            if (Boolean.parseBoolean(map.get("icon"))) {
                setIcon(true);
            }

        } catch (Exception ignored) {
        }
    }

    /**
     * Обновляет содержимое окна логов на основе текущих записей.
     */
    private void updateLogContent() {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : logSource.all()) {
            content.append(entry.getMessage()).append("\n");
        }
        logContent.setText(content.toString());
        logContent.invalidate();
    }

    /**
     * Вызывается при изменении логов и обновляет отображение.
     */
    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }
}
