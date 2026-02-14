package se.fusion1013.cobaltCore.variable.provider;

import dev.jorel.commandapi.wrappers.ParticleData;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.yaml.snakeyaml.util.EnumUtils;

import java.util.Map;

public class ParticleValueProvider extends AbstractValueProvider<ParticleData> {

    private ParticleData particleData;

    public ParticleValueProvider(String parameterName) {
        super(parameterName);
    }

    public ParticleValueProvider(String parameterName, ParticleData particleData) {
        super(parameterName);
        this.particleData = particleData;
    }

    @Override
    public ParticleData getValue() {
        return particleData;
    }

    @Override
    public void setValue(ParticleData value) {
        this.particleData = value;
    }

    @Override
    public void load(ConfigurationSection yaml) {

    }

    @Override
    public void load(Map<?, ?> map) {
        if (map.containsKey(parameterName) && map.get(parameterName) instanceof String value) {
            Particle particle = EnumUtils.findEnumInsensitiveCase(Particle.class, value);
            this.particleData = new ParticleData(particle, null);
        }
    }
}
