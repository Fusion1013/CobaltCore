package se.fusion1013.cobaltCore.particle.effects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.commands.system.ICommandValue;
import se.fusion1013.cobaltCore.particle.transformation.ScalingTransformation;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;
import se.fusion1013.cobaltCore.shape.tesseract.Tesseract;
import se.fusion1013.cobaltCore.variable.DoubleVariable;
import se.fusion1013.cobaltCore.variable.IntVariable;

import java.util.List;

public class ParticleEffectTesseract extends AbstractParticleEffect implements IParticleEffect {

    private final DoubleVariable width = new DoubleVariable("width");
    private final IntVariable density = new IntVariable("density");
    private final DoubleVariable wGap = new DoubleVariable("wGap");
    private final DoubleVariable xVelocity = new DoubleVariable("xVelocity");
    private final DoubleVariable yVelocity = new DoubleVariable("yVelocity");
    private final DoubleVariable zVelocity = new DoubleVariable("zVelocity");
    private final DoubleVariable wVelocity = new DoubleVariable("wVelocity");

    public ParticleEffectTesseract(Particle particle) {
        super("tesseract", particle, new TransformationPipeline().add(new ScalingTransformation(10)));
    }

    @Override
    public void display(Location center, Player player) {
        display(center, player, particle.getParticle(), getPoints());
    }

    @Override
    public List<Vector> getPoints() {
        double time = System.currentTimeMillis();
        return Tesseract.generateTesseract(
                width.getValue(),
                wGap.getValue(),
                density.getValue(),
                time * xVelocity.getValue(),
                time * yVelocity.getValue(),
                time * zVelocity.getValue(),
                time * wVelocity.getValue());
    }

    @Override
    public List<Vector> getPoints(Location center) {
        double time = System.currentTimeMillis();
        return Tesseract.generateTesseract(
                width.getValue(),
                wGap.getValue(),
                density.getValue(),
                time * xVelocity.getValue(),
                time * yVelocity.getValue(),
                time * zVelocity.getValue(),
                time * wVelocity.getValue());
    }

    @Override
    public ICommandValue[] getValues() {
        return new ICommandValue[]{particle, width, density, wGap, xVelocity, yVelocity, zVelocity, wVelocity};
    }
}
