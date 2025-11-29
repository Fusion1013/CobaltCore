package se.fusion1013.cobaltCore.particle.effects.factory;

import org.bukkit.Particle;
import org.json.simple.JSONObject;
import se.fusion1013.cobaltCore.particle.effects.ParticleEffectCircle;

public class ParticleEffectCircleFactory extends ParticleEffectFactory<ParticleEffectCircle> {

    @Override
    public ParticleEffectCircle create() {
        return new ParticleEffectCircle(Particle.END_ROD);
    }

    @Override
    public ParticleEffectCircle create(JSONObject json) {
        String particleName = (String) json.get("particle");

        Particle particle = Particle.valueOf(particleName);

        return new ParticleEffectCircle(particle);
    }

    @Override
    public String getName() {
        return "circle";
    }
}
