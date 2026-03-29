package se.fusion1013.cobaltCore.particle.effects.glyph;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.commands.system.ICommandValue;
import se.fusion1013.cobaltCore.particle.effects.AbstractParticleEffect;
import se.fusion1013.cobaltCore.particle.effects.IParticleEffect;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;
import se.fusion1013.cobaltCore.util.VectorUtil;
import se.fusion1013.cobaltCore.variable.DoubleVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates particles in the shape of text. Uses the given alphabet.
 */
public class ParticleEffectTextFormatted extends AbstractParticleEffect implements IParticleEffect {

    private final StringVariable text = new StringVariable("text")
            .onValueChange(k -> calculatePoints());
    private final DoubleVariable scale = new DoubleVariable("scale")
            .onValueChange(k -> calculatePoints());
    private final DoubleVariable spacing = new DoubleVariable("spacing")
            .onValueChange(k -> calculatePoints());
    private final DoubleVariable randomOffset = new DoubleVariable("randomOffset")
            .onValueChange(k -> calculatePoints())
            .optional();
    private final DoubleVariable wiggleSpeed = new DoubleVariable("wiggleSpeed")
            .onValueChange(k -> calculatePoints())
            .optional();

    private final List<Vector> points = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param particle the particle to use for the effect.
     */
    public ParticleEffectTextFormatted(Particle particle) {
        super("text_formatted", particle, new TransformationPipeline());
        calculatePoints();
    }

    @Override
    public void display(Location center, Player player, TransformationPipeline extraPipeline) {
        display(center, player, particle.getParticle(), particle.getData(), getPoints(), extraPipeline);
    }

    @Override
    public List<Vector> getPoints() {
        return points;
    }

    private void calculatePoints() {
        points.clear();
        if (text == null) return;

        String[] splitText = text.getValue().split("&n");
        double offset = spacing.getValue() * splitText.length / 2f;

        for (int i = 0; i < splitText.length; i++) {
            String textSegment = splitText[i];
            points.addAll(calculatePoints(textSegment, i * spacing.getValue() - offset));
        }
    }

    private List<Vector> calculatePoints(String textSegment, double yOffset) {
        if (textSegment == null) return List.of();
        double xOffset = spacing.getValue() * (textSegment.split(",").length - 1) / 2f;

        List<Vector> newPoints = new ArrayList<>();

        String[] splitTextSegment = textSegment.split(",");

        for (int i = 0; i < splitTextSegment.length; i++) {
            String segment = splitTextSegment[i];
            newPoints.addAll(calculatePoints(segment, i * spacing.getValue() - xOffset, yOffset));
        }
        return newPoints;
    }

    private List<Vector> calculatePoints(String replace, double xOffset, double yOffset) {
        GlyphData glyphData = GlyphManager.getGlyphFromName(replace);
        if (glyphData == null) return List.of();
        Vector posOffset = VectorUtil.deterministicRandomDirection(xOffset, yOffset, 0).multiply(randomOffset.getValue());
        posOffset = posOffset.multiply(Math.sin(System.currentTimeMillis() * wiggleSpeed.getValue() + xOffset + yOffset));
        final Vector extraOffset = posOffset;
        return glyphData.positions().stream().map(p -> new Vector(p.getX() * scale.getValue() + xOffset + extraOffset.getX(), p.getY() * scale.getValue() - yOffset + extraOffset.getY(), p.getZ() * scale.getValue())).toList();
    }

    @Override
    public ICommandValue[] getValues() {
        return new ICommandValue[]{particle, text, scale, spacing, randomOffset, wiggleSpeed};
    }
}
