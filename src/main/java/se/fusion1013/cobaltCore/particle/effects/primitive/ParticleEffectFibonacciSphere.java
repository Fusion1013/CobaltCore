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

public class ParticleEffectFibonacciSphere extends AbstractParticleEffect
    implements IParticleEffect {

  private double radius;
  private int density;

  public ParticleEffectFibonacciSphere(Particle particle, double radius, int density) {
    this(particle, new TransformationPipeline(), radius, density);
  }

  public ParticleEffectFibonacciSphere(
      Particle particle,
      TransformationPipeline transformationPipeline,
      double radius,
      int density) {
    super(particle, transformationPipeline);
    this.radius = radius;
    this.density = density;
  }

  @Override
  public void display(Location center, Player player) {
    display(center, player, particle, getPoints());
  }

  @Override
  public List<Vector> getPoints() {
    return ShapeUtils.generateFibonacciSphere(radius, density);
  }

  @Override
  public String getName() {
    return "fibonacci_sphere";
  }

  @Override
  public List<Argument> getModifyArguments() {
    List<Argument> arguments = super.getModifyArguments();

    arguments.add(new DoubleArgument("radius"));
    arguments.add(new IntegerArgument("density"));

    return arguments;
  }

  @Override
  public void modify(String key, Object value) {
    super.modify(key, value);
    switch (key) {
      case "radius" -> radius = (double) value;
      case "density" -> density = (int) value;
    }
  }

  @Override
  public void modify(CommandArguments arguments) {
    super.modify(arguments);
    radius = (double) arguments.get("radius");
    density = (int) arguments.get("density");
  }

  @Override
  public IParticleEffect copy() {
    return new ParticleEffectFibonacciSphere(particle, transformationPipeline, radius, density);
  }
}
