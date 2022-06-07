package se.fusion1013.plugin.cobaltcore.particle.styles;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.ParticleContainer;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

import java.util.ArrayList;
import java.util.List;

public class ParticleStyleParametric extends ParticleStyle {

    // ----- VARIABLES -----

    String xEquation = "";
    String yEquation = "";
    String zEquation = "";

    String xEquation2 = "";
    String yEquation2 = "";
    String zEquation2 = "";

    int duration = 0; // Duration before the style loops, measured in ticks
    int duration2 = 0;

    int currentTick = 0;

    // ----- CONSTRUCTORS -----

    public ParticleStyleParametric(String name, Particle particle, Vector offset, int count, double speed, Object extra) {
        super("parametric", name, particle, offset, count, speed, extra);
    }

    public ParticleStyleParametric(String name) {
        super("parametric", name);
    }

    // ----- INFO -----

    @Override
    public List<String> getInfoStrings() {
        List<String> info = super.getInfoStrings();

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("ex", xEquation)
                .addPlaceholder("ey", yEquation)
                .addPlaceholder("ez", zEquation)
                .addPlaceholder("duration", duration)
                .build();
        info.add(LocaleManager.getInstance().getLocaleMessage("particle.style.parametric.info.1", placeholders));

        if (!xEquation2.equalsIgnoreCase("") && !yEquation2.equalsIgnoreCase("") && !zEquation2.equalsIgnoreCase("")) {
            StringPlaceholders placeholders2 = StringPlaceholders.builder()
                    .addPlaceholder("ex2", xEquation2)
                    .addPlaceholder("ey2", yEquation2)
                    .addPlaceholder("ez2", zEquation2)
                    .addPlaceholder("duration2", duration2)
                    .build();
            info.add(LocaleManager.getInstance().getLocaleMessage("particle.style.parametric.info.2", placeholders2));
        }

        return info;
    }

    // ----- EXTRA SETTINGS -----

    @Override
    public void setExtraSetting(String key, Object value) {
        switch (key) {
            case "xEquation" -> xEquation = (String) value;
            case "yEquation" -> yEquation = (String) value;
            case "zEquation" -> zEquation = (String) value;
            case "xEquation2" -> xEquation2 = (String) value;
            case "yEquation2" -> yEquation2 = (String) value;
            case "zEquation2" -> zEquation2 = (String) value;
            case "duration" -> duration = (int) value;
            case "duration2" -> duration2 = (int) value;
        }
    }

    @Override
    public Argument[] getExtraSettingsArguments() {
        List<Argument> arguments = new ArrayList<>();

        arguments.add(new TextArgument("xEquation"));
        arguments.add(new TextArgument("yEquation"));
        arguments.add(new TextArgument("zEquation"));
        arguments.add(new TextArgument("xEquation2"));
        arguments.add(new TextArgument("yEquation2"));
        arguments.add(new TextArgument("zEquation2"));
        arguments.add(new IntegerArgument("duration"));
        arguments.add(new IntegerArgument("duration2"));

        return arguments.toArray(new Argument[0]);
    }

    @Override
    public void setExtraSettings(Object[] args) {
        super.setExtraSettings(args);

        xEquation = (String) args[0];
        yEquation = (String) args[1];
        zEquation = (String) args[2];
        xEquation2 = (String) args[3];
        yEquation2 = (String) args[4];
        zEquation2 = (String) args[5];
        duration = (int) args[6];
        duration2 = (int) args[7];
    }

    @Override
    public String getExtraSettings() {
        JsonObject jo = new JsonObject();
        jo.addProperty("xEquation", xEquation);
        jo.addProperty("yEquation", yEquation);
        jo.addProperty("zEquation", zEquation);
        jo.addProperty("xEquation2", xEquation2);
        jo.addProperty("yEquation2", yEquation2);
        jo.addProperty("zEquation2", zEquation2);
        jo.addProperty("duration", duration);
        jo.addProperty("duration2", duration2);
        return jo.toString();
    }

