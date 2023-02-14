package se.fusion1013.plugin.cobaltcore.particle.styles.glyph;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.*;
import org.apache.maven.model.Build;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.particle.manager.ParticleStyleManager;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyle;
import se.fusion1013.plugin.cobaltcore.util.ParticleContainer;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParticleStyleCircleText extends ParticleStyle {

    // ----- VARIABLES -----

    // TODO: Add individual rotation to glyphs
    String text = "";
    double compress = .2;
    double radius = 10;
    ParticleStyleGlyph glyphStyle = new ParticleStyleGalactic();

    // ----- CONSTRUCTORS -----

    public ParticleStyleCircleText(String name, Particle particle, Vector offset, int count, double speed, Object extra) {
        super("text_circle", name, particle, offset, count, speed, extra);

        skipTicks = 2;
    }

    public ParticleStyleCircleText(String name) {
        super("text_circle", name);

        skipTicks = 2;
    }

    public ParticleStyleCircleText() {
        super("text_circle", "text_circle_internal");

        skipTicks = 2;
    }

    //region DATA_LOADING

    @Override
    public void loadData(Map<?, ?> data) {
        super.loadData(data);

        if (data.containsKey("text")) text = data.get("text").toString();
        if (data.containsKey("compress")) compress = (double) data.get("compress");
        if (data.containsKey("radius")) radius = (double) data.get("radius");

        if (data.containsKey("glyph_style")) {
            String glyphStyleName = data.get("glyph_style").toString();
            ParticleStyle style = ParticleStyleManager.getDefaultParticleStyle(glyphStyleName).clone();
            if (style instanceof ParticleStyleGlyph glyph) glyphStyle = glyph;
        }
    }

    //endregion

    // ----- INFO -----

    @Override
    public List<String> getInfoStrings() {
        List<String> info = super.getInfoStrings();
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("text", text)
                .addPlaceholder("radius", radius)
                .addPlaceholder("style", glyphStyle.getInternalName())
                .addPlaceholder("compress", compress)
                .build();
        info.add(LocaleManager.getInstance().getLocaleMessage("particle.style.text.info", placeholders));
        return info;
    }

    // ----- SET EXTRA SETTINGS -----

    @Override
    public void setExtraSetting(String key, Object value) {
        switch (key) {
            case "text" -> text = (String) value;
            case "radius" -> radius = (double) value;
            case "compress" -> compress = (double) value;
            case "style" -> glyphStyle = (ParticleStyleGlyph) ParticleStyleManager.getDefaultParticleStyle((String) value).clone();
        }
    }

    @Override
    public Argument[] getExtraSettingsArguments() {
        List<Argument> arguments = new ArrayList<>();
        arguments.add(new TextArgument("text"));
        arguments.add(new DoubleArgument("radius"));
        arguments.add(new DoubleArgument("compress"));
        arguments.add(new StringArgument("style").replaceSuggestions(ArgumentSuggestions.strings(suggestionInfo -> new String[] {"galactic", "finnish_glyph"})));
        return arguments.toArray(new Argument[0]);
    }

    @Override
    public String getExtraSettings() {
        JsonObject jo = new JsonObject();
        jo.addProperty("text", text);
        jo.addProperty("radius", radius);
        jo.addProperty("compress", compress);
        jo.addProperty("style", glyphStyle.getInternalName());
        return jo.toString();
    }

    @Override
    public void setExtraSettings(String extra) {
        JsonObject jsonObject = new Gson().fromJson(extra, JsonObject.class);
        text = jsonObject.get("text").getAsString();
        radius = jsonObject.get("radius").getAsDouble();
        compress = jsonObject.get("compress").getAsDouble();
        glyphStyle = (ParticleStyleGlyph) ParticleStyleManager.getDefaultParticleStyle(jsonObject.get("style").getAsString()).clone();
    }

    @Override
    public void setExtraSettings(Object[] args) {
        super.setExtraSettings(args);

        text = (String)args[0];
        radius = (double) args[1];
        compress = (double) args[2];
        glyphStyle = (ParticleStyleGlyph) ParticleStyleManager.getDefaultParticleStyle((String) args[3]).clone();
    }

    // ----- PARTICLE GETTING -----

    @Override
    public ParticleContainer[] getParticleContainers(Location location) {
        List<ParticleContainer> particles = new ArrayList<>();

        for (int x = 0; x < text.length(); x++) {
            double angle = (((Math.PI * 2) / text.length()) * x);
            double xDelta = Math.cos(angle) * radius;
            double zDelta = Math.sin(angle) * radius;
            Vector offset = new Vector(xDelta, 0, -zDelta);

            if (glyphStyle != null) {
                glyphStyle.letter = text.charAt(x);
                glyphStyle.compress = compress;
                glyphStyle.setRotation(new Vector(0, Math.toDegrees(angle) - 90, 0));
                particles.addAll(List.of(glyphStyle.getParticles(location.clone().add(offset))));
            }
        }

        return particles.toArray(new ParticleContainer[0]);
    }

    @Override
    public ParticleContainer[] getParticleContainers(Location location1, Location location2) {
        return new ParticleContainer[0];
    }

    // ----- BUILDER -----

    public static class Builder extends ParticleStyleBuilder<ParticleStyleCircleText, ParticleStyleCircleText.Builder> {

        // -- VARIABLES

        String text = "";
        double compress = .2;
        double radius = 10;
        ParticleStyleGlyph glyphStyle = new ParticleStyleGalactic();

        // -- CONSTRUCTORS

        public Builder() {
            super();
        }

        public Builder(String name) {
            super(name);
        }

        // -- BUILD

        @Override
        public ParticleStyleCircleText build() {
            obj.text = text;
            obj.compress = compress;
            obj.glyphStyle = glyphStyle;
            obj.radius = radius;

            return super.build();
        }

        // -- BUILDER METHODS

        public Builder setText(String text) {
            this.text = text;
            return getThis();
        }

        public Builder setCompress(double compress) {
            this.compress = compress;
            return getThis();
        }

        public Builder setGlyphStyle(ParticleStyleGlyph glyphStyle) {
            this.glyphStyle = glyphStyle;
            return getThis();
        }

        public Builder setRadius(double radius) {
            this.radius = radius;
            return getThis();
        }

        // -- GETTERS / SETTERS

        @Override
        protected ParticleStyleCircleText createObj() {
            return new ParticleStyleCircleText(name);
        }

        @Override
        protected Builder getThis() {
            return this;
        }
    }

    // ----- CLONE -----

    @Override
    public ParticleStyleCircleText clone() {
        return new ParticleStyleCircleText(this);
    }

    public ParticleStyleCircleText(ParticleStyleCircleText target) {
        super(target);

        this.text = target.text;
        this.compress = target.compress;
        this.radius = target.radius;
        this.glyphStyle = target.glyphStyle.clone();
    }
}
