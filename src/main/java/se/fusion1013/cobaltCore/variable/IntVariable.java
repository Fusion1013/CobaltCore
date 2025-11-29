package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;

public class IntVariable extends AbstractVariable<Integer, IntegerArgument, IntVariable> {

    public IntVariable(String name) {
        super(name, 0);
    }

    public IntVariable(String name, int defaultValue) {
        super(name, defaultValue);
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
}
