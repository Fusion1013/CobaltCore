package se.fusion1013.cobaltCore.manager.registry;

import se.fusion1013.cobaltCore.util.IProviderStorage;

public class RegistryProviderStorage<T> implements IProviderStorage<T> {

    private final SimpleCobaltRegistry<T> registry;

    public RegistryProviderStorage(SimpleCobaltRegistry<T> registry) {
        this.registry = registry;
    }

    @Override
    public void put(String key, T provider) {
        registry.register(key, provider);
    }

    @Override
    public boolean has(String key) {
        return registry.get(key) != null;
    }

    @Override
    public T get(String key) {
        return registry.get(key);
    }
}
