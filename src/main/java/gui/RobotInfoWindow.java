package gui;

import gui.saveState.SaveAndRestoreState;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

/**
 * Внутреннее окно отображения состояния робота.
 * Показывает координаты, угол робота и угол до цели.
 */
public class RobotInfoWindow extends JInternalFrame implements PropertyChangeListener, SaveAndRestoreState {

    /**
     * Модель робота, из которой берутся данные.
     */
    private RobotModel model;

    /**
     * Текстовая область для отображения состояния.
     */
    private TextArea infoContent;

    /**
     * Конструктор - создаёт окно состояния и регистрирует слушателя изменений модели.
     */
    public RobotInfoWindow(RobotModel model) {
        super("Состояние робота", true, true, true, true);
        this.model = model;
        this.model.addPropertyChangeListener(this);

        this.infoContent = new TextArea("");
        this.infoContent.setSize(250, 120);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(infoContent, BorderLayout.CENTER);
        getContentPane().add(panel);

        pack();
        updateInfoContent();
    }

    @Override
    public String getStatePrefix() {
        return "robotInfo";
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
     * Обновляет содержимое окна состояния на основе текущего состояния модели.
     */
    private void updateInfoContent() {
        double x = model.getRobotPositionX();
        double y = model.getRobotPositionY();
        double robotAngle = model.getRobotDirection();
        double angleToTarget = Math.atan2(
                model.getTargetPositionY() - y,
                model.getTargetPositionX() - x
        );

        String content = ""
                + "x = " + x + "\n"
                + "y = " + y + "\n"
                + "robot angle (rad) = " + robotAngle + "\n"
                + "angle to target (rad) = " + angleToTarget + "\n";

        infoContent.setText(content);
    }

    /**
     * Вызывается при изменении модели и обновляет отображение.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        updateInfoContent();
    }
}