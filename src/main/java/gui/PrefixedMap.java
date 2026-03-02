package gui;

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
     * Удаляет значение по ключу без префикса.
     * В исходном словаре удаляется запись с префиксом.
     * @return предыдущее значение, которое было связано с этим ключом или null, если такого ключа не было.
     */
    @Override
    public String remove(Object key) {
        if (!(key instanceof String)) {
            return null;
        }
        return rootMap.remove(prefixWithDot + key);
    }

    /**
     * Возвращает множество пар ключ-значение, относящихся только к данному префиксу.
     * Ключи в возвращаемом представлении не содержат префикса.
     */
    @Override
    public Set<Entry<String, String>> entrySet() {
        return new PrefixedEntrySet();
    }

    /**
     * Множество записей (Entry) только для текущего префикса.
     */
    private class PrefixedEntrySet extends AbstractSet<Entry<String, String>> {

        /**
         * Возвращает итератор, который перебирает только записи с нужным префиксом.
         */
        @Override
        public Iterator<Entry<String, String>> iterator() {
            return new PrefixedIterator();
        }

        /**
         * Возвращает количество записей с нужным префиксом.
         */
        @Override
        public int size() {
            int count = 0;
            for (String k : rootMap.keySet()) {
                if (k.startsWith(prefixWithDot)) {
                    count++;
                }
            }
            return count;
        }
    }

    /**
     * Итератор по rootMap, который пропускает все записи без нужного префикса.
     */
    private class PrefixedIterator implements Iterator<Entry<String, String>> {

        /**
         * Итератор по всем записям исходного словаря.
         */
        private final Iterator<Entry<String, String>> baseIterator = rootMap.entrySet().iterator();

        /**
         * Следующая подходящая запись из rootMap (с нужным префиксом), либо null.
         */
        private Entry<String, String> nextEntry;

        /**
         * Проверяет, существует ли следующая запись
         * с нужным префиксом в исходном словаре.
         */
        @Override
        public boolean hasNext() {
            while (nextEntry == null && baseIterator.hasNext()) {
                Entry<String, String> e = baseIterator.next();
                if (e.getKey().startsWith(prefixWithDot)) {
                    nextEntry = e;
                }
            }
            return nextEntry != null;
        }

        /**
         * Возвращает следующую запись без префикса.
         * Если подходящих записей больше нет, выбрасывает исключение.
         */
        @Override
        public Entry<String, String> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Entry<String, String> fullEntry = nextEntry;
            nextEntry = null;

            String shortKey = fullEntry.getKey().substring(prefixWithDot.length());
            return new PrefixedEntry(shortKey, fullEntry.getValue());
        }
    }

    /**
     * Запись (Entry) с ключом без префикса.
     * При изменении значения через setValue обновляет rootMap.
     */
    private class PrefixedEntry implements Entry<String, String> {

        /**
         * Ключ без префикса, используемый в представлении PrefixedMap.
         */
        private final String shortKey;

        /**
         * Текущее значение записи.
         */
        private String value;

        /**
         * Создаёт запись с ключом без префикса.
         */
        public PrefixedEntry(String shortKey, String value) {
            this.shortKey = shortKey;
            this.value = value;
        }

        /**
         * Возвращает ключ без префикса.
         */
        @Override
        public String getKey() {
            return shortKey;
        }

        /**
         * Возвращает значение записи.
         */
        @Override
        public String getValue() {
            return value;
        }

        /**
         * Устанавливает новое значение и обновляет исходный словарь.
         * Возвращает предыдущее значение.
         */
        @Override
        public String setValue(String newValue) {
            String oldValue = rootMap.put(prefixWithDot + shortKey, newValue);
            this.value = newValue;
            return oldValue;
        }
    }
}
