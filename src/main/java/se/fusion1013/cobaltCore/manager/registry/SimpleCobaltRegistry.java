package se.fusion1013.cobaltCore.manager.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SimpleCobaltRegistry<T> {

    protected final Map<String, T> content = new HashMap<>();

    public T register(String key, T item) {
        content.put(key, item);
        return item;
    }

    @SuppressWarnings("unchecked")
    public T register(IRegistryItem provider) {
        return register(provider.getInternalName(), (T) provider);
    }

    public Collection<T> values() {
        return content.values();
    }

    public T get(String key) {
        return content.get(key);
    }

}
