package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.TextArgument;
import se.fusion1013.cobaltCore.variable.provider.StringValueProvider;

public class StringVariable extends AbstractVariable<String, StringValueProvider, TextArgument, StringVariable> {

    public StringVariable(String name) {
        super(name, new StringValueProvider(name, ""));
    }

    public StringVariable(String name, String defaultValue) {
        super(name, new StringValueProvider(name, defaultValue));
    }

    @Override
    public TextArgument getArgument() {
        TextArgument argument = new TextArgument(getName());
        argument.setOptional(optionalArgument);
        if (argumentSuggestions != null)
            argument.replaceSuggestions(ArgumentSuggestions.strings(argumentSuggestions.get()));
        return argument;
    }

    @Override
    public StringVariable getSelf() {
        return this;
    }
}
