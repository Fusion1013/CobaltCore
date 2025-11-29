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

public class ParticleEffectBezier extends AbstractParticleEffect implements IParticleEffect {

  private Location startLocation;
  private Vector startDirection = new Vector();
  private Location endLocation;
  private Vector endDirection = new Vector();
  private int density;

  public ParticleEffectBezier(Particle particle) {
    super(particle, new TransformationPipeline());
  }

  public ParticleEffectBezier(
      Particle particle,
      TransformationPipeline transformationPipeline,
      Location startLocation,
      Vector startDirection,
      Location endLocation,
      Vector endDirection,
      int density) {
    super(particle, transformationPipeline);
    this.startLocation = startLocation;
    this.startDirection = startDirection;
    this.endLocation = endLocation;
    this.endDirection = endDirection;
    this.density = density;
  }

  @Override
  public void display(Location center, Player player) {
    display(new Location(center.getWorld(), 0, 0, 0), player, particle, getPoints());
  }

  @Override
  public List<Vector> getPoints() {
    return BezierUtil.bezierCurve(
        startLocation.toVector(), startDirection, endDirection, endLocation.toVector(), density);
  }

  @Override
  public void modify(CommandArguments arguments) {
    super.modify(arguments);
    startLocation = (Location) arguments.get("start_location");
    startDirection.setX((double) arguments.get("start_direction_x"));
    startDirection.setY((double) arguments.get("start_direction_y"));
    startDirection.setZ((double) arguments.get("start_direction_z"));
    endLocation = (Location) arguments.get("end_location");
    endDirection.setX((double) arguments.get("end_direction_x"));
    endDirection.setY((double) arguments.get("end_direction_y"));
    endDirection.setZ((double) arguments.get("end_direction_z"));
    density = (int) arguments.get("density");
  }

  @Override
  public List<Argument> getModifyArguments() {
    List<Argument> arguments = super.getModifyArguments();
    arguments.add(new LocationArgument("start_location"));
    arguments.add(new DoubleArgument("start_direction_x"));
    arguments.add(new DoubleArgument("start_direction_y"));
    arguments.add(new DoubleArgument("start_direction_z"));
    arguments.add(new LocationArgument("end_location"));
    arguments.add(new DoubleArgument("end_direction_x"));
    arguments.add(new DoubleArgument("end_direction_y"));
    arguments.add(new DoubleArgument("end_direction_z"));
    arguments.add(new IntegerArgument("density"));
    return arguments;
  }

  @Override
  public String getName() {
    return "bezier";
  }

  @Override
  public IParticleEffect copy() {
    return new ParticleEffectBezier(
        particle,
        transformationPipeline,
        startLocation,
        startDirection,
        endLocation,
        endDirection,
        density);
  }
}