    @Override
    public void setExtraSettings(String extra) {
        JsonObject jo = new Gson().fromJson(extra, JsonObject.class);
        if (jo.get("xEquation") != null) xEquation = jo.get("xEquation").getAsString();
        if (jo.get("yEquation") != null) yEquation = jo.get("yEquation").getAsString();
        if (jo.get("zEquation") != null) zEquation = jo.get("zEquation").getAsString();
        if (jo.get("xEquation2") != null) xEquation2 = jo.get("xEquation2").getAsString();
        if (jo.get("yEquation2") != null) yEquation2 = jo.get("yEquation2").getAsString();
        if (jo.get("zEquation2") != null) zEquation2 = jo.get("zEquation2").getAsString();
        if (jo.get("duration") != null) duration = jo.get("duration").getAsInt();
        if (jo.get("duration2") != null) duration2 = jo.get("duration2").getAsInt();
    }

    // ----- PARTICLE GETTING -----

    @Override
    public ParticleContainer[] getParticleContainers(Location location) {
        if (currentTick > duration) currentTick = 0;

        List<ParticleContainer> containers = new ArrayList<>();

        // Create the point from the first equation
        double xOff = new ExpressionBuilder(xEquation)
                .variable("t")
                .build()
                .setVariable("t", currentTick).evaluate();
        double yOff = new ExpressionBuilder(yEquation)
                .variable("t")
                .build()
                .setVariable("t", currentTick).evaluate();
        double zOff = new ExpressionBuilder(zEquation)
                .variable("t")
                .build()
                .setVariable("t", currentTick).evaluate();

        // If the second equation exists, calculate it and draw the entire span
        if (!xEquation2.equalsIgnoreCase("") && !yEquation2.equalsIgnoreCase("") && !zEquation2.equalsIgnoreCase("")) {
            for (int i = 0; i < duration2; i++) {
                double xOff2 = new ExpressionBuilder(xEquation2)
                        .variable("t2")
                        .variable("t")
                        .build()
                        .setVariable("t2", i)
                        .setVariable("t", currentTick).evaluate();
                double yOff2 = new ExpressionBuilder(yEquation2)
                        .variable("t2")
                        .variable("t")
                        .build()
                        .setVariable("t2", i)
                        .setVariable("t", currentTick).evaluate();
                double zOff2 = new ExpressionBuilder(zEquation2)
                        .variable("t2")
                        .variable("t")
                        .build()
                        .setVariable("t2", i)
                        .setVariable("t", currentTick).evaluate();

                containers.add(new ParticleContainer(location.clone().add(xOff + xOff2, yOff + yOff2, zOff + zOff2), offset.getX(), offset.getY(), offset.getZ(), speed, count));
            }
        } else {
            containers.add(new ParticleContainer(location.clone().add(xOff, yOff, zOff), offset.getX(), offset.getY(), offset.getZ(), speed, count));
        }

        currentTick++;

        return containers.toArray(new ParticleContainer[0]);
    }

    @Override
    public ParticleContainer[] getParticleContainers(Location location1, Location location2) {
        return new ParticleContainer[0];
    }

    // ----- BUILDER -----

    public static class ParticleStyleParametricBuilder extends ParticleStyleBuilder<ParticleStyleParametric, ParticleStyleParametric.ParticleStyleParametricBuilder> {

        public ParticleStyleParametricBuilder() {
            super();
        }

        public ParticleStyleParametricBuilder(String name) {
            super(name);
        }

        @Override
        public ParticleStyleParametric build() {
            return super.build();
        }

        protected ParticleStyleParametric createObj() { return new ParticleStyleParametric(name); }

        protected ParticleStyleParametric.ParticleStyleParametricBuilder getThis() { return this; }
    }

    // ----- CLONE -----

    public ParticleStyleParametric(ParticleStyleParametric target) {
        super(target);
    }

    @Override
    public ParticleStyle clone() {
        return new ParticleStyleParametric(this);
    }
}
