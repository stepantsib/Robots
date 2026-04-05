package gui;

import gui.saveState.SaveAndRestoreState;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

/**
 * Внутреннее окно отображения позиции робота.
 * Показывает текущие координаты и направление робота.
 * Подписывается на изменения модели и обновляет информацию
 * при получении уведомлений об изменении состояния.
 * Поддерживает сохранение и восстановление своего состояния.
 */
public class RobotPositionWindow extends JInternalFrame implements SaveAndRestoreState, PropertyChangeListener {

    /**
     * Модель(логика) робота.
     */
    private final RobotModel model;

    /**
     * Отображение координат и направления робота.
     */
    private final JLabel positionLabel;

    /**
     * Конструктор, создаёт окно отображения позиции робота и подписывается на изменения модели.
     */
    public RobotPositionWindow(RobotModel model) {
        super("Позиция робота", true, true, true, true);

        this.model = model;
        model.addPropertyChangeListener(this);
        positionLabel = new JLabel();

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(positionLabel, BorderLayout.CENTER);

        getContentPane().add(panel);

        updateText();
        pack();
    }

    /**
     * Обновляет текст метки с текущими координатами и направлением робота.
     */
    private void updateText() {
        String text = String.format("X: %.2f Y: %.2f Direction: %.2f", model.getRobotPositionX(), model.getRobotPositionY(), model.getRobotDirection());
        positionLabel.setText(text);
    }

    /**
     * Обрабатывает событие изменения состояния модели.
     * При изменении позиции робота обновляет отображаемую информацию.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("robotPosition".equals(evt.getPropertyName())) {
            updateText();
        }
    }

    @Override
    public String getStatePrefix() {
        return "robotPosition";
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
}
