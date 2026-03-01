package se.fusion1013.cobaltCore.particle.effects.glyph;

import java.util.ArrayList;
import java.util.List;

public record GlyphAlphabet(String identifier, List<GlyphAlphabetCharacter> characters) {
    public GlyphData getGlyph(char character) {
        for (GlyphAlphabetCharacter glyphAlphabetCharacter : characters) {
            if (!glyphAlphabetCharacter.symbol().equalsIgnoreCase("" + character)) continue;
            return GlyphManager.getGlyphFromName(glyphAlphabetCharacter.path());
        }
        return null;
    }

    public GlyphData[] getGlyphs() {
        List<GlyphData> glyphs = new ArrayList<>();
        for (GlyphAlphabetCharacter character : characters) {
            glyphs.add(GlyphManager.getGlyphFromName(character.path()));
        }
        return glyphs.toArray(new GlyphData[0]);
    }
}
