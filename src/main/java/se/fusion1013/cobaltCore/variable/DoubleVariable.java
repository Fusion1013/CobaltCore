package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.DoubleArgument;

public class DoubleVariable extends AbstractVariable<Double, DoubleArgument, DoubleVariable> {

    public DoubleVariable(String name) {
        super(name, 0D);
    }

    public DoubleVariable(String name, double defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public DoubleArgument getArgument() {
        DoubleArgument argument = new DoubleArgument(getName());
        argument.setOptional(optionalArgument);
        if (argumentSuggestions != null)
            argument.replaceSuggestions(ArgumentSuggestions.strings(argumentSuggestions.get()));
        return argument;
    }

    @Override
    public DoubleVariable getSelf() {
        return this;
    }
}
