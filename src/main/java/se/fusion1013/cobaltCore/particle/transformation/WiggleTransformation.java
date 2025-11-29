package se.fusion1013.cobaltCore.particle.transformation;

import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.shape.ShapeUtils;

public class WiggleTransformation implements IParticleTransformation {

  private final double amplitude = 0.4;
  private final double frequency = 1.2;

  @Override
  public Vector apply(Vector input) {
    return ShapeUtils.applyPositionWiggle(amplitude, frequency, input, System.currentTimeMillis());
  }
}
