package se.fusion1013.plugin.cobaltcore.particle.styles.glyph;

import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class ParticleStyleGalactic extends ParticleStyleGlyph {

    // ----- CONSTRUCTORS -----

    public ParticleStyleGalactic() {
        super("galactic", "galactic_internal");
    }

    public ParticleStyleGalactic(String name, Particle particle, Vector offset, int count, double speed, Object extra) {
        super("galactic", name, particle, offset, count, speed, extra);
    }

    public ParticleStyleGalactic(String name) {
        super("galactic", name);
    }

    // ----- BUILDER -----

    public static class ParticleStyleGalacticBuilder extends ParticleStyleGlyphBuilder<ParticleStyleGalactic, ParticleStyleGalacticBuilder> {

        public ParticleStyleGalacticBuilder(String name) {
            super(name);
        }

        @Override
        public ParticleStyleGalactic build() {
            return super.build();
        }

        @Override
        protected ParticleStyleGalactic createObj() {
            return new ParticleStyleGalactic(name);
        }

        @Override
        protected ParticleStyleGalacticBuilder getThis() {
            return this;
        }
    }

    // ----- CLONE METHOD & CONSTRUCTOR -----

    public ParticleStyleGalactic(ParticleStyleGalactic target) {
        super(target);
    }

    @Override
    public ParticleStyleGalactic clone() {
        return new ParticleStyleGalactic(this);
    }

    // ----- GLYPHS -----

    String[] aGlyphString = registerGlyph("galactic", 'a', new String[] {
            "--xx-",
            "-x--x",
            "-x---",
            "-x---",
            "xx---"}
    );

    String[] bGlyphString = registerGlyph("galactic", 'b', new String[] {
            "--x--",
                    "--x--",
                    "---x-",
                    "----x",
                    "xxxxx"}
    );

    String[] cGlyphString = registerGlyph("galactic", 'c', new String[] {
            "--x--",
                    "-----",
                    "--x--",
                    "---x-",
                    "---x-"}
    );

    String[] dGlyphString = registerGlyph("galactic", 'd', new String[] {
            "xxxxx",
                    "-----",
                    "xx---",
                    "--xxx",
                    "-----"}
    );

    String[] eGlyphString = registerGlyph("galactic", 'e', new String[] {
            "x---x",
                    "x----",
                    "x----",
                    "x----",
                    "xxxxx"}
    );

    String[] fGlyphString = registerGlyph("galactic", 'f', new String[] {
            "xxxxx",
                    "-----",
                    "x-x-x",
                    "-----",
                    "-----"}
    );

    String[] gGlyphString = registerGlyph("galactic", 'g', new String[] {
            "---x-",
                    "---x-",
                    "-xxx-",
                    "---x-",
                    "---x-"}
    );

    String[] hGlyphString = registerGlyph("galactic", 'h', new String[] {
            "xxxxx",
                    "-----",
                    "xxxxx",
                    "--x--",
                    "--x--"}
    );

    String[] iGlyphString = registerGlyph("galactic", 'i', new String[] {
            "--x--",
                    "--x--",
                    "-----",
                    "--x--",
                    "--x--"}
    );

    String[] jGlyphString = registerGlyph("galactic", 'j', new String[] {
            "--x--",
                    "-----",
                    "--x--",
                    "-----",
                    "--x--"}
    );

    String[] kGlyphString = registerGlyph("galactic", 'k', new String[] {
            "--x--",
                    "--x--",
                    "x-x-x",
                    "--x--",
                    "--x--"}
    );

    String[] lGlyphString = registerGlyph("galactic", 'l', new String[] {
            "-x---",
                    "-x-x-",
                    "-x---",
                    "-x-x-",
                    "-x---"}
    );

    String[] mGlyphString = registerGlyph("galactic", 'm', new String[] {
            "x---x",
                    "----x",
                    "----x",
                    "----x",
                    "xxxxx"}
    );

    String[] nGlyphString = registerGlyph("galactic", 'n', new String[] {
            "x---x",
                    "x---x",
                    "---xx",
                    "--xx-",
                    "xxx--"}
    );

    String[] oGlyphString = registerGlyph("galactic", 'o', new String[] {
            "xxxxx",
                    "----x",
                    "---xx",
                    "--xx-",
                    "xxx--"}
    );

    String[] pGlyphString = registerGlyph("galactic", 'p', new String[] {
            "x---x",
                    "----x",
                    "x---x",
                    "x----",
                    "x---x"}
    );

    String[] qGlyphString = registerGlyph("galactic", 'q', new String[] {
            "--x--",
                    "-----",
                    "xxxxx",
                    "----x",
                    "xxxxx"}
    );

    String[] rGlyphString = registerGlyph("galactic", 'r', new String[] {
            "x---x",
                    "-----",
                    "-----",
                    "-----",
                    "x---x"}
    );

    String[] sGlyphString = registerGlyph("galactic", 's', new String[] {
            "--x--",
                    "--x--",
                    "---x-",
                    "---x-",
                    "---x-"}
    );

    String[] tGlyphString = registerGlyph("galactic", 't', new String[] {
            "xxxxx",
                    "----x",
                    "----x",
                    "-----",
                    "----x"}
    );

    String[] uGlyphString = registerGlyph("galactic", 'u', new String[] {
            "-----",
                    "-x-x-",
                    "-----",
                    "-xxx-",
                    "-----"}
    );

    String[] vGlyphString = registerGlyph("galactic", 'v', new String[] {
            "--x--",
                    "--x--",
                    "xxxxx",
                    "-----",
                    "xxxxx"}
    );

    String[] wGlyphString = registerGlyph("galactic", 'w', new String[] {
            "-----",
                    "--x--",
                    "-----",
                    "-x-x-",
                    "-----"}
    );

    String[] xGlyphString = registerGlyph("galactic", 'x', new String[] {
            "x---x",
                    "---x-",
                    "---x-",
                    "--x--",
                    "--x--"}
    );

    String[] yGlyphString = registerGlyph("galactic", 'y', new String[] {
            "-x-x-",
                    "-x-x-",
                    "-x-x-",
                    "-x-x-",
                    "-x-x-"}
    );

    String[] zGlyphString = registerGlyph("galactic", 'z', new String[] {
            "--x--",
                    "-x-x-",
                    "-x-x-",
                    "-x-x-",
                    "-x-x-"}
    );

    String[] spaceGlyphString = registerGlyph("galactic", ' ', new String[] {
            "-----",
                    "-----",
                    "-----",
                    "-----",
                    "-----"}
            );
}
