package se.fusion1013.cobaltCore.database.particle.effect;

import se.fusion1013.cobaltCore.database.system.DataStorageType;
import se.fusion1013.cobaltCore.particle.effects.IParticleEffect;

import java.util.List;
import java.util.Map;

public class ParticleEffectDaoSQLite implements IParticleEffectDao {
    @Override
    public void removeParticleEffect(String styleName) {

    }

    @Override
    public void insertParticleEffects(List<IParticleEffect> effects) {

    }

    @Override
    public Map<String, IParticleEffect> getParticleEffects() {
        return Map.of();
    }

    @Override
    public DataStorageType getDataStorageType() {
        return null;
    }

    @Override
    public void init() {

    }
}
