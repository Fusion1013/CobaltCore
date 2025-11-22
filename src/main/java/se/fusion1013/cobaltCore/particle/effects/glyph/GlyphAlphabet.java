package se.fusion1013.cobaltCore.particle.effects.glyph;

import java.util.List;

public record GlyphAlphabet(String identifier, List<GlyphAlphabetCharacter> characters) {
    public GlyphData getGlyph(char character) {
        for (GlyphAlphabetCharacter glyphAlphabetCharacter : characters) {
            if (!glyphAlphabetCharacter.symbol().equalsIgnoreCase("" + character)) continue;
            return GlyphManager.getGlyphFromName(glyphAlphabetCharacter.path());
        }
        return null;
    }
}
