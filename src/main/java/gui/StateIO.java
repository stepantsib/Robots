package gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс выполняет чтение и запись состояния приложения в файл state.cfg.
 * Файл создаётся в домашнем каталоге пользователя в директории,
 * названной по фамилии. Формат файла — текстовый: каждая строка
 * содержит пару вида key=value
 * Ничего не знает о компонентах и префиксах.
 */
public class StateIO {

    /**
     * Название директории внутри домашнего каталога пользователя.
     */
    private static final String SURNAME_DIR = "Tsibizov";

    /**
     * Имя файла состояния приложения.
     */
    private static final String FILE_NAME = "state.cfg";

    /**
     * Полный путь к файлу состояния.
     */
    private final Path stateFilePath;


    /**
     * Создаёт объект для работы с файлом состояния.
     * Путь формируется как ${user.home}/{SURNAME_DIR}/state.cfg.
     */
    public StateIO() {
        String home = System.getProperty("user.home");
        this.stateFilePath = Path.of(home, SURNAME_DIR, FILE_NAME);
    }


    /**
     * Загружает состояние из файла.
     * Если файл отсутствует, возвращается пустой словарь.
     */
    public Map<String, String> load() {
        Map<String, String> result = new HashMap<>();

        if (!Files.exists(stateFilePath)) {
            return result;
        }

        try (BufferedReader reader = Files.newBufferedReader(stateFilePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                int eq = line.indexOf('=');
                if (eq < 0) {
                    continue;
                }

                String key = line.substring(0, eq).trim();
                String value = line.substring(eq + 1).trim();

                if (!key.isEmpty()) {
                    result.put(key, value);
                }

            }
        } catch (IOException _) { }
        return result;
    }

    /**
     * Сохраняет переданный словарь состояния в файл.
     * При необходимости создаёт директорию.
     * Ошибки записи не приводят к завершению приложения.
     */
    public void save(Map<String, String> state) {
        try {
            Files.createDirectories(stateFilePath.getParent());
        } catch (IOException _) {}

        try (BufferedWriter writer = Files.newBufferedWriter(stateFilePath,StandardCharsets.UTF_8)) {

            for (Map.Entry<String, String> entry : state.entrySet()) {

                String key = entry.getKey();
                String value = entry.getValue();

                if (value == null || key == null) {
                    continue;
                }

                writer.write(key);
                writer.write("=");
                writer.write(value);
                writer.newLine();

            }

            writer.newLine();

        } catch (IOException _) { }
    }
}
