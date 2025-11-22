package se.fusion1013.cobaltCore.particle.transformation;

import org.bukkit.util.Vector;

public class TranslationTransformation implements IParticleTransformation {

    private final Vector offset;

    public TranslationTransformation(Vector offset) {
        this.offset = offset;
    }

    @Override
    public Vector apply(Vector input) {
        return input.clone().add(offset);
    }
}
