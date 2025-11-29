package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.executors.CommandArguments;
import se.fusion1013.cobaltCore.commands.system.ICommandValue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractVariable<VALUE, ARGUMENT extends Argument<VALUE>, SELF> implements ICommandValue<ARGUMENT> {

    protected VALUE value;
    private final String name;

    protected boolean optionalArgument = false;
    protected Supplier<String[]> argumentSuggestions;

    private final List<Consumer<VALUE>> onValueChange = new ArrayList<>();

    public AbstractVariable(String name, VALUE defaultValue) {
        this.name = name;
        this.value = defaultValue;
    }

    @Override
    public void setValue(CommandArguments arguments) {
        if (arguments.get(getName()) != null) value = (VALUE) arguments.get(getName());
        onValueChange.forEach(k -> k.accept(value));
    }

    @Override
    public String getName() {
        return name;
    }

    public VALUE getValue() {
        return value;
    }

    public abstract SELF getSelf();

    public SELF optional() {
        optionalArgument = true;
        return getSelf();
    }

    public SELF onValueChange(Consumer<VALUE> event) {
        this.onValueChange.add(event);
        return getSelf();
    }

    public SELF suggestions(Supplier<String[]> supplier) {
        this.argumentSuggestions = supplier;
        return getSelf();
    }

}
