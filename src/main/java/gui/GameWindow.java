package gui;

import gui.saveState.SaveAndRestoreState;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Внутреннее окно игрового поля.
 * Отображает визуализацию игры и поддерживает
 * сохранение и восстановление своего состояния.
 */
public class GameWindow extends JInternalFrame implements SaveAndRestoreState {

    /**
     * Компонент, отвечающий за отрисовку игрового процесса.
     */
    private final GameVisualizer gameVisualizer;

    /**
     * Создаёт игровое окно и инициализирует визуализатор.
     */
    public GameWindow() {
        super("Игровое поле", true, true, true, true);
        RobotModel model = new RobotModel();
        gameVisualizer = new GameVisualizer(model);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(gameVisualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    @Override
    public String getStatePrefix() {
        return "game";
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
