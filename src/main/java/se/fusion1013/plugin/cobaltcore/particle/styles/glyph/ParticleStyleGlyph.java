package se.fusion1013.plugin.cobaltcore.particle.styles.glyph;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyle;
import se.fusion1013.plugin.cobaltcore.util.ParticleContainer;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ParticleStyleGlyph extends ParticleStyle {

    // ----- VARIABLES -----

    private static final Map<String, Map<Character, String[]>> glyphOffsets = new HashMap<>();

    Character letter;
    double compress = 1;

    // ----- CONSTRUCTORS -----

    public ParticleStyleGlyph(String internalStyleName, String name, Particle particle, Vector offset, int count, double speed, Object extra) {
        super(internalStyleName, name, particle, offset, count, speed, extra);
    }

    public ParticleStyleGlyph(String internalStyleName, String name) {
        super(internalStyleName, name);
    }

    // ----- INFO -----

    @Override
    public List<String> getInfoStrings() {
        List<String> info = super.getInfoStrings();
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("letter", letter)
                .addPlaceholder("compress", compress)
                .build();
        info.add(LocaleManager.getInstance().getLocaleMessage("particle.style.glyph.info", placeholders));
        return info;
    }

    // ----- SET EXTRA SETTINGS -----

    @Override
    public void setExtraSetting(String key, Object value) {
        switch (key) {
            case "letter" -> letter = ((String) value).charAt(0);
            case "compress" -> compress = (double) value;
        }
    }

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
        if (letter == null) return new ParticleContainer[0];
        String[] letterStrings = glyphOffsets.get(internalStyleName).get(Character.toLowerCase(letter));

        if (letterStrings == null) return particles.toArray(new ParticleContainer[0]);

        for (int y = 0; y < letterStrings.length; y++) {
            for (int x = 0; x < letterStrings[y].length(); x++) {

                String line = letterStrings[y];
                char c = line.charAt(x);

                if (c == 'x') {

                    double radius = ((double)line.length() - 1) / 2.0;
                    double topRadius = ((double)letterStrings.length - 1) / 2.0;

                    Vector offset = new Vector((x - radius) * compress, -(y - topRadius) * compress, 0);

                    particles.add(new ParticleContainer(location.clone().add(offset), getOffset().getX(), getOffset().getY(), getOffset().getZ(), getSpeed(), (int) getCount()));
                }
            }
        }

        return particles.toArray(new ParticleContainer[0]);
    }

    @Override
    public ParticleContainer[] getParticleContainers(Location location1, Location location2) {
        return new ParticleContainer[0];
    }

    // ----- REGISTER -----

    String[] registerGlyph(String styleName, final char c, final String[] offsets) {
        glyphOffsets.computeIfAbsent(styleName, k -> new HashMap<>());
        return glyphOffsets.get(styleName).put(c, offsets);
    }

    // ----- BUILDER -----

    public static abstract class ParticleStyleGlyphBuilder<T extends ParticleStyleGlyph, B extends ParticleStyleBuilder> extends ParticleStyleBuilder<T, B> {

        Character letter;
        double compress = .2;

        public ParticleStyleGlyphBuilder(String name) {
            super(name);
        }

        @Override
        public T build() {
            obj.letter = letter;
            obj.compress = compress;

            return super.build();
        }

        public B setLetter(Character letter) {
            this.letter = letter;
            return getThis();
        }

        public B setCompress(double compress) {
            this.compress = compress;
            return getThis();
        }
    }

    // ----- GETTERS / SETTERS -----

    public void setLetter(Character letter) {
        this.letter = letter;
    }

    public void setCompress(double compress) {
        this.compress = compress;
    }


    // ----- CLONE METHOD & CONSTRUCTOR -----

    public ParticleStyleGlyph(ParticleStyleGlyph target) {
        super(target);

        this.letter = target.letter;
        this.compress = target.compress;
    }

    public abstract ParticleStyleGlyph clone();
}
