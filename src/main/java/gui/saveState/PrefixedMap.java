package gui.saveState;

import java.util.*;

/**
 * Представление подсловаря общего словаря по заданному префиксу.
 * Ключи в исходном словаре имеют вид "prefix.key",
 * а в данном представлении используются без префикса.
 */
public class PrefixedMap extends AbstractMap<String, String> {

    /**
     * Общий словарь, в котором фактически хранятся все данные.
     */
    private final Map<String, String> rootMap;

    /**
     * Префикс с добавленной точкой, используемый при обращении к ключам.
     */
    private final String prefixWithDot;

    /**
     * Создаёт представление подсловаря по указанному префиксу.
     * Если префикс не оканчивается точкой, она добавляется автоматически.
     */
    public PrefixedMap(Map<String, String> rootMap, String prefix) {
        this.rootMap = rootMap;
        this.prefixWithDot = prefix.endsWith(".") ? prefix : prefix + ".";
    }

    /**
     * Возвращает значение по ключу без префикса.
     * В исходном словаре поиск выполняется по ключу с префиксом.
     * @return значение по ключу или null, если такого ключа нет.
     */
    @Override
    public String get(Object key) {
        if (!(key instanceof String)) {
            return null;
        }
        return rootMap.get(prefixWithDot + key);
    }

    /**
     * Сохраняет значение по ключу без префикса.
     * В исходном словаре ключ автоматически дополняется префиксом.
     * @return предыдущее значение по этому ключу, или null, если раньше такого ключа не было.
     */
    @Override
    public String put(String key, String value) {
        return rootMap.put(prefixWithDot + key, value);
    }

    /**
     * Возвращает множество пар ключ-значение, относящихся только к данному префиксу.
     * Ключи в возвращаемом представлении не содержат префикса.
     */
    @Override
    public Set<Entry<String, String>> entrySet() {
        throw new UnsupportedOperationException();
    }

}
