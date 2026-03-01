package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.Argument;
import se.fusion1013.cobaltCore.components.conditions.ICondition;
import se.fusion1013.cobaltCore.variable.provider.ConditionValueProvider;

public class ConditionVariable extends AbstractVariable<ICondition, ConditionValueProvider, Argument<ICondition>, ConditionVariable> {

    public ConditionVariable(String name) {
        super(name, new ConditionValueProvider(name));
    }

    public ConditionVariable(String name, ICondition defaultValue) {
        super(name, new ConditionValueProvider(name, defaultValue));
    }

    @Override
    public ConditionVariable getSelf() {
        return this;
    }

    @Override
    public Argument<ICondition> getArgument() {
        return null;
    }
}
