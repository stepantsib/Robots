package gui;

import log.Logger;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

public class RobotController {

    private final Timer timer = initTimer();

    private final RobotModel model;
    private final GameVisualizer view;

    private static final double MAX_VELOCITY = 0.1;
    private static final double MAX_ANGULAR_VELOCITY = 0.01;

    public RobotController(RobotModel model, GameVisualizer view) {
        this.model = model;
        this.view = view;
    }

    private static Timer initTimer() {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    public void start() {
        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                model.setTargetPosition(e.getX(), e.getY());
                Logger.debug(String.format("Target position changed to (%d, %d)", e.getX(), e.getY()));
            }
        });

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> onModelUpdateEvent());
            }
        }, 0, 10);
    }

    protected void onModelUpdateEvent()
    {
        double distance = distance(model.getTargetPositionX(), model.getTargetPositionY(),
                model.getRobotPositionX(), model.getRobotPositionY());
        if (distance < 0.5)
        {
            return;
        }

        double velocity = MAX_VELOCITY;
        double angleToTarget = angleTo(model.getRobotPositionX(), model.getRobotPositionY(),
                    model.getTargetPositionX(), model.getTargetPositionY());

        //берём кратчайшую разницу углов в диапазоне (-PI, PI]
        double angleDiff = normalizeToPi(angleToTarget - model.getRobotDirection());

        double angularVelocity = 0;
        if (angleDiff > 0)
        {
            angularVelocity = MAX_ANGULAR_VELOCITY;
        }
        if (angleDiff < 0)
        {
            angularVelocity = -MAX_ANGULAR_VELOCITY;
        }

        moveRobot(velocity, angularVelocity, 10);
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {

        velocity = applyLimits(velocity, 0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);

        double robotPositionX = model.getRobotPositionX();
        double robotPositionY = model.getRobotPositionY();
        double robotDirection = model.getRobotDirection();

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

        double newDirection = asNormalizedRadians(robotDirection + angularVelocity * duration);

        model.setRobotState(newX, newY, newDirection);
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;

        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    private static double applyLimits(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    private static double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    // (-PI, PI]
    private static double normalizeToPi(double angle) {
        while (angle <= -Math.PI) angle += 2 * Math.PI;
        while (angle > Math.PI) angle -= 2 * Math.PI;
        return angle;
    }

}
