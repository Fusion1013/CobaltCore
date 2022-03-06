package se.fusion1013.plugin.cobaltcore.util;

/**
 * This represents a variable that can be shared across several classes, and will keep the same value regardless of where it was changed.
 * @param <T> the type of the value.
 */
public class SharedVariable<T> {

    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

}
