package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.StringArgument;
import se.fusion1013.cobaltCore.variable.provider.LiteralValueProvider;

public class LiteralVariable extends AbstractVariable<String, LiteralValueProvider, StringArgument, LiteralVariable> {

    public LiteralVariable(String name, String[] allowed) {
        super(name, new LiteralValueProvider(name, allowed));
    }

    public LiteralVariable(String name, String defaultValue, String[] allowed) {
        super(name, new LiteralValueProvider(name, defaultValue, allowed));
    }

    @Override
    public LiteralVariable getSelf() {
        return this;
    }

    @Override
    public StringArgument getArgument() {
        return new StringArgument(getName());
    }
}
