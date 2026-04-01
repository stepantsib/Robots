package gui;

import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

/**
 * Главное окно приложения.
 * Содержит рабочую область, окно логов, игровое окно и верхнее меню.
 * Также отвечает за переключение темы оформления и корректное закрытие программы.
 */
public class MainApplicationFrame extends JFrame implements SaveAndRestoreState {

    /**
     * Объект для чтения и записи состояния приложения в файл.
     */
    private final StateIO stateIO = new StateIO();

    /**
     * Координатор сохранения и восстановления состояния всех компонентов.
     */
    private final StateManager stateManager = new StateManager();


    @Override
    public String getStatePrefix() {
        return "main";
    }

    @Override
    public void saveState(Map<String, String> map) {
        map.put("x", String.valueOf(getX()));
        map.put("y", String.valueOf(getY()));
        map.put("w", String.valueOf(getWidth()));
        map.put("h", String.valueOf(getHeight()));
        map.put("state", String.valueOf(getExtendedState()));
    }

    @Override
    public void loadState(Map<String, String> map) {
        try {
            int x = Integer.parseInt(map.get("x"));
            int y = Integer.parseInt(map.get("y"));
            int w = Integer.parseInt(map.get("w"));
            int h = Integer.parseInt(map.get("h"));
            int state = Integer.parseInt(map.get("state"));

            setBounds(x, y, w, h);
            setExtendedState(state);
        } catch (Exception ignored) {}
    }

    /**
     * Рабочая область главного окна.
     * Используется для размещения внутренних окон приложения.
     */
    private final JDesktopPane desktopPane = new JDesktopPane();

    /**
     * Конструктор главного окна.
     * Настраивает размеры окна, создаёт внутренние окна,
     * устанавливает меню и перехватывает событие закрытия.
     */
    public MainApplicationFrame() {
        // Отступ от границ экрана
        int inset = 50;

        // Установка размеров главного окна
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane);

        // Создание и добавление окна логов
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        // Создание и добавление игрового окна
        RobotModel model = new RobotModel();
        GameWindow gameWindow = new GameWindow(model);
        RobotPositionWindow positionWindow = new RobotPositionWindow(model);
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);
        addWindow(positionWindow);

        // Установка меню
        setJMenuBar(generateMenuBar());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });

        stateManager.register(this);
        stateManager.register(logWindow);
        stateManager.register(gameWindow);
        stateManager.register(positionWindow);

        Map<String, String> root = stateIO.load();
        stateManager.loadAll(root);
    }

    /**
     * Создаёт и настраивает окно логирования.
     *
     * @return готовое окно логов
     */
    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);

        Logger.debug("Протокол работает");
        return logWindow;
    }

    /**
     * Добавляет внутреннее окно в рабочую область
     * и делает его видимым.
     *
     * @param frame добавляемое внутреннее окно
     */
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    /**
     * Создаёт верхнюю панель меню приложения.
     *
     * @return панель меню
     */
    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();

        JMenu lookAndFeelMenu = modeOfDisplay();
        JMenu testMenu = modeTestMenu();
        JMenu exitMenu = modeExitMenu();

        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        menuBar.add(exitMenu);

        return menuBar;
    }

    /**
     * Создаёт меню переключения режима отображения.
     * Содержит пункты выбора системной и универсальной темы.
     *
     * @return меню режима отображения
     */
    private JMenu modeOfDisplay() {

        // Меню переключения режима отображения
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        // Пункт выбора системной темы
        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(systemLookAndFeel);

        // Пункт выбора универсальной темы
        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(crossplatformLookAndFeel);

        return lookAndFeelMenu;
    }

    /**
     * Создаёт тестовое меню.
     * Используется для работы логирования.
     *
     * @return тестовое меню
     */
    private JMenu modeTestMenu() {
        // Тестовое меню
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        // Пункт добавления сообщения в лог
        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        testMenu.add(addLogMessageItem);

        return testMenu;
    }

    /**
     * Создаёт меню "Файл" с пунктом выхода из программы.
     *
     * @return меню выхода
     */
    private JMenu modeExitMenu() {

        // Меню выхода
        JMenu exitMenu = new JMenu("Файл");
        exitMenu.setMnemonic(KeyEvent.VK_Z);
        JMenuItem exitFromProgram = new JMenuItem("Выход", KeyEvent.VK_C);
        exitFromProgram.addActionListener(event ->
                exitApplication()
        );
        exitMenu.add(exitFromProgram);

        return exitMenu;
    }

    /**
     * Выполняет завершение работы приложения.
     * Показывает окно подтверждения и закрывает
     * главное окно при согласии пользователя.
     */
    private void exitApplication() {

        int result = JOptionPane.showOptionDialog(
                this,
                "Вы действительно хотите выйти?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Да", "Нет"},
                "Нет"
        );

        if (result == JOptionPane.YES_OPTION) {
            Map<String, String> root = stateManager.saveAll();
            stateIO.save(root);

            dispose();
            System.exit(0);
        }
    }

    /**
     * Устанавливает тему оформления приложения
     * и обновляет интерфейс.
     *
     * @param className имя класса темы оформления
     */
    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);

            // Обновление всех компонентов окна
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}
