package se.fusion1013.plugin.cobaltcore.particle.styles;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.ParticleContainer;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticleStyleGalactic extends ParticleStyle {

    // ----- VARIABLES -----

    private static final Map<Character, String> glyphOffsets = new HashMap<>();

    Character letter;
    double compress = 4.0;

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

    // ----- INFO -----

    @Override
    public List<String> getInfoStrings() {
        List<String> info = super.getInfoStrings();
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("letter", letter)
                .addPlaceholder("compress", compress)
                .build();
        info.add(LocaleManager.getInstance().getLocaleMessage("particle.style.galactic.info", placeholders));
        return info;
    }

    // ----- SET EXTRA SETTINGS -----

    @Override
    public Argument[] getExtraSettingsArguments() {
        List<Argument> arguments = new ArrayList<>();
        arguments.add(new StringArgument("letter"));
        arguments.add(new DoubleArgument("compress"));
        return arguments.toArray(new Argument[0]);
    }

    @Override
    public void setExtraSettings(Object[] args) {
        super.setExtraSettings(args);

        letter = ((String)args[0]).charAt(0);
        compress = (double) args[1];
    }

    @Override
    public String getExtraSettings() {
        JsonObject jo = new JsonObject();
        jo.addProperty("letter", letter);
        jo.addProperty("compress", compress);
        return jo.toString();
    }

    @Override
    public void setExtraSettings(String extra) {
        JsonObject jsonObject = new Gson().fromJson(extra, JsonObject.class);
        letter = jsonObject.get("letter").getAsCharacter();
        compress = jsonObject.get("compress").getAsDouble();
    }

    // ----- PARTICLE GETTING -----

    @Override
    public ParticleContainer[] getParticleContainers(Location location) {
        List<ParticleContainer> particles = new ArrayList<>();
        String letterString = glyphOffsets.get(letter);

        if (letterString == null) return particles.toArray(new ParticleContainer[0]);

        for (int i = 0; i < letterString.length(); i++) {
            char c = letterString.charAt(i);

            if (c == 'x') {

                int xInt = i % 5;
                int yInt = 5 - (i / 5);

                double x = (double)xInt / compress;
                double y = (double)yInt / compress;

                double messageRadius = 2.5 / compress;

                Vector offset = new Vector(x-messageRadius, y-messageRadius, 0);

                particles.add(new ParticleContainer(location.clone().add(offset), 0, 0, 0, 0, 1));
            }
        }

        return particles.toArray(new ParticleContainer[0]);
    }

    @Override
    public ParticleContainer[] getParticleContainers(Location location1, Location location2) {
        return new ParticleContainer[0];
    }

    // ----- REGISTER -----

    private static String registerGlyph(final char c, final String offsets) {
        return glyphOffsets.put(c, offsets);
    }

    // ----- BUILDER -----

    public static class ParticleStyleGalacticBuilder extends ParticleStyleBuilder<ParticleStyleGalactic, ParticleStyleGalacticBuilder> {

        Character letter;

        @Override
        public ParticleStyleGalactic build() {
            obj.letter = letter;

            return super.build();
        }

        @Override
        protected ParticleStyleGalactic createObj() {
            return new ParticleStyleGalactic();
        }

        @Override
        protected ParticleStyleGalacticBuilder getThis() {
            return this;
        }

        public ParticleStyleGalacticBuilder setLetter(Character letter) {
            this.letter = letter;
            return getThis();
        }
    }

    // ----- CLONE METHOD & CONSTRUCTOR -----

    public ParticleStyleGalactic(ParticleStyleGalactic target) {
        super(target);

        this.letter = target.letter;
        this.compress = target.compress;
    }

    @Override
    public ParticleStyleGalactic clone() {
        return new ParticleStyleGalactic(this);
    }

    // ----- GLYPHS -----

    String aGlyphString = registerGlyph('a',
            "--xx-" +
                    "-x--x" +
                    "-x---" +
                    "-x---" +
                    "xx---");

    String bGlyphString = registerGlyph('b',
            "--x--" +
                    "--x--" +
                    "---x-" +
                    "----x" +
                    "xxxxx");

    String cGlyphString = registerGlyph('c',
            "--x--" +
                    "-----" +
                    "--x--" +
                    "---x-" +
                    "---x-");

    String dGlyphString = registerGlyph('d',
            "xxxxx" +
                    "-----" +
                    "xx---" +
                    "--xxx" +
                    "-----");

    String eGlyphString = registerGlyph('e',
            "x---x" +
                    "x----" +
                    "x----" +
                    "x----" +
                    "xxxxx");

    String fGlyphString = registerGlyph('f',
            "xxxxx" +
                    "-----" +
                    "x-x-x" +
                    "-----" +
                    "-----");

    String gGlyphString = registerGlyph('g',
            "---x-" +
                    "---x-" +
                    "-xxx-" +
                    "---x-" +
                    "---x-");

    String hGlyphString = registerGlyph('h',
            "xxxxx" +
                    "-----" +
                    "xxxxx" +
                    "--x--" +
                    "--x--");

    String iGlyphString = registerGlyph('i',
            "--x--" +
                    "--x--" +
                    "-----" +
                    "--x--" +
                    "--x--");

    String jGlyphString = registerGlyph('j',
            "--x--" +
                    "-----" +
                    "--x--" +
                    "-----" +
                    "--x--");

    String kGlyphString = registerGlyph('k',
            "--x--" +
                    "--x--" +
                    "x-x-x" +
                    "--x--" +
                    "--x--");

    String lGlyphString = registerGlyph('l',
            "-x---" +
                    "-x-x-" +
                    "-x---" +
                    "-x-x-" +
                    "-x---");

    String mGlyphString = registerGlyph('m',
            "x---x" +
                    "----x" +
                    "----x" +
                    "----x" +
                    "xxxxx");

    String nGlyphString = registerGlyph('n',
            "x---x" +
                    "x---x" +
                    "---xx" +
                    "--xx-" +
                    "xxx--");

    String oGlyphString = registerGlyph('o',
            "xxxxx" +
                    "----x" +
                    "---xx" +
                    "--xx-" +
                    "xxx--");

    String pGlyphString = registerGlyph('p',
            "x---x" +
                    "----x" +
                    "x---x" +
                    "x----" +
                    "x---x");

    String qGlyphString = registerGlyph('q',
            "--x--" +
                    "-----" +
                    "xxxxx" +
                    "----x" +
                    "xxxxx");

    String rGlyphString = registerGlyph('r',
            "x---x" +
                    "-----" +
                    "-----" +
                    "-----" +
                    "x---x");

    String sGlyphString = registerGlyph('s',
            "--x--" +
                    "--x--" +
                    "---x-" +
                    "---x-" +
                    "---x-");

    String tGlyphString = registerGlyph('t',
            "xxxxx" +
                    "----x" +
                    "----x" +
                    "-----" +
                    "----x");

    String uGlyphString = registerGlyph('u',
            "-----" +
                    "-x-x-" +
                    "-----" +
                    "-xxx-" +
                    "-----");

    String vGlyphString = registerGlyph('v',
            "--x--" +
                    "--x--" +
                    "xxxxx" +
                    "-----" +
                    "xxxxx");

    String wGlyphString = registerGlyph('w',
            "-----" +
                    "--x--" +
                    "-----" +
                    "-x-x-" +
                    "-----");

    String xGlyphString = registerGlyph('x',
            "x---x" +
                    "---x-" +
                    "---x-" +
                    "--x--" +
                    "--x--");

    String yGlyphString = registerGlyph('y',
            "-x-x-" +
                    "-x-x-" +
                    "-x-x-" +
                    "-x-x-" +
                    "-x-x-");

    String zGlyphString = registerGlyph('z',
            "--x--" +
                    "-x-x-" +
                    "-x-x-" +
                    "-x-x-" +
                    "-x-x-");

    String spaceGlyphString = registerGlyph(' ',
            "-----" +
                    "-----" +
                    "-----" +
                    "-----" +
                    "-----");
}
