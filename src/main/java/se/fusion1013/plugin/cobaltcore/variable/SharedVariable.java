package se.fusion1013.plugin.cobaltcore.variable;

import java.util.ArrayList;
import java.util.List;

public class SharedVariable<T> implements Cloneable {

    // ----- VARIABLES -----

    T value;
    List<Command<T>> commands = new ArrayList<>();

    // ----- UPDATE VALUE -----

    private void updateValue() {
        for (Command<T> command : commands) {
            command.updateVariable(value);
        }
    }

    // ----- GETTERS / SETTERS -----

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
        updateValue();
    }

    public void addCommand(Command<T> command) {
        this.commands.add(command);
    }

    // ----- CLONE -----

    public SharedVariable(SharedVariable<T> target) {
        this.value = target.value;
        this.commands = target.commands;
    }

    @Override
    public SharedVariable<T> clone() {
        return new SharedVariable<>(this);
    }

    // ----- METHOD INTERFACE -----

    public interface Command<T> {
        void updateVariable(T value);
    }

}
