package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.DoubleArgument;
import se.fusion1013.cobaltCore.variable.provider.DoubleValueProvider;

public class DoubleVariable extends AbstractVariable<Double, DoubleValueProvider, DoubleArgument, DoubleVariable> {

    public DoubleVariable(String name) {
        super(name, new DoubleValueProvider(name, 0D));
    }

    public DoubleVariable(String name, double defaultValue) {
        super(name, new DoubleValueProvider(name, defaultValue));
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
