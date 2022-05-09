package se.fusion1013.plugin.cobaltcore.variable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SharedVariable<T> implements Cloneable {

    // ----- VARIABLES -----

    List<Method> methods = new ArrayList<>();
    T value;

    // ----- CONSTRUCTORS -----

    public SharedVariable() {}

    // ----- GETTERS / SETTERS -----

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    // ----- CLONE -----

    public SharedVariable(SharedVariable<T> target) {
        this.value = target.value;
    }

    @Override
    public SharedVariable<T> clone() {
        return new SharedVariable<>(this);
    }
}
