package se.fusion1013.cobaltCore.particle.effects.factory;

import org.json.simple.JSONObject;

public abstract class ParticleEffectFactory<T> {

    public abstract T create();

    public abstract T create(JSONObject json);

    public abstract String getName();

}
