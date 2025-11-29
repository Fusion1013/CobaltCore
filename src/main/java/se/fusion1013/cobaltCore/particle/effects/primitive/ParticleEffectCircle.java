package se.fusion1013.cobaltCore.particle.effects.primitive;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.particle.effects.AbstractParticleEffect;
import se.fusion1013.cobaltCore.particle.effects.IParticleEffect;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;
import se.fusion1013.cobaltCore.shape.ShapeUtils;

/** Generates particles in the shape of a circle. */
public class ParticleEffectCircle extends AbstractParticleEffect implements IParticleEffect {

  private double radius;
  private int iterations;

  /**
   * Constructor.
   *
   * @param particle the particle.
   */
  public ParticleEffectCircle(Particle particle) {
    super(particle, new TransformationPipeline());
  }

  /**
   * Constructor.
   *
   * @param particle the particle.
   * @param transformationPipeline transformation pipeline.
   * @param radius radius of the circle.
   * @param iterations number of points along the circle.
   */
  public ParticleEffectCircle(
      Particle particle,
      TransformationPipeline transformationPipeline,
      double radius,
      int iterations) {
    super(particle, transformationPipeline);
    this.radius = radius;
    this.iterations = iterations;
  }

  @Override
  public List<Vector> getPoints(Location center) {
    return ShapeUtils.generateCircle(radius, iterations);
  }

  @Override
  public List<Argument> getModifyArguments() {
    List<Argument> arguments = super.getModifyArguments();
    arguments.add(new DoubleArgument("radius"));
    arguments.add(new IntegerArgument("iterations"));
    return arguments;
  }

  @Override
  public void modify(CommandArguments arguments) {
    super.modify(arguments);
    radius = (double) arguments.get("radius");
    iterations = (int) arguments.get("iterations");
  }

  @Override
  public void display(Location center, Player player) {
    display(center, player, particle, getPoints());
  }

  @Override
  public List<Vector> getPoints() {
    return ShapeUtils.generateCircle(radius, iterations);
  }

  @Override
  public String getName() {
    return "circle";
  }

  @Override
  public IParticleEffect copy() {
    return new ParticleEffectCircle(particle, transformationPipeline, radius, iterations);
  }
}
