package se.fusion1013.cobaltCore.util;

public interface IProviderStorage<T> {
    void put(String key, T provider);

    boolean has(String key);

    T get(String key);
}
