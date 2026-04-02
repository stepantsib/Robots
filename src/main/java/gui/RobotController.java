package gui;

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
            }
        });

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> onModelUpdateEvent());
            }
        }, 0, (long) 10);
    }

    protected void onModelUpdateEvent() {
        double x = model.getRobotPositionX();
        double y = model.getRobotPositionY();
        double dir = model.getRobotDirection();

        double tx = model.getTargetPositionX();
        double ty = model.getTargetPositionY();

        double distance = distance(tx, ty, x, y);

        // Захват цели
        if (distance <= 2.0) {
            model.setRobotState(tx, ty, dir);
            return;
        }

        double targetAngle = angleTo(x, y, tx, ty);
        double angleDiff = normalizeToPi(targetAngle - dir);

        double maxTurn = MAX_ANGULAR_VELOCITY * 10;
        double turn = applyLimits(angleDiff, -maxTurn, maxTurn);

        double velocity = (Math.abs(angleDiff) < Math.PI / 4) ? MAX_VELOCITY : 0.0;

        moveRobot(velocity, turn, 10);
    }

    private void moveRobot(double velocity, double turnAngle, double duration) {
        double x = model.getRobotPositionX();
        double y = model.getRobotPositionY();
        double dir = model.getRobotDirection();

        double newDir = asNormalizedRadians(dir + turnAngle);

        double tx = model.getTargetPositionX();
        double ty = model.getTargetPositionY();
        double distToTarget = distance(tx, ty, x, y);

        double step = velocity * duration;
        if (step >= distToTarget) {
            model.setRobotState(tx, ty, newDir);
            return;
        }

        double newX = x + step * Math.cos(newDir);
        double newY = y + step * Math.sin(newDir);

        model.setRobotState(newX, newY, newDir);
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
