package se.fusion1013.plugin.cobaltcore.particle.styles;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import org.bukkit.*;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.ParticleContainer;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

import java.util.ArrayList;
import java.util.List;

public class ParticleStyleLine extends ParticleStyle {

    // ----- VARIABLES -----

    private int density = 8;
    private Location location2;

    // ----- CONSTRUCTORS -----

    public ParticleStyleLine() {
        super("line", "line_internal");
    }

    public ParticleStyleLine(String name, Particle particle, Vector offset, int count, double speed, Object extra) {
        super("line", name, particle, offset, count, speed, extra);
    }

    public ParticleStyleLine(String name) {
        super("line", name);
    }

    // ----- INFO -----

    @Override
    public List<String> getInfoStrings() {
        List<String> info = super.getInfoStrings();
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("density", density)
                .addPlaceholder("location2", location2)
                .build();
        info.add(LocaleManager.getInstance().getLocaleMessage("particle.style.line.info", placeholders));
        return info;
    }

    // ----- SET EXTRA SETTINGS -----

    @Override
    public void setExtraSetting(String key, Object value) {
        switch (key) {
            case "density" -> density = (int) value;
            case "location2" -> location2 = (Location) value;
        }
    }

    @Override
    public Argument[] getExtraSettingsArguments() {
        List<Argument> arguments = new ArrayList<>();
        arguments.add(new IntegerArgument("density"));
        arguments.add(new LocationArgument("location2"));
        return arguments.toArray(new Argument[0]);
    }

    @Override
    public void setExtraSettings(Object[] args) {
        super.setExtraSettings(args);

        density = (Integer) args[0];
        location2 = (Location) args[1];
    }

    @Override
    public String getExtraSettings() {
        JsonObject jo = new JsonObject();
        jo.addProperty("density", density);
        jo.addProperty("world", location2.getWorld().toString());
        jo.addProperty("location2_x", location2.getX());
        jo.addProperty("location2_y", location2.getY());
        jo.addProperty("location2_z", location2.getZ());
        return jo.toString();
    }

    @Override
    public void setExtraSettings(String extra) {
        JsonObject jsonObject = new Gson().fromJson(extra, JsonObject.class);
        density = jsonObject.get("density").getAsInt();
        World world = Bukkit.getWorld(jsonObject.get("world").getAsString());
        location2 = new Location(world, jsonObject.get("location2_x").getAsDouble(), jsonObject.get("location2_y").getAsDouble(), jsonObject.get("location2_z").getAsDouble());
    }

    // ----- PARTICLE GETTING -----

    @Override
    public ParticleContainer[] getParticleContainers(Location location) {
        if (location2 != null) return getParticleContainers(location, location2);
        return new ParticleContainer[0];
    }

    @Override
    public ParticleContainer[] getParticleContainers(Location startLocation, Location endLocation) {
        // return drawLine(startLocation, endLocation, steps);

        List<ParticleContainer> particles = new ArrayList<>();

        double distance = startLocation.distance(endLocation);
        int steps = (int)Math.round(density * distance);

        Vector direction = endLocation.clone().subtract(startLocation).toVector().normalize();

        for (int i = 0; i < steps; i++) {
            Location location = startLocation.clone().add(direction.clone().multiply((double)i / (double)density));
            particles.add(new ParticleContainer(location, offset.getX(), offset.getY(), offset.getZ(), speed, count));
        }

        return particles.toArray(new ParticleContainer[0]);
    }

    public ParticleContainer[] drawLine(Location point1, Location point2, double space) {
        List<ParticleContainer> particles = new ArrayList<>();
        World world = point1.getWorld();
        if (!point2.getWorld().equals(world)) CobaltCore.getInstance().getLogger().info("Lines cannot be in different worlds!");
        double distance = point1.distance(point2);
        Vector p1 = point1.toVector();
        Vector p2 = point2.toVector();
        Vector vector = p2.clone().subtract(p1).normalize().multiply(space);
        double length = 0;
        for (; length < distance; p1.add(vector)) {
            length += space;
            particles.add(new ParticleContainer(new Location(world, p1.getX(), p1.getY(), p1.getZ()), offset.getX(), offset.getY(), offset.getZ(), speed, count));
        }
        return particles.toArray(new ParticleContainer[0]);
    }

    // ----- BUILDER -----

    public static class ParticleStyleLineBuilder extends ParticleStyleBuilder<ParticleStyleLine, ParticleStyleLineBuilder> {

        int density = 8;
        Location location2 = null;

        public ParticleStyleLineBuilder() {
            super();
        }

        public ParticleStyleLineBuilder(String name) {
            super(name);
        }

        @Override
        public ParticleStyleLine build() {
            obj.setDensity(density);
            obj.setLocation2(location2);

            return super.build();
        }

        public ParticleStyleLineBuilder setLocation2(Location location2) {
            this.location2 = location2;
            return getThis();
        }

        public ParticleStyleLineBuilder setDensity(int density) {
            this.density = density;
            return getThis();
        }

        @Override
        protected ParticleStyleLine createObj() {
            return new ParticleStyleLine();
        }

        @Override
        protected ParticleStyleLineBuilder getThis() {
            return this;
        }
    }

    // ----- GETTERS / SETTERS -----

    public void setDensity(int density) {
        this.density = density;
    }

    public void setLocation2(Location location2) {
        this.location2 = location2;
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public ParticleStyleLine(ParticleStyleLine target) {
        super(target);

        this.density = target.density;
        if (target.location2 != null) this.location2 = target.location2.clone();
    }

    @Override
    public ParticleStyleLine clone() {
        return new ParticleStyleLine(this);
    }
}
