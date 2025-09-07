package se.fusion1013.cobaltCore.particle.transformation;

import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class TransformationPipeline {

    private final List<IParticleTransformation> transformations = new ArrayList<>();

    public TransformationPipeline add(IParticleTransformation t) {
        transformations.add(t);
        return this;
    }

    public Vector apply(Vector input) {
        Vector result = input.clone();
        for (IParticleTransformation t : transformations) {
            result = t.apply(result);
        }
        return result;
    }

}
