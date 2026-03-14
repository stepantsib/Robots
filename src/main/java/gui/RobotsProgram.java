package gui;

import javax.swing.*;

/**
 * Точка входа в приложение.
 * Отвечает за запуск программы и создание главного окна.
 */
public class RobotsProgram {

    /**
     * Главный метод программы.
     * Устанавливает тему оформления интерфейса
     * и запускает главное окно приложения.
     */
    static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            MainApplicationFrame frame = new MainApplicationFrame();
            frame.setVisible(true);
        });
    }
}
