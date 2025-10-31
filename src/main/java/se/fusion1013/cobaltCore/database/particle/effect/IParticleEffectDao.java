package se.fusion1013.cobaltCore.database.particle.effect;

import se.fusion1013.cobaltCore.database.system.IDao;
import se.fusion1013.cobaltCore.particle.effects.IParticleEffect;

import java.util.List;
import java.util.Map;

public interface IParticleEffectDao extends IDao {

    void removeParticleEffect(String styleName);
    void insertParticleEffects(List<IParticleEffect> effects);
    Map<String, IParticleEffect> getParticleEffects();

    @Override
    default String getId() { return "particle_effect"; }
}
