package se.fusion1013.plugin.cobaltcore.particle.styles;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.simulation.boids.Boids;
import se.fusion1013.plugin.cobaltcore.simulation.boids.Vector;
import se.fusion1013.plugin.cobaltcore.util.ParticleContainer;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

import java.util.ArrayList;
import java.util.List;

public class ParticleStyleBoids extends ParticleStyle implements IParticleStyle, Cloneable {

    // ----- VARIABLES -----

    private Boids boids;

    private int amount = 10;
    private int width = 1000;
    private int height = 1000;
    private int depth = 1000;

    private int moveDistance = 50;
    private double cohesionCoefficient = 100.0;
    private int alignmentCoefficient = 8;
    private double separationCoefficient = 10.0;

    // ----- CONSTRUCTORS -----

    public ParticleStyleBoids() {
        super("boids", "boids_internal");
    }

    public ParticleStyleBoids(String name) {
        super("boids", name);
    }

    public ParticleStyleBoids(ParticleStyleBoids target) {
        super(target);

        this.amount = target.amount;
        this.width = target.width;
        this.height = target.height;
        this.depth = target.depth;

        this.boids = new Boids(amount, width, height, depth);

        this.moveDistance = target.moveDistance;
        this.cohesionCoefficient = target.cohesionCoefficient;
        this.alignmentCoefficient = target.alignmentCoefficient;
        this.separationCoefficient = target.separationCoefficient;
    }

    // ----- PARTIClE GETTING -----

    @Override
    public ParticleContainer[] getParticleContainers(Location location) {
        List<ParticleContainer> particles = new ArrayList<>();

        boids.move(moveDistance, cohesionCoefficient, alignmentCoefficient, separationCoefficient);
        List<Vector> positions = boids.getPositions();

        for (Vector position : positions) {
            particles.add(new ParticleContainer(location.clone().add(
                    (position.data[0] / 100.0) - (width / 200.0),
                    (position.data[1] / 100.0) - (height / 200.0),
                    (position.data[2] / 100.0) - (depth / 200.0)),
                    offset.getX(), offset.getY(), offset.getZ(), speed, count));
        }

        return particles.toArray(new ParticleContainer[0]);
    }

    @Override
    public ParticleContainer[] getParticleContainers(Location location1, Location location2) {
        return new ParticleContainer[0];
    }

    // ----- INFO -----

    @Override
    public List<String> getInfoStrings() {
        List<String> info = super.getInfoStrings();

        LocaleManager locale = LocaleManager.getInstance();
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("move_distance", moveDistance/100)
                .addPlaceholder("cohesion", cohesionCoefficient)
                .addPlaceholder("alignment", alignmentCoefficient)
                .addPlaceholder("separation", separationCoefficient)
                .addPlaceholder("amount", amount)
                .addPlaceholder("width", width/100)
                .addPlaceholder("height", height/100)
                .addPlaceholder("depth", depth/100)
                .build();
        info.add(locale.getLocaleMessage("particle.style.boids.info.1", placeholders));
        info.add(locale.getLocaleMessage("particle.style.boids.info.2", placeholders));
        info.add(locale.getLocaleMessage("particle.style.boids.info.3", placeholders));
        info.add(locale.getLocaleMessage("particle.style.boids.info.4", placeholders));
        info.add(locale.getLocaleMessage("particle.style.boids.info.5", placeholders));

        return info;
    }


    // ----- EXTRA -----

    @Override
    public void setExtraSetting(String key, Object value) {
        switch (key) {
            case "move_distance" -> moveDistance = (int) ((double) value * 100);
            case "cohesion_coefficient" -> cohesionCoefficient = (double) value;
            case "alignment_coefficient" -> alignmentCoefficient = (int) value;
            case "separation_coefficient" -> separationCoefficient = (double) value;
            case "amount" -> {
                amount = (int) value;
                boids = new Boids(amount, width, height, depth);
            }
            case "width" -> {
                width = (int) value * 100;
                boids.xRes = width;
            }
            case "height" -> {
                height = (int) value * 100;
                boids.yRes = height;
            }
            case "depth" -> {
                depth = (int) value * 100;
                boids.zRes = depth;
            }
        }
    }

    @Override
    public Argument[] getExtraSettingsArguments() {
        return new Argument[] {
                new DoubleArgument("move_distance"),
                new DoubleArgument("cohesion_coefficient"),
                new IntegerArgument("alignment_coefficient"),
                new DoubleArgument("separation_coefficient"),
                new IntegerArgument("amount"),
                new IntegerArgument("width"),
                new IntegerArgument("height"),
                new IntegerArgument("depth")
        };
    }

    @Override
    public void setExtraSettings(Object[] args) {
        super.setExtraSettings(args);

        moveDistance = (int) args[0];
        cohesionCoefficient = (double) args[1];
        alignmentCoefficient = (int) args[2];
        separationCoefficient = (double) args[3];
        amount = (int) args[4];
        width = (int) args[5];
        height = (int) args[6];
        depth = (int) args[7];

        boids = new Boids(amount, width, height, depth);
    }

    @Override
    public String getExtraSettings() {
        JsonObject jo = new JsonObject();
        jo.addProperty("move_distance", moveDistance);
        jo.addProperty("cohesion_coefficient", cohesionCoefficient);
        jo.addProperty("alignment_coefficient", alignmentCoefficient);
        jo.addProperty("separation_coefficient", separationCoefficient);
        jo.addProperty("amount", amount);
        jo.addProperty("width", width);
        jo.addProperty("height", height);
        jo.addProperty("depth", depth);
        return jo.toString();
    }

    @Override
    public void setExtraSettings(String extra) {
        JsonObject jsonObject = new Gson().fromJson(extra, JsonObject.class);
        moveDistance = jsonObject.get("move_distance").getAsInt();
        cohesionCoefficient = jsonObject.get("cohesion_coefficient").getAsDouble();
        alignmentCoefficient = jsonObject.get("alignment_coefficient").getAsInt();
        separationCoefficient = jsonObject.get("separation_coefficient").getAsDouble();
        amount = jsonObject.get("amount").getAsInt();
        width = jsonObject.get("width").getAsInt();
        height = jsonObject.get("height").getAsInt();
        depth = jsonObject.get("depth").getAsInt();

        boids = new Boids(amount, width, height, depth);
    }

    // ----- CLONE -----

    @Override
    public ParticleStyleBoids clone() {
        return new ParticleStyleBoids(this);
    }

}
