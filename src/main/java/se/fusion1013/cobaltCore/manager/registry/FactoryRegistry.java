package se.fusion1013.cobaltCore.manager.registry;

import java.util.function.Supplier;

public class FactoryRegistry<T extends IRegistryItem> extends SimpleCobaltRegistry<Supplier<T>> {

    public T create(String key) {
        Supplier<T> factory = content.get(key);
        if (factory == null) return null;
        return factory.get();
    }

    public Supplier<T> register(Supplier<T> supplier) {
        T item = supplier.get();
        content.put(item.getInternalName(), supplier);
        return supplier;
    }

    public static <T extends IRegistryItem> FactoryRegistry<T> forRegistry(Supplier<T>... items) {
        FactoryRegistry<T> registry = new FactoryRegistry<>();
        for (Supplier<T> itemFactory : items) {
            T item = itemFactory.get();
            registry.register(item.getInternalName(), itemFactory);
        }
        return registry;
    }

}
