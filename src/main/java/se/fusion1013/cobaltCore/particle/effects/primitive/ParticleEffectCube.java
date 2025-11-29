package se.fusion1013.cobaltCore.particle.effects.primitive;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.particle.effects.AbstractParticleEffect;
import se.fusion1013.cobaltCore.particle.effects.IParticleEffect;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;
import se.fusion1013.cobaltCore.shape.ShapeUtils;
import se.fusion1013.cobaltCore.util.StringPlaceholders;

public class ParticleEffectCube extends AbstractParticleEffect implements IParticleEffect {

  private double size;
  private int density;

  public ParticleEffectCube(Particle particle, double size, int density) {
    this(new TransformationPipeline(), particle, size, density);
  }

  public ParticleEffectCube(
      TransformationPipeline pipeline, Particle particle, double size, int density) {
    super(particle, pipeline);
    this.size = size;
    this.density = density;
  }

  @Override
  public void display(Location center, Player player) {
    List<Vector> points = ShapeUtils.generateCube(size, density);
    display(center, player, particle, points);
  }

  @Override
  public List<Vector> getPoints() {
    return ShapeUtils.generateCube(size, density);
  }

  @Override
  public String getName() {
    return "cube";
  }

  @Override
  public List<String> getInfoStrings() {
    List<String> info = new ArrayList<>();
    StringPlaceholders placeholders =
        StringPlaceholders.builder()
            .addPlaceholder("size", size)
            .addPlaceholder("density", density)
            .build();
    info.add(
        LocaleManager.getInstance().getLocaleMessage("particle.style.cube.info", placeholders));
    return info;
  }

  @Override
  public List<Argument> getModifyArguments() {
    List<Argument> arguments = super.getModifyArguments();

    arguments.add(new DoubleArgument("size"));
    arguments.add(new IntegerArgument("density"));

    return arguments;
  }

  @Override
  public void modify(Object[] args) {
    super.modify(args);
  }

  @Override
  public void modify(String key, Object value) {
    super.modify(key, value);
    switch (key) {
      case "size" -> size = (double) value;
      case "density" -> density = (int) value;
    }
  }

  @Override
  public void modify(CommandArguments arguments) {
    super.modify(arguments);
    size = (double) arguments.get("size");
    density = (int) arguments.get("density");
  }

  @Override
  public IParticleEffect copy() {
    return new ParticleEffectCube(transformationPipeline, particle, size, density);
  }
}
