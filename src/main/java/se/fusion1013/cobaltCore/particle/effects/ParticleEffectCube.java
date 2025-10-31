package se.fusion1013.cobaltCore.particle.effects;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.particle.ParticleShapeUtils;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;
import se.fusion1013.cobaltCore.util.StringPlaceholders;

import java.util.List;

public class ParticleEffectCube extends AbstractParticleEffect implements IParticleEffect {

    private double size;
    private int density;

    public ParticleEffectCube(Particle particle, double size, int density) {
        this(new TransformationPipeline(), particle, size, density);
    }

    public ParticleEffectCube(TransformationPipeline pipeline, Particle particle, double size, int density) {
        super(particle, pipeline);
        this.size = size;
        this.density = density;
    }

    @Override
    public void display(Location center, Player player) {
        List<Vector> points = ParticleShapeUtils.generateCube(size, density);
        display(center, player, particle, points);
    }

    @Override
    public List<Vector> getPoints() {
        return ParticleShapeUtils.generateCube(size, density);
    }

    @Override
    public String getName() {
        return "cube";
    }

    @Override
    public List<String> getInfoStrings() {
        List<String> info = super.getInfoStrings();
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("size", size)
                .addPlaceholder("density", density)
                .build();
        info.add(LocaleManager.getInstance().getLocaleMessage("particle.style.cube.info", placeholders));
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
    public void modify(String key, Object value) {
        super.modify(key, value);
        switch (key) {
            case "size" -> size = (double) value;
            case "density" -> density = (int) value;

        }
    }

    @Override
    public IParticleEffect copy() {
        return new ParticleEffectCube(transformationPipeline, particle, size, density);
    }
}
