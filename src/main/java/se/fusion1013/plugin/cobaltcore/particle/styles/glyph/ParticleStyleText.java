package se.fusion1013.plugin.cobaltcore.particle.styles.glyph;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.*;
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

public class ParticleStyleText extends ParticleStyle {

    // ----- VARIABLES -----

    String text = "";
    double spacing = 1.7;
    double compress = .2;
    ParticleStyleGlyph glyphStyle = new ParticleStyleGalactic();

    // ----- CONSTRUCTORS -----

    public ParticleStyleText(String name, Particle particle, Vector offset, int count, double speed, Object extra) {
        super("text", name, particle, offset, count, speed, extra);

        skipTicks = 2;
    }

    public ParticleStyleText(String name) {
        super("text", name);

        skipTicks = 2;
    }

    // ----- INFO -----

    @Override
    public List<String> getInfoStrings() {
        List<String> info = super.getInfoStrings();
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("text", text)
                .addPlaceholder("spacing", spacing)
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
            case "spacing" -> spacing = (int) value;
            case "compress" -> compress = (double) value;
            case "style" -> glyphStyle = (ParticleStyleGlyph) ParticleStyleManager.getDefaultParticleStyle((String) value).clone();
        }
    }

    @Override
    public Argument[] getExtraSettingsArguments() {
        List<Argument> arguments = new ArrayList<>();
        arguments.add(new TextArgument("text"));
        arguments.add(new DoubleArgument("spacing"));
        arguments.add(new DoubleArgument("compress"));
        arguments.add(new StringArgument("style").replaceSuggestions(ArgumentSuggestions.strings(suggestionInfo -> new String[] {"galactic", "finnish_glyph"})));
        return arguments.toArray(new Argument[0]);
    }

    @Override
    public String getExtraSettings() {
        JsonObject jo = new JsonObject();
        jo.addProperty("text", text);
        jo.addProperty("spacing", spacing);
        jo.addProperty("compress", compress);
        jo.addProperty("style", glyphStyle.getInternalName());
        return jo.toString();
    }

    @Override
    public void setExtraSettings(String extra) {
        JsonObject jsonObject = new Gson().fromJson(extra, JsonObject.class);
        text = jsonObject.get("text").getAsString();
        spacing = jsonObject.get("spacing").getAsDouble();
        compress = jsonObject.get("compress").getAsDouble();
        glyphStyle = (ParticleStyleGlyph) ParticleStyleManager.getDefaultParticleStyle(jsonObject.get("style").getAsString()).clone();
    }

    @Override
    public void setExtraSettings(Object[] args) {
        super.setExtraSettings(args);

        text = (String)args[0];
        spacing = (double) args[1];
        compress = (double) args[2];
        glyphStyle = (ParticleStyleGlyph) ParticleStyleManager.getDefaultParticleStyle((String) args[3]).clone();
    }

    // ----- PARTICLE GETTING -----

    @Override
    public ParticleContainer[] getParticleContainers(Location location) {
        List<ParticleContainer> particles = new ArrayList<>();

        for (int x = 0; x < text.length(); x++) {
            double radius = ((double)text.length() - 1) / 2.0;

            Vector offset = new Vector((x - radius) * spacing, 0, 0);

            if (glyphStyle != null) {
                glyphStyle.letter = text.charAt(x);
                glyphStyle.compress = compress;
                particles.addAll(List.of(glyphStyle.getParticleContainers(location.clone().add(offset))));
            }
        }

        return particles.toArray(new ParticleContainer[0]);
    }

    @Override
    public ParticleContainer[] getParticleContainers(Location location1, Location location2) {
        return new ParticleContainer[0];
    }

    // ----- CLONE -----

    @Override
    public ParticleStyleText clone() {
        return new ParticleStyleText(this);
    }

    public ParticleStyleText(ParticleStyleText target) {
        super(target);

        this.text = target.text;
        this.spacing = target.spacing;
        this.compress = target.compress;
        this.glyphStyle = target.glyphStyle.clone();
    }
}
