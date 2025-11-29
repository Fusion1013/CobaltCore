package se.fusion1013.cobaltCore.particle.effects.glyph;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.particle.effects.AbstractParticleEffect;
import se.fusion1013.cobaltCore.particle.effects.IParticleEffect;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;
import se.fusion1013.cobaltCore.util.FileUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ParticleEffectGlyph extends AbstractParticleEffect implements IParticleEffect {

    private double scale;
    private String glyph;
    private GlyphData glyphData;



    public ParticleEffectGlyph(Particle particle) {
        super(particle, new TransformationPipeline());
    }

    public ParticleEffectGlyph(Particle particle, double scale, String glyph) {
        super(particle, new TransformationPipeline());
        this.scale = scale;
        this.glyph = glyph;
        this.glyphData = GlyphManager.getGlyphFromName(glyph);
    }

    @Override
    public List<Argument> getModifyArguments() {
        List<Argument> arguments = super.getModifyArguments();

        arguments.add(new StringArgument("glyph").replaceSuggestions(ArgumentSuggestions.strings(GlyphManager.getGlyphNamespaces())));
        arguments.add(new DoubleArgument("scale"));

        return arguments;
    }

    @Override
    public void modify(CommandArguments arguments) {
        super.modify(arguments);
        glyph = (String) arguments.get("glyph");
        glyphData = GlyphManager.getGlyphFromName(glyph);
        scale = (double) arguments.get("scale");
    }

    @Override
    public void display(Location center, Player player) {
        display(center, player, particle, getPoints().stream().map(p -> new Vector(p.getX() * scale, p.getY() * scale, p.getZ() * scale)).toList());
    }

    @Override
    public List<Vector> getPoints() {
        if (glyphData == null) return List.of();
        return glyphData.positions();
    }

    @Override
    public String getName() {
        return "glyph";
    }

    @Override
    public IParticleEffect copy() {
        return new ParticleEffectGlyph(particle, scale, glyph);
    }
}
