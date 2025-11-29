package se.fusion1013.cobaltCore.particle.effects;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;
import se.fusion1013.cobaltCore.shape.BezierUtil;
import se.fusion1013.cobaltCore.shape.ShapeUtils;

public class ParticleEffectSpiralConnector extends AbstractParticleEffect
    implements IParticleEffect {

  private Location startLocation;
  private Location endLocation;
  private Vector direction1 = new Vector();
  private Vector direction2 = new Vector();
  private double radius;
  private double turns;
  private double wiggleAmplitude;
  private double animationSpeed;
  private int points;

  public ParticleEffectSpiralConnector(Particle particle) {
    super(particle, new TransformationPipeline());
  }

  public ParticleEffectSpiralConnector(
      Particle particle,
      TransformationPipeline transformationPipeline,
      Location startLocation,
      Location endLocation,
      Vector direction1,
      Vector direction2,
      double radius,
      double turns,
      double wiggleAmplitude,
      double animationSpeed,
      int points) {
    super(particle, transformationPipeline);
    this.startLocation = startLocation;
    this.endLocation = endLocation;
    this.direction1 = direction1;
    this.direction2 = direction2;
    this.radius = radius;
    this.turns = turns;
    this.wiggleAmplitude = wiggleAmplitude;
    this.animationSpeed = animationSpeed;
    this.points = points;
  }

  @Override
  public void display(Location center, Player player) {
    display(new Location(center.getWorld(), 0, 0, 0), player, particle, getPoints());
  }

  @Override
  public List<Vector> getPoints() {
    double time = System.currentTimeMillis() * animationSpeed;
    return ShapeUtils.spiralAroundPath(
        BezierUtil.bezierCurve(
            startLocation.toVector(), direction1, direction2, endLocation.toVector(), points * 10),
        radius,
        turns,
        time,
        points,
        wiggleAmplitude);
  }

  @Override
  public void modify(CommandArguments arguments) {
    super.modify(arguments);
    startLocation = (Location) arguments.get("start_location");
    endLocation = (Location) arguments.get("end_location");
    direction1.setX((int) arguments.get("direction1_x"));
    direction1.setY((int) arguments.get("direction1_y"));
    direction1.setZ((int) arguments.get("direction1_z"));
    direction2.setX((int) arguments.get("direction2_x"));
    direction2.setY((int) arguments.get("direction2_y"));
    direction2.setZ((int) arguments.get("direction2_z"));
    radius = (double) arguments.get("radius");
    turns = (double) arguments.get("turns");
    wiggleAmplitude = (double) arguments.get("wiggle_amplitude");
    animationSpeed = (double) arguments.get("animation_speed");
    points = (int) arguments.get("points");
  }

  @Override
  public List<Argument> getModifyArguments() {
    List<Argument> arguments = super.getModifyArguments();
    arguments.add(new LocationArgument("start_location"));
    arguments.add(new LocationArgument("end_location"));
    arguments.add(new IntegerArgument("direction1_x"));
    arguments.add(new IntegerArgument("direction1_y"));
    arguments.add(new IntegerArgument("direction1_z"));
    arguments.add(new IntegerArgument("direction2_x"));
    arguments.add(new IntegerArgument("direction2_y"));
    arguments.add(new IntegerArgument("direction2_z"));
    arguments.add(new DoubleArgument("radius"));
    arguments.add(new DoubleArgument("turns"));
    arguments.add(new DoubleArgument("wiggle_amplitude"));
    arguments.add(new DoubleArgument("animation_speed"));
    arguments.add(new IntegerArgument("points"));
    return arguments;
  }

  @Override
  public String getName() {
    return "spiral_connector";
  }

  @Override
  public IParticleEffect copy() {
    return new ParticleEffectSpiralConnector(
        particle,
        transformationPipeline,
        startLocation,
        endLocation,
        direction1,
        direction2,
        radius,
        turns,
        wiggleAmplitude,
        animationSpeed,
        points);
  }
}
