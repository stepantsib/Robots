package gui;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Модель робота.
 * Хранит координаты робота, направление движения и целевую точку.
 * Отвечает за вычисление движения робота и уведомляет представления
 * об изменении состояния через PropertyChangeListener.
 */
public class RobotModel {

    /**
     * Максимальная линейная скорость робота.
     */
    private static final double MAX_VELOCITY = 0.1;

    /**
     * Максимальная угловая скорость робота.
     */
    private static final double MAX_ANGULAR_VELOCITY = 0.01;

    /**
     * Объект поддержки слушателей изменений состояния модели.
     */
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Текущая координата X робота.
     */
    private volatile double robotPositionX = 100;

    /**
     * Текущая координата Y робота.
     */
    private volatile double robotPositionY = 100;


    /**
     * Текущее направление движения робота (в радианах).
     */
    private volatile double robotDirection = 0;

    /**
     * Координата X целевой точки.
     */
    private volatile int targetPositionX = 150;

    /**
     * Координата Y целевой точки.
     */
    private volatile int targetPositionY = 100;

    /**
     * Вычисляет расстояние между двумя точками.
     */
    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    /**
     * Вычисляет угол между двумя точками.
     */
    private double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;

        return normalizeAngle(Math.atan2(diffY, diffX));
    }

    /**
     * Нормализует угол в диапазон от 0 до 2π.
     */
    private static double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    /**
     * Ограничивает значение заданным диапазоном.
     */
    private static double applyLimits(double value, double min, double max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    /**
     * Добавляет слушателя изменений состояния модели.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * Возвращает текущую координату X робота.
     */
    public double getRobotPositionX() {
        return robotPositionX;
    }

    /**
     * Возвращает текущую координату Y робота.
     */
    public double getRobotPositionY() {
        return robotPositionY;
    }

    /**
     * Возвращает текущее направление движения робота.
     */
    public double getRobotDirection() {
        return robotDirection;
    }

    /**
     * Возвращает координату X целевой точки.
     */
    public int getTargetPositionX() {
        return targetPositionX;
    }

    /**
     * Возвращает координату Y целевой точки.
     */
    public int getTargetPositionY() {
        return targetPositionY;
    }

    /**
     * Выполняет перемещение робота на основе линейной и угловой скорости.
     * После обновления координат уведомляет слушателей об изменении модели.
     */
    private void moveRobot(double velocity, double angularVelocity, double duration) {

        velocity = applyLimits(velocity, 0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);
        double newX = robotPositionX + velocity / angularVelocity *
                (Math.sin(robotDirection + angularVelocity * duration) -
                        Math.sin(robotDirection));
        if (!Double.isFinite(newX)) {
            newX = robotPositionX + velocity * duration * Math.cos(robotDirection);
        }
        double newY = robotPositionY - velocity / angularVelocity *
                (Math.cos(robotDirection + angularVelocity * duration) -
                        Math.cos(robotDirection));
        if (!Double.isFinite(newY)) {
            newY = robotPositionY + velocity * duration * Math.sin(robotDirection);
        }
        robotPositionX = newX;
        robotPositionY = newY;
        robotDirection = normalizeAngle(robotDirection + angularVelocity * duration);

        pcs.firePropertyChange("robotPosition", null, null);
    }

    /**
     * Обновляет состояние модели.
     * Вызывается для перемещения робота к целевой точке.
     */
    protected void onModelUpdateEvent() {
        double distance = distance(targetPositionX, targetPositionY,
                robotPositionX, robotPositionY);
        if (distance < 0.5) return;
        double angleToTarget = angleTo(robotPositionX, robotPositionY,
                targetPositionX, targetPositionY);
        /*
        double angularVelocity = 0;
        if (angleToTarget > robotDirection) {
            angularVelocity = MAX_ANGULAR_VELOCITY;
        }
        if (angleToTarget < robotDirection) {
            angularVelocity = -MAX_ANGULAR_VELOCITY;
        } */

        double angleDiff = normalizeAngle(angleToTarget - robotDirection);

        double angularVelocity = 0;

        if (angleDiff > 0) {
            angularVelocity = MAX_ANGULAR_VELOCITY;
        } else if (angleDiff < 0) {
            angularVelocity = -MAX_ANGULAR_VELOCITY;
        }

        moveRobot(MAX_VELOCITY, angularVelocity, 10);
    }

    /**
     * Нормализует угол в диапазон [-PI, PI].
     */
    public double normalizeAngle(double angle) {
        return Math.IEEEremainder(angle, 2 * Math.PI);
    }

    /**
     * Устанавливает новую целевую точку движения робота.
     */
    protected void setTargetPosition(Point p) {
        targetPositionX = p.x;
        targetPositionY = p.y;
    }

}
