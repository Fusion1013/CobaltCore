package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.Argument;
import se.fusion1013.cobaltCore.components.actions.IAction;
import se.fusion1013.cobaltCore.variable.provider.ActionValueProvider;

public class ActionVariable extends AbstractVariable<IAction, ActionValueProvider, Argument<IAction>, ActionVariable> {

    public ActionVariable(String name) {
        super(name, new ActionValueProvider(name));
    }

    public ActionVariable(String name, IAction defaultValue) {
        super(name, new ActionValueProvider(name, defaultValue));
    }

    @Override
    public ActionVariable getSelf() {
        return this;
    }

    @Override
    public Argument<IAction> getArgument() {
        return null;
    }
}
