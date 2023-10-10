package se.fusion1013.plugin.cobaltcore.util;

public interface IProviderStorage {
    void put(String key, INameProvider provider);
    boolean has(String key);
    INameProvider get(String key);
}
