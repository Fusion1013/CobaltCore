package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.Argument;
import se.fusion1013.cobaltCore.components.actions.IAction;
import se.fusion1013.cobaltCore.variable.provider.ActionValueProvider;

import java.util.List;

public class ActionVariable extends AbstractVariable<List<IAction>, ActionValueProvider, Argument<List<IAction>>, ActionVariable> {

    public ActionVariable(String name) {
        super(name, new ActionValueProvider(name));
    }

    public ActionVariable(String name, List<IAction> defaultValue) {
        super(name, new ActionValueProvider(name, defaultValue));
    }

    @Override
    public ActionVariable getSelf() {
        return this;
    }

    @Override
    public Argument<List<IAction>> getArgument() {
        return null;
    }
}
