package se.fusion1013.cobaltCore.particle.effects;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;
import se.fusion1013.cobaltCore.shape.ShapeUtils;

public class ParticleEffectSpirograph extends AbstractParticleEffect implements IParticleEffect {

  private double fixedCircleRadius;
  private double rollingCircleRadius;
  private double drawingPointOffset;
  private int points;
  private boolean epitrochoid;
  private double animationSpeed;
  private double height;

  public ParticleEffectSpirograph(Particle particle) {
    super(particle, new TransformationPipeline());
  }

  public ParticleEffectSpirograph(
      Particle particle,
      TransformationPipeline transformationPipeline,
      double fixedCircleRadius,
      double rollingCircleRadius,
      double drawingPointOffset,
      int points,
      boolean epitrochoid,
      double animationSpeed,
      double height) {
    super(particle, transformationPipeline);
    this.fixedCircleRadius = fixedCircleRadius;
    this.rollingCircleRadius = rollingCircleRadius;
    this.drawingPointOffset = drawingPointOffset;
    this.points = points;
    this.epitrochoid = epitrochoid;
    this.animationSpeed = animationSpeed;
    this.height = height;
  }

  @Override
  public void display(Location center, Player player) {
    display(center, player, particle, getPoints());
  }

  @Override
  public List<Vector> getPoints() {
    return ShapeUtils.generateSpirograph(
        fixedCircleRadius,
        rollingCircleRadius + Math.sin(System.currentTimeMillis() * animationSpeed),
        drawingPointOffset,
        points,
        epitrochoid,
        animationSpeed * System.currentTimeMillis(),
        height);
  }

  @Override
  public void modify(CommandArguments arguments) {
    super.modify(arguments);
    fixedCircleRadius = (double) arguments.get("fixed_circle_radius");
    rollingCircleRadius = (double) arguments.get("rolling_circle_radius");
    drawingPointOffset = (double) arguments.get("drawing_point_offset");
    points = (int) arguments.get("points");
    epitrochoid = (boolean) arguments.get("epitrochoid");
    animationSpeed = (double) arguments.get("animation_speed");
    height = (double) arguments.get("height");
  }

  @Override
  public List<Argument> getModifyArguments() {
    List<Argument> arguments = super.getModifyArguments();
    arguments.add(new DoubleArgument("fixed_circle_radius"));
    arguments.add(new DoubleArgument("rolling_circle_radius"));
    arguments.add(new DoubleArgument("drawing_point_offset"));
    arguments.add(new IntegerArgument("points"));
    arguments.add(new BooleanArgument("epitrochoid"));
    arguments.add(new DoubleArgument("animation_speed"));
    arguments.add(new DoubleArgument("height"));
    return arguments;
  }

  @Override
  public String getName() {
    return "spirograph";
  }

  @Override
  public IParticleEffect copy() {
    return new ParticleEffectSpirograph(
        particle,
        transformationPipeline,
        fixedCircleRadius,
        rollingCircleRadius,
        drawingPointOffset,
        points,
        epitrochoid,
        animationSpeed,
        height);
  }
}
