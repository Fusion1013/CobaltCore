package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.Argument;
import se.fusion1013.cobaltCore.util.EnchantmentContainer;
import se.fusion1013.cobaltCore.variable.provider.EnchantmentValueProvider;

public class EnchantmentVariable extends AbstractVariable<EnchantmentContainer, EnchantmentValueProvider, Argument<EnchantmentContainer>, EnchantmentVariable> {

    public EnchantmentVariable(String name) {
        super(name, new EnchantmentValueProvider(name));
    }

    public EnchantmentVariable(String name, EnchantmentContainer defaultValue) {
        super(name, new EnchantmentValueProvider(name, defaultValue));
    }

    @Override
    public EnchantmentVariable getSelf() {
        return this;
    }

    @Override
    public Argument<EnchantmentContainer> getArgument() {
        return null;
    }
}
