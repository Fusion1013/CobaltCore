package se.fusion1013.cobaltCore.particle.effects;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.particle.transformation.ScalingTransformation;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;
import se.fusion1013.cobaltCore.shape.tesseract.Tesseract;

public class ParticleEffectTesseract extends AbstractParticleEffect implements IParticleEffect {

  private double width;
  private int density;
  private double wGap;
  private double xVelocity;
  private double yVelocity;
  private double zVelocity;
  private double wVelocity;

  public ParticleEffectTesseract(Particle particle) {
    super(particle, new TransformationPipeline().add(new ScalingTransformation(10)));
  }

  public ParticleEffectTesseract(
      Particle particle, TransformationPipeline transformationPipeline, double width, int density) {
    super(particle, transformationPipeline);
    this.width = width;
    this.density = density;
  }

  @Override
  public List<Argument> getModifyArguments() {
    List<Argument> arguments = super.getModifyArguments();
    arguments.add(new DoubleArgument("width"));
    arguments.add(new IntegerArgument("density"));
    arguments.add(new DoubleArgument("w_gap"));
    arguments.add(new DoubleArgument("x_velocity"));
    arguments.add(new DoubleArgument("y_velocity"));
    arguments.add(new DoubleArgument("z_velocity"));
    arguments.add(new DoubleArgument("w_velocity"));
    return arguments;
  }

  @Override
  public void modify(CommandArguments arguments) {
    super.modify(arguments);
    width = (double) arguments.get("width");
    density = (int) arguments.get("density");
    wGap = (double) arguments.get("w_gap");
    xVelocity = (double) arguments.get("x_velocity");
    yVelocity = (double) arguments.get("y_velocity");
    zVelocity = (double) arguments.get("z_velocity");
    wVelocity = (double) arguments.get("w_velocity");
  }

  @Override
  public void display(Location center, Player player) {
    display(center, player, particle, getPoints());
  }

  @Override
  public List<Vector> getPoints() {
    double time = System.currentTimeMillis();
    return Tesseract.generateTesseract(
        width,
        wGap,
        density,
        time * xVelocity,
        time * yVelocity,
        time * zVelocity,
        time * wVelocity);
  }

  @Override
  public List<Vector> getPoints(Location center) {
    double time = System.currentTimeMillis();
    return Tesseract.generateTesseract(
        width,
        wGap,
        density,
        time * xVelocity,
        time * yVelocity,
        time * zVelocity,
        time * wVelocity);
  }

  @Override
  public String getName() {
    return "tesseract";
  }

  @Override
  public IParticleEffect copy() {
    return new ParticleEffectTesseract(particle, transformationPipeline, width, density);
  }
}
