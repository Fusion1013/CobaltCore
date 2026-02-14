package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.ParticleArgument;
import dev.jorel.commandapi.wrappers.ParticleData;
import org.bukkit.Particle;
import se.fusion1013.cobaltCore.variable.provider.ParticleValueProvider;

public class ParticleVariable extends AbstractVariable<ParticleData, ParticleValueProvider, ParticleArgument, ParticleVariable> {

    public ParticleVariable(String name, Particle particle) {
        super(name, new ParticleValueProvider(name, new ParticleData(particle, null)));
    }

    public ParticleVariable(String name) {
        super(name, new ParticleValueProvider(name, new ParticleData(Particle.CRIT, null)));
    }

    public ParticleVariable(String name, ParticleData defaultValue) {
        super(name, new ParticleValueProvider(name, defaultValue));
    }

    @Override
    public ParticleVariable getSelf() {
        return this;
    }

    public void setParticle(Particle particle) {
        valueGetter.setValue(new ParticleData(particle, null));
    }

    public Particle getParticle() {
        return getValue().particle();
    }

    public Object getData() {
        return getValue().data();
    }

    @Override
    public ParticleArgument getArgument() {
        ParticleArgument argument = new ParticleArgument(getName());
        argument.setOptional(optionalArgument);
        if (argumentSuggestions != null)
            argument.replaceSuggestions(ArgumentSuggestions.strings(argumentSuggestions.get()));
        return argument;
    }
}
