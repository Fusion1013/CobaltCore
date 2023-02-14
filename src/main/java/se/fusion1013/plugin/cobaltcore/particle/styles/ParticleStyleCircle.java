package se.fusion1013.plugin.cobaltcore.particle.styles;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.animated.AnimatedDouble;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.ParticleContainer;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParticleStyleCircle extends ParticleStyle {

    // ----- VARIABLES -----

    private AnimatedDouble radius;
    private int iterations;

    // ----- CONSTRUCTORS -----

    public ParticleStyleCircle() {
        super("circle", "circle_internal");
    }

    public ParticleStyleCircle(String name, Particle particle, Vector offset, int count, double speed, Object extra) {
        super("circle", name, particle, offset, count, speed, extra);
    }

    public ParticleStyleCircle(String name) {
        super("circle", name);
    }

    //region DATA_LOADING

    @Override
    public void loadData(Map<?, ?> data) {
        super.loadData(data);

        if (data.containsKey("radius")) radius = new AnimatedDouble((double) data.get("radius"), (double) data.get("radius"), 0, false);
        if (data.containsKey("iterations")) iterations = (int) data.get("iterations");
    }

    //endregion

    // ----- INFO -----

    @Override
    public List<String> getInfoStrings() {
        List<String> info = super.getInfoStrings();
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("radius", radius)
                .addPlaceholder("iterations", iterations)
                .build();
        info.add(LocaleManager.getInstance().getLocaleMessage("particle.style.circle.info", placeholders));
        return info;
    }

    // ----- SET EXTRA SETTINGS -----

    @Override
    public void setExtraSetting(String key, Object value) {
        switch (key) {
            case "radius" -> radius = new AnimatedDouble((double) value, (double) value, 0, false);
            case "iterations" -> iterations = (int) value;
        }
    }

    @Override
    public Argument[] getExtraSettingsArguments() {
        List<Argument> arguments = new ArrayList<>();

        arguments.add(new DoubleArgument("radius"));
        arguments.add(new IntegerArgument("iterations"));

        return arguments.toArray(new Argument[0]);
    }

    @Override
    public void setExtraSettings(Object[] args) {
        super.setExtraSettings(args);

        radius = new AnimatedDouble((double) args[0], (double) args[0], 0, false);
        iterations = (int) args[1];
    }

    @Override
    public String getExtraSettings() {
        JsonObject jo = new JsonObject();
        jo.addProperty("radius", radius.getStartValue());
        jo.addProperty("iterations", iterations);
        return jo.toString();
    }

    @Override
    public void setExtraSettings(String extra) {
        JsonObject jo = new Gson().fromJson(extra, JsonObject.class);
        radius = new AnimatedDouble(jo.get("radius").getAsDouble(), jo.get("radius").getAsDouble(), 0, false);
        iterations = jo.get("iterations").getAsInt();
    }

    // ----- METHOD GETTING -----

    @Override
    public ParticleContainer[] getParticleContainers(Location location) {
        List<ParticleContainer> particleContainers = new ArrayList<>();

        for (int i = 0; i < iterations; i++) {
            double angle = ((Math.PI * 2) / iterations) * i;
            double x = Math.cos(angle)*radius.getValue();
            double z = Math.sin(angle)*radius.getValue();

            particleContainers.add(new ParticleContainer(location.clone().add(x, 0, z), offset.getX(), offset.getY(), offset.getZ(), speed, count));
        }

        return particleContainers.toArray(new ParticleContainer[0]);
    }

    @Override
    public ParticleContainer[] getParticleContainers(Location location1, Location location2) {
        return new ParticleContainer[0];
    }

    // ----- BUILDER -----

    public static class ParticleStyleCircleBuilder extends ParticleStyleBuilder<ParticleStyleCircle, ParticleStyleCircle.ParticleStyleCircleBuilder> {

        AnimatedDouble radius = new AnimatedDouble(1, 1, 0, false);
        int iterations = 16;

        public ParticleStyleCircleBuilder() {
            super();
        }

        public ParticleStyleCircleBuilder(String name) {
            super(name);
        }

        @Override
        public ParticleStyleCircle build() {
            obj.radius = radius;
            obj.iterations = iterations;

            return super.build();
        }

        protected ParticleStyleCircle createObj() { return new ParticleStyleCircle(name); }

        protected ParticleStyleCircle.ParticleStyleCircleBuilder getThis() { return this; }

        public ParticleStyleCircleBuilder setRadius(double radius) {
            this.radius = new AnimatedDouble(radius, radius, 0, false);
            return getThis();
        }

        public ParticleStyleCircleBuilder setRadius(AnimatedDouble radius) {
            this.radius = radius;
            return getThis();
        }

        public ParticleStyleCircleBuilder setIterations(int iterations) {
            this.iterations = iterations;
            return getThis();
        }

    }

    // ----- GETTERS / SETTERS -----

    public void setRadius(double radius) {
        this.radius = new AnimatedDouble(radius, radius, 0, false);
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public double getRadius() {
        return radius.getValue();
    }

    public int getIterations() {
        return iterations;
    }

    // ----- CLONE METHOD & CONSTRUCTOR -----

    public ParticleStyleCircle(ParticleStyleCircle target) {
        super(target);

        this.radius = target.radius;
        this.iterations = target.iterations;
    }

    @Override
    public ParticleStyleCircle clone() {
        return new ParticleStyleCircle(this);
    }
}
