package se.fusion1013.cobaltCore.particle.effects.glyph;

import org.bukkit.util.Vector;

import java.util.List;

public record GlyphData(String category, String name, List<Vector> positions, double width) {
}
