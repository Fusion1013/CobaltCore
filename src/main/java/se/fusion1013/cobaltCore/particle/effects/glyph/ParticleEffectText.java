package se.fusion1013.cobaltCore.particle.effects.glyph;

import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.particle.effects.AbstractParticleEffect;
import se.fusion1013.cobaltCore.particle.effects.IParticleEffect;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;
import se.fusion1013.cobaltCore.util.VectorUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates particles in the shape of text. Uses the given alphabet.
 */
public class ParticleEffectText extends AbstractParticleEffect implements IParticleEffect {

    private String alphabet;
    private String text;
    private double scale;
    private double spacing;
    private double randomOffset;
    private double wiggleSpeed;

    private final List<GlyphData> glyphs = new ArrayList<>();
    private final List<Vector> points = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param particle the particle to use for the effect.
     */
    public ParticleEffectText(Particle particle) {
        super(particle, new TransformationPipeline());
        calculatePoints();
    }

    /**
     * Constructor.
     *
     * @param particle the particle to use for the effect.
     * @param spacing  the spacing between the characters.
     * @param text     the text to display.
     */
    public ParticleEffectText(Particle particle, double spacing, String text, double randomOffset, double wiggleSpeed) {
        super(particle, new TransformationPipeline());
        this.spacing = spacing;
        this.text = text;
        this.randomOffset = randomOffset;
        this.wiggleSpeed = wiggleSpeed;
        calculatePoints();
    }

    @Override
    public void display(Location center, Player player) {
        display(center, player, particle, getPoints());
    }

    @Override
    public List<Vector> getPoints() {
        return points;
    }

    private void calculatePoints() {
        glyphs.clear();
        points.clear();
        if (text == null) return;

        String[] splitText = text.split("&n");
        double offset = spacing * splitText.length / 2f;

        for (int i = 0; i < splitText.length; i++) {
            String textSegment = splitText[i];
            points.addAll(calculatePoints(textSegment, i * spacing - offset));
        }
    }

    private List<Vector> calculatePoints(String textSegment, double yOffset) {
        if (textSegment == null) return List.of();
        double xOffset = spacing * textSegment.length() / 2f;

        List<Vector> newPoints = new ArrayList<>();

        for (int i = 0; i < textSegment.length(); i++) {
            char c = textSegment.charAt(i);
            newPoints.addAll(calculatePoints(c, i * spacing - xOffset, yOffset));
        }
        return newPoints;
    }

    private List<Vector> calculatePoints(char character, double xOffset, double yOffset) {
        GlyphData glyphData = GlyphManager.getGlyph(alphabet, character);
        if (glyphData == null) return List.of();
        Vector posOffset = VectorUtil.deterministicRandomDirection(xOffset, yOffset, 0).multiply(randomOffset);
        posOffset = posOffset.multiply(Math.sin(System.currentTimeMillis() * wiggleSpeed + xOffset + yOffset));
        final Vector extraOffset = posOffset;
        return glyphData.positions().stream().map(p -> new Vector(p.getX() * scale + xOffset + extraOffset.getX(), p.getY() * scale - yOffset + extraOffset.getY(), p.getZ() * scale)).toList();
    }

    @Override
    public List<Argument> getModifyArguments() {
        List<Argument> arguments = super.getModifyArguments();
        arguments.add(
                new StringArgument("alphabet")
                        .replaceSuggestions(ArgumentSuggestions.strings(GlyphManager.getAlphabetNames())));
        arguments.add(new DoubleArgument("scale"));
        arguments.add(new DoubleArgument("spacing"));
        arguments.add(new DoubleArgument("random_offset"));
        arguments.add(new DoubleArgument("wiggle_speed"));
        arguments.add(new GreedyStringArgument("text"));
        return arguments;
    }

    @Override
    public void modify(CommandArguments arguments) {
        super.modify(arguments);
        alphabet = (String) arguments.get("alphabet");
        scale = (double) arguments.get("scale");
        spacing = (double) arguments.get("spacing");
        randomOffset = (double) arguments.get("random_offset");
        wiggleSpeed = (double) arguments.get("wiggle_speed");
        text = (String) arguments.get("text");
        calculatePoints();
    }

    @Override
    public String getName() {
        return "text";
    }

    @Override
    public IParticleEffect copy() {
        return new ParticleEffectText(particle, spacing, text, randomOffset, wiggleSpeed);
    }
}
