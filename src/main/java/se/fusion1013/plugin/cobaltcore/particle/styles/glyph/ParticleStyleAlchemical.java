package se.fusion1013.plugin.cobaltcore.particle.styles.glyph;

import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class ParticleStyleAlchemical extends ParticleStyleGlyph {

    // ----- CONSTRUCTORS -----

    public ParticleStyleAlchemical() {
        super("alchemical", "alchemical_internal");
    }

    public ParticleStyleAlchemical(String name, Particle particle, Vector offset, int count, double speed, Object extra) {
        super("alchemical", name, particle, offset, count, speed, extra);
    }

    public ParticleStyleAlchemical(String name) {
        super("alchemical", name);
    }

    // ----- BUILDER -----

    public static class ParticleStyleAlchemicalBuilder extends ParticleStyleGlyph.ParticleStyleGlyphBuilder<ParticleStyleAlchemical, ParticleStyleAlchemicalBuilder> {

        public ParticleStyleAlchemicalBuilder(String name) {
            super(name);
        }

        @Override
        public ParticleStyleAlchemical build() {
            return super.build();
        }

        @Override
        protected ParticleStyleAlchemical createObj() {
            return new ParticleStyleAlchemical(name);
        }

        @Override
        protected ParticleStyleAlchemical.ParticleStyleAlchemicalBuilder getThis() {
            return this;
        }
    }

    // ----- CLONE METHOD & CONSTRUCTOR -----

    public ParticleStyleAlchemical(ParticleStyleAlchemical target) {
        super(target);
    }

    @Override
    public ParticleStyleAlchemical clone() {
        return new ParticleStyleAlchemical(this);
    }

    // ----- GLYPHS -----

    String[] fireGlyphSymbol = registerGlyph("alchemical", '1', new String[] {
            "---------------",
            "-------x-------",
            "------x-x------",
            "------x-x------",
            "-----x---x-----",
            "-----x---x-----",
            "----x-----x----",
            "----x-----x----",
            "---x-------x---",
            "---x-------x---",
            "--x---------x--",
            "--x---------x--",
            "-xxxxxxxxxxxxx-",
            "---------------",
            "---------------"}
    );

    String[] waterGlyphSymbol = registerGlyph("alchemical", '2', new String[] {
            "---------------",
            "-xxxxxxxxxxxxx-",
            "--x---------x--",
            "--x---------x--",
            "---x-------x---",
            "---x-------x---",
            "----x-----x----",
            "----x-----x----",
            "-----x---x-----",
            "-----x---x-----",
            "------x-x------",
            "------x-x------",
            "-------x-------",
            "---------------",
            "---------------"}
    );

    String[] airGlyphSymbol = registerGlyph("alchemical", '3', new String[] {
            "---------------",
            "-------x-------",
            "------x-x------",
            "------x-x------",
            "-----x---x-----",
            "-----x---x-----",
            "-xxxxxxxxxxxxx-",
            "----x-----x----",
            "---x-------x---",
            "---x-------x---",
            "--x---------x--",
            "--x---------x--",
            "-xxxxxxxxxxxxx-",
            "---------------",
            "---------------"}
    );

    String[] earthGlyphSymbol = registerGlyph("alchemical", '4', new String[] {
            "---------------",
            "-xxxxxxxxxxxxx-",
            "--x---------x--",
            "--x---------x--",
            "---x-------x---",
            "---x-------x---",
            "-xxxxxxxxxxxxx-",
            "----x-----x----",
            "-----x---x-----",
            "-----x---x-----",
            "------x-x------",
            "------x-x------",
            "-------x-------",
            "---------------",
            "---------------"}
    );

    String[] ironGlyphSymbol = registerGlyph("alchemical", '5', new String[] {
            "---------------",
            "---------xxxxx-",
            "------------xx-",
            "-----------x-x-",
            "----------x--x-",
            "----xxx--x---x-",
            "---x---xx------",
            "--x-----x------",
            "-x-------x-----",
            "-x-------x-----",
            "-x-------x-----",
            "--x-----x------",
            "---x---x-------",
            "----xxx--------",
            "---------------"}
    );

    String[] goldGlyphSymbol = registerGlyph("alchemical", '6', new String[] {
            "-----xxxx------",
            "---xx----xx----",
            "--x--------x---",
            "-x----------x--",
            "-x----xx----x--",
            "x----xxxx----x-",
            "x---xxxxxx---x-",
            "x---xxxxxx---x-",
            "x----xxxx----x-",
            "-x----xx----x--",
            "-x----------x--",
            "--x--------x---",
            "---xx----xx----",
            "-----xxxx------",
            "---------------"}
    );

    String[] brimstoneGlyphSymbol = registerGlyph("alchemical", '7', new String[] {
            "---------------",
            "-------x-------",
            "-------x-------",
            "-------x-------",
            "-----xxxxx-----",
            "-------x-------",
            "-------x-------",
            "-------x-------",
            "----xxxxxxx----",
            "-------x-------",
            "--xxxx-x-xxxx--",
            "-x----xxx----x-",
            "x------x------x",
            "-x----x-x----x-",
            "--xxxx---xxxx--"}
    );

    String[] phosphorGlyphSymbol = registerGlyph("alchemical", '8', new String[] {
            "---------------",
            "-------x-------",
            "-------x-------",
            "-----xxxxx-----",
            "-------x-------",
            "-------x-------",
            "------x-x------",
            "------x-x------",
            "-----x---x-----",
            "-----x---x-----",
            "----x-----x----",
            "----x-----x----",
            "---x-------x---",
            "--xxxxxxxxxxx--",
            "---------------"}
    );

    String[] copperGlyphSymbol = registerGlyph("alchemical", '9', new String[] {
            "----x-----x----",
            "----x-----x----",
            "-----x---x-----",
            "-----x---x-----",
            "-x----x-x----x-",
            "x-xxxxxxxxxxx-x",
            "-x-----x-----x-",
            "---xxxxxxxxx---",
            "-x-----x-----x-",
            "x-xxxxxxxxxxx-x",
            "-x----x-x----x-",
            "-----x---x-----",
            "-----x---x-----",
            "----x-----x----",
            "----x-----x----"}
    );

    String[] emptyGlyphSymbol = registerGlyph("alchemical", ' ', new String[] {
            "---------------",
            "---------------",
            "---------------",
            "---------------",
            "---------------",
            "---------------",
            "---------------",
            "---------------",
            "---------------",
            "---------------",
            "---------------",
            "---------------",
            "---------------",
            "---------------",
            "---------------"}
    );

}
