package gui;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Координатор сохранения/восстановления состояния.
 * Хранит список компонентов и умеет собирать/раздавать общий словарь состояния.
 */
public class StateManager {

    /**
     * Список зарегистрированных компонентов, состояние которых нужно сохранять.
     */
    private final List<SaveAndRestoreState> components = new ArrayList<>();

    /**
     * Регистрирует компонент для сохранения/восстановления состояния.
     */
    public void register(SaveAndRestoreState component) {
        if (component == null) {
            return;
        }
        components.add(component);
    }

    /**
     * Собирает общее состояние всех зарегистрированных компонентов в один словарь.
     * Для каждого компонента используется его префикс.
     */
    public Map<String, String> saveAll() {
        Map<String, String> root = new HashMap<>();

        for (SaveAndRestoreState c : components) {
            String prefix = c.getStatePrefix();

            if (prefix == null || prefix.trim().isEmpty()) {
                continue;
            }

            Map<String, String> subMap = new PrefixedMap(root, prefix);
            c.saveState(subMap);
        }

        return root;
    }

    /**
     * Восстанавливает состояние всех зарегистрированных компонентов из общего словаря.
     * Если словарь пустой или null, метод ничего не делает.
     */
    public void loadAll(Map<String, String> root) {
        if (root == null || root.isEmpty()) {
            return;
        }

        for (SaveAndRestoreState c : components) {
            String prefix = c.getStatePrefix();
            if (prefix == null || prefix.trim().isEmpty()) {
                continue;
            }

            Map<String, String> subMap = new PrefixedMap(root, prefix);
            c.loadState(subMap);
        }
    }
}
