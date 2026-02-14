package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import se.fusion1013.cobaltCore.variable.provider.DurationValueProvider;

public class DurationVariable extends AbstractVariable<Integer, DurationValueProvider, IntegerArgument, DurationVariable> {

    public DurationVariable(String name, int defaultValue) {
        super(name, new DurationValueProvider(name, defaultValue));
    }

    public DurationVariable(String name) {
        super(name, new DurationValueProvider(name));
    }

    @Override
    public DurationVariable getSelf() {
        return this;
    }

    @Override
    public IntegerArgument getArgument() {
        IntegerArgument argument = new IntegerArgument(getName());
        argument.setOptional(optionalArgument);
        if (argumentSuggestions != null)
            argument.replaceSuggestions(ArgumentSuggestions.strings(argumentSuggestions.get()));
        return argument;
    }
}
