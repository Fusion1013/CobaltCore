package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.SoundArgument;
import org.bukkit.Sound;
import se.fusion1013.cobaltCore.variable.provider.SoundValueProvider;

public class SoundVariable extends AbstractVariable<Sound, SoundValueProvider, SoundArgument, SoundVariable> {

    public SoundVariable(String name, Sound defaultValue) {
        super(name, new SoundValueProvider(name, defaultValue));
    }

    public SoundVariable(String name) {
        super(name, new SoundValueProvider(name));
    }

    @Override
    public SoundVariable getSelf() {
        return this;
    }

    @Override
    public SoundArgument getArgument() {
        return null;
    }
}
