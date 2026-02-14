package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.PotionEffectArgument;
import org.bukkit.potion.PotionEffectType;
import se.fusion1013.cobaltCore.variable.provider.PotionTypeValueProvider;

public class PotionTypeVariable extends AbstractVariable<PotionEffectType, PotionTypeValueProvider, PotionEffectArgument, PotionTypeVariable> {

    public PotionTypeVariable(String name) {
        super(name, new PotionTypeValueProvider(name));
    }

    public PotionTypeVariable(String name, PotionEffectType defaultValue) {
        super(name, new PotionTypeValueProvider(name, defaultValue));
    }

    @Override
    public PotionTypeVariable getSelf() {
        return this;
    }

    @Override
    public PotionEffectArgument getArgument() {
        PotionEffectArgument argument = new PotionEffectArgument(getName());
        argument.setOptional(optionalArgument);
        if (argumentSuggestions != null)
            argument.replaceSuggestions(ArgumentSuggestions.strings(argumentSuggestions.get()));
        return argument;
    }
}
