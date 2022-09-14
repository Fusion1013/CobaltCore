package se.fusion1013.plugin.cobaltcore.animator;

public interface IAnimatorComponent<T> {

    void setValue(String key, T value);

    T getValue(String key);

    String[] getVariableKeys();

}
