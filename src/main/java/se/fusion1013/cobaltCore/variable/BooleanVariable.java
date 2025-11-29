package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.BooleanArgument;

public class BooleanVariable extends AbstractVariable<Boolean, BooleanArgument, BooleanVariable> {

    public BooleanVariable(String name) {
        super(name, false);
    }

    public BooleanVariable(String name, Boolean defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public BooleanVariable getSelf() {
        return this;
    }

    @Override
    public BooleanArgument getArgument() {
        BooleanArgument argument = new BooleanArgument(getName());
        argument.setOptional(optionalArgument);
        if (argumentSuggestions != null)
            argument.replaceSuggestions(ArgumentSuggestions.strings(argumentSuggestions.get()));
        return argument;
    }
}
