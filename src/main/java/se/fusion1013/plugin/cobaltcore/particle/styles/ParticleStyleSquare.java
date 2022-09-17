package se.fusion1013.plugin.cobaltcore.particle.styles;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import org.apache.maven.model.Build;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.ParticleContainer;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

import java.util.ArrayList;
import java.util.List;

public class ParticleStyleSquare extends ParticleStyle {

    // ----- VARIABLES -----

    double radius;
    int iterations;

    // ----- CONSTRUCTORS -----

    public ParticleStyleSquare() {
        super("square", "square_internal");
    }

    public ParticleStyleSquare(String name, Particle particle, Vector offset, int count, double speed, Object extra) {
        super("square", name, particle, offset, count, speed, extra);
    }

    public ParticleStyleSquare(String name) {
        super("square", name);
    }

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
            case "radius" -> radius = (double) value;
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

        radius = (double) args[0];
        iterations = (int) args[1];
    }

    @Override
    public String getExtraSettings() {
        JsonObject jo = new JsonObject();
        jo.addProperty("radius", radius);
        jo.addProperty("iterations", iterations);
        return jo.toString();
    }

    @Override
    public void setExtraSettings(String extra) {
        JsonObject jo = new Gson().fromJson(extra, JsonObject.class);
        radius = jo.get("radius").getAsDouble();
        iterations = jo.get("iterations").getAsInt();
    }

    // ----- METHOD GETTING -----

    @Override
    public ParticleContainer[] getParticleContainers(Location location) {
        List<ParticleContainer> particleContainers = new ArrayList<>();

        for (int i = 0; i < iterations; i++) {
            double gap = (radius*2) / (double) iterations;
            double off = -radius + i * gap;

            particleContainers.add(new ParticleContainer(location.clone().add(off, 0, -radius), offset.getX(), offset.getY(), offset.getZ(), speed, count));
            particleContainers.add(new ParticleContainer(location.clone().add(-radius, 0, off), offset.getX(), offset.getY(), offset.getZ(), speed, count));
            particleContainers.add(new ParticleContainer(location.clone().add(off, 0, radius), offset.getX(), offset.getY(), offset.getZ(), speed, count));
            particleContainers.add(new ParticleContainer(location.clone().add(radius, 0, off), offset.getX(), offset.getY(), offset.getZ(), speed, count));
        }

        return particleContainers.toArray(new ParticleContainer[0]);
    }

    @Override
    public ParticleContainer[] getParticleContainers(Location location1, Location location2) {
        return new ParticleContainer[0];
    }

    // ----- BUILDER -----

    public static class Builder extends ParticleStyleBuilder<ParticleStyleSquare, ParticleStyleSquare.Builder> {

        double radius = 1;
        int iterations = 16;

        public Builder() {
            super();
        }

        public Builder(String name) {
            super(name);
        }

        @Override
        public ParticleStyleSquare build() {
            obj.radius = radius;
            obj.iterations = iterations;

            return super.build();
        }

        protected ParticleStyleSquare createObj() { return new ParticleStyleSquare(name); }

        protected ParticleStyleSquare.Builder getThis() { return this; }

        public Builder setRadius(double radius) {
            this.radius = radius;
            return getThis();
        }

        public Builder setIterations(int iterations) {
            this.iterations = iterations;
            return getThis();
        }

    }

    // ----- GETTERS / SETTERS -----

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public double getRadius() {
        return radius;
    }

    public int getIterations() {
        return iterations;
    }

    // ----- CLONE METHOD & CONSTRUCTOR -----

    public ParticleStyleSquare(ParticleStyleSquare target) {
        super(target);

        this.radius = target.radius;
        this.iterations = target.iterations;
    }

    @Override
    public ParticleStyleSquare clone() {
        return new ParticleStyleSquare(this);
    }
}
