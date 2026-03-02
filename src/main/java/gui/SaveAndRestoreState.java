package gui;

import java.util.Map;

public interface SaveAndRestoreState {

    /**
     * Возвращает префикс компонента,
     * используемый при объединении состояний в общий словарь.
     */
    String getStatePrefix();

    /**
     * Записывает состояние компонента в переданный словарь.
     */
    void saveState(Map<String, String> map);

    /**
     * Восстанавливает состояние компонента
     * из переданного словаря.
     */
    void loadState(Map<String, String> map);
}
