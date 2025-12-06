package se.fusion1013.cobaltCore.particle.effects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.commands.system.ICommandValue;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;
import se.fusion1013.cobaltCore.shape.ShapeUtils;
import se.fusion1013.cobaltCore.variable.DoubleVariable;
import se.fusion1013.cobaltCore.variable.IntVariable;

import java.util.List;

public class ParticleEffectCube extends AbstractParticleEffect implements IParticleEffect {

    private final DoubleVariable size = new DoubleVariable("size");
    private final IntVariable density = new IntVariable("density");

    public ParticleEffectCube(Particle particle) {
        super("cube", particle, new TransformationPipeline());
    }

    @Override
    public void display(Location center, Player player, TransformationPipeline extraPipeline) {
        List<Vector> points = ShapeUtils.generateCube(size.getValue(), density.getValue());
        display(center, player, particle.getParticle(), points, extraPipeline);
    }

    @Override
    public List<Vector> getPoints() {
        return ShapeUtils.generateCube(size.getValue(), density.getValue());
    }

    @Override
    public String getName() {
        return "cube";
    }

    @Override
    public ICommandValue[] getValues() {
        return new ICommandValue[]{particle, size, density};
    }
}
