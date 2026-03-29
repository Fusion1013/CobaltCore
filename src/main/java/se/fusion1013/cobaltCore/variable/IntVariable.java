package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import se.fusion1013.cobaltCore.variable.provider.IntValueProvider;

public class IntVariable extends AbstractVariable<Integer, IntValueProvider, IntegerArgument, IntVariable> {

    public IntVariable(String name) {
        super(name, new IntValueProvider(name, 0));
    }

    public IntVariable(String name, int defaultValue) {
        super(name, new IntValueProvider(name, defaultValue));
    }

    @Override
    public IntegerArgument getArgument() {
        IntegerArgument argument = new IntegerArgument(getName());
        argument.setOptional(optionalArgument);
        if (argumentSuggestions != null)
            argument.replaceSuggestions(ArgumentSuggestions.strings(argumentSuggestions.get()));
        return argument;
    }

    @Override
    public IntVariable getSelf() {
        return this;
    }

    public Integer getMin() {
        return valueGetter.getMin();
    }

    public Integer getMax() {
        return valueGetter.getMax();
    }
}
