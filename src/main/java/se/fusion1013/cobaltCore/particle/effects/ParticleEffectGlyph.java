package se.fusion1013.cobaltCore.particle.effects;

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
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;
import se.fusion1013.cobaltCore.util.FileUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ParticleEffectGlyph extends AbstractParticleEffect implements IParticleEffect {

    private static final Map<String, GlyphData> GLYPHS = getGlyphs();

    private double scale;
    private String glyph;
    private GlyphData glyphData;

    private static Map<String, GlyphData> getGlyphs() {
        Map<String, GlyphData> glyphDataMap = new HashMap<>();
        String[] glyphs = FileUtil.getResources(CobaltCore.class, "glyphs");
        for (String s : glyphs) {
            GlyphData glyphData = getGlyph(s);
            if (glyphData == null) continue;
            glyphDataMap.put(glyphData.category() + "." + glyphData.name(), glyphData);
        }
        return glyphDataMap;
    }

    private static GlyphData getGlyph(String path) {
        if (!path.endsWith(".png")) return null;

        File glyphFile = FileUtil.getOrCreateFileFromResource(CobaltCore.getInstance(), "glyphs/" + path);

        String[] splitPath = path.split("/");
        String category = splitPath[0];
        String name = splitPath[1].replace(".png", "");

        List<Vector> positions = new ArrayList<>();

        int width = 0;

        try {
            BufferedImage img = ImageIO.read(glyphFile);

            // Image dimensions
            width = img.getWidth();
            int height = img.getHeight();

            double xOffset = width / 2.0f;
            double yOffset = height / 2.0f;

            // Scaling factor – how far apart particles are

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {

                    int argb = img.getRGB(x, y);
                    int alpha = (argb >> 24) & 0xff;

                    // Skip transparent pixels
                    if (alpha < 10) continue;

                    // Compute world position
                    double px = + (x - xOffset);
                    double py = + -(y - yOffset);

                    Vector position = new Vector(px + 0.5f, py - 0.5f, 0);
                    positions.add(position);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new GlyphData(category, name, positions, width);
    }

    public static GlyphData getGlyphFromName(String namespace) {
        return GLYPHS.get(namespace);
    }

    private static List<String> getGlyphCategories() {
        return GLYPHS.values().stream().map(gd -> gd.category()).toList();
    }

    private static List<String> getGlyphNames(String category) {
        return GLYPHS.values().stream().filter(gd -> gd.category().equalsIgnoreCase(category)).map(gd -> gd.name()).toList();
    }

    private static List<String> getGlyphNamespaces() {
        return GLYPHS.values().stream().map(gd -> gd.category() + "." + gd.name()).sorted().toList();
    }

    public ParticleEffectGlyph(Particle particle) {
        super(particle, new TransformationPipeline());
    }

    public ParticleEffectGlyph(Particle particle, double scale, String glyph) {
        super(particle, new TransformationPipeline());
        this.scale = scale;
        this.glyph = glyph;
        this.glyphData = GLYPHS.get(glyph);
    }

    @Override
    public List<Argument> getModifyArguments() {
        List<Argument> arguments = super.getModifyArguments();

        arguments.add(new StringArgument("glyph").replaceSuggestions(ArgumentSuggestions.strings(getGlyphNamespaces())));
        arguments.add(new DoubleArgument("scale"));

        return arguments;
    }

    @Override
    public void modify(CommandArguments arguments) {
        super.modify(arguments);
        glyph = (String) arguments.get("glyph");
        glyphData = GLYPHS.get(glyph);
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
