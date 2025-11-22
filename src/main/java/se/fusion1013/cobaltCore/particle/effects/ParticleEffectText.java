package se.fusion1013.cobaltCore.particle.effects;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;

import java.util.ArrayList;
import java.util.List;

public class ParticleEffectText extends AbstractParticleEffect implements IParticleEffect {

    private String text;
    private double scale;
    private double spacing;

    private final List<GlyphData> glyphs = new ArrayList<>();
    private final List<Vector> points = new ArrayList<>();

    public ParticleEffectText(Particle particle) {
        super(particle, new TransformationPipeline());
        calculatePoints();
    }

    public ParticleEffectText(Particle particle, double spacing, String text) {
        super(particle, new TransformationPipeline());
        this.spacing = spacing;
        this.text = text;
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
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            GlyphData glyphData = ParticleEffectGlyph.getGlyphFromName("aether_rune." + c);
            if (glyphData == null) {
                glyphs.add(new GlyphData("", "", List.of(), spacing * scale));
            } else {
                glyphs.add(glyphData);
            }
        }

        final double totalWidth = glyphs.stream().map(GlyphData::width).mapToDouble(Double::doubleValue).sum() + (glyphs.size() - 1) * spacing;

        for (int i = 0; i < glyphs.size(); i++) {
            final int count = i;
            GlyphData glyphData = glyphs.get(i);
            if (glyphData == null) continue;
            glyphData.positions().forEach(p -> points.add(new Vector((p.getX() * scale + spacing * count) - (totalWidth * scale / 2f), p.getY() * scale, p.getZ() * scale)));
        }
    }

    @Override
    public List<Argument> getModifyArguments() {
        List<Argument> arguments = super.getModifyArguments();
        arguments.add(new DoubleArgument("scale"));
        arguments.add(new DoubleArgument("spacing"));
        arguments.add(new GreedyStringArgument("text"));
        return arguments;
    }

    @Override
    public void modify(CommandArguments arguments) {
        super.modify(arguments);
        scale = (double) arguments.get("scale");
        spacing = (double) arguments.get("spacing");
        text = (String) arguments.get("text");
        calculatePoints();
    }

    @Override
    public String getName() {
        return "text";
    }

    @Override
    public IParticleEffect copy() {
        return new ParticleEffectText(particle, spacing, text);
    }
}
