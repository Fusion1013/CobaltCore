package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.TextArgument;

public class StringVariable extends AbstractVariable<String, TextArgument, StringVariable> {

    public StringVariable(String name) {
        super(name, "");
    }

    public StringVariable(String name, String defaultValue) {
        super(name, defaultValue);
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
