package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Компонент визуализации игрового процесса.
 * Отображает робота и целевую точку на игровом поле,
 * а также обрабатывает пользовательские клики для задания новой цели.
 * Подписывается на изменения модели и перерисовывает поле
 * при обновлении состояния робота.
 */
public class GameVisualizer extends JPanel implements PropertyChangeListener {

    /**
     * Таймер, генерирующий события перерисовки и обновления модели.
     */
    private final java.util.Timer timer = initTimer();

    /**
     * Модель(логика) робота.
     */
    private RobotModel model;

    /**
     * Создаёт компонент визуализации и подписывается на изменения модели.
     * Также запускает таймеры для обновления состояния и перерисовки.
     */
    public GameVisualizer(RobotModel model) {
        this.model = model;
        model.addPropertyChangeListener(this);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 50);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                model.onModelUpdateEvent();
            }
        }, 0, 10);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Click at: " + e.getPoint());
                model.setTargetPosition(e.getPoint());
                repaint();
            }
        });
        setDoubleBuffered(true);
    }

    /**
     * Создаёт таймер для генерации событий обновления.
     */
    private static java.util.Timer initTimer() {
        java.util.Timer timer = new Timer("events generator", true);
        return timer;
    }

    /**
     * Округляет значение до ближайшего целого числа.
     */
    private static int round(double value) {
        return (int) (value + 0.5);
    }

    /**
     * Рисует закрашенный овал с центром в заданной точке.
     */
    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    /**
     * Рисует контур овала с центром в заданной точке.
     */
    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    /**
     * Запрашивает перерисовку компонента
     */
    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    /**
     * Отрисовывает робота на игровом поле.
     */
    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        AffineTransform old = g.getTransform();  // сохраняем старую трансформацию
        int robotCenterX = round(x);
        int robotCenterY = round(y);
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);
        g.setTransform(t);

        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX + 10, robotCenterY, 5, 5);

        g.setTransform(old);  // восстанавливаем старую трансформацию
    }

    /**
     * Отрисовывает целевую точку движения робота.
     */
    private void drawTarget(Graphics2D g, int x, int y) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }

    /**
     * Основной метод отрисовки компонента.
     * Вызывает методы отображения робота и целевой точки.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);  // очищает фон
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d, round(model.getRobotPositionX()), round(model.getRobotPositionY()), model.getRobotDirection());
        drawTarget(g2d, model.getTargetPositionX(), model.getTargetPositionY());
    }

    /**
     * Обрабатывает событие изменения состояния модели
     * и инициирует перерисовку игрового поля.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        repaint();
    }
}
