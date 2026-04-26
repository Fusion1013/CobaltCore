package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.commands.system.ICommandValue;
import se.fusion1013.cobaltCore.variable.provider.IValueProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractVariable<VALUE, GETTER extends IValueProvider<VALUE>, ARGUMENT extends Argument<VALUE>, SELF> implements ICommandValue<ARGUMENT> {

    protected GETTER valueGetter;
    private final String name;

    protected boolean optionalArgument = false;
    protected Supplier<String[]> argumentSuggestions;

    private final List<Consumer<VALUE>> onValueChange = new ArrayList<>();

    public AbstractVariable(String name, GETTER defaultValue) {
        this.name = name;
        this.valueGetter = defaultValue;
    }

    public void setValue(VALUE value) {
        valueGetter.setValue(value);
    }

    @Override
    public void setValueGetter(CommandArguments arguments) {
        if (arguments.get(getName()) != null) valueGetter.setValue((VALUE) arguments.get(getName()));
        onValueChange.forEach(k -> k.accept(valueGetter.getValue()));
    }

    @Override
    public String getName() {
        return name;
    }

    public void load(ConfigurationSection yaml) {
        valueGetter.load(yaml);
    }

    public void load(Map<?, ?> map) {
        try {
            valueGetter.load(map);
        } catch (Exception ex) {
            CobaltCore.getInstance().getLogger().severe("Failed loading variable " + name + ": " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public VALUE getValue() {
        return valueGetter.getValue();
    }

    public List<VALUE> getValueList() {
        return valueGetter.getValueList();
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

    public boolean isEmpty() {
        return valueGetter.isEmpty();
    }

    public boolean isPresent() {
        return !isEmpty();
    }

}
