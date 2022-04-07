package se.fusion1013.plugin.cobaltcore.particle.styles;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.GeometryUtil;
import se.fusion1013.plugin.cobaltcore.util.ParticleContainer;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

import java.util.ArrayList;
import java.util.List;

public class ParticleStyleSphere extends ParticleStyle {

    // ----- VARIABLES -----

    private int density = 150;
    private double radius = 5;
    private boolean inSphere;

    // ----- CONSTRUCTORS -----

    public ParticleStyleSphere() {
        super("sphere", "sphere_internal");
    }

    public ParticleStyleSphere(String name, Particle particle, Vector offset, int count, double speed, Object extra) {
        super("sphere", name, particle, offset, count, speed, extra);
    }

    public ParticleStyleSphere(String name) {
        super("sphere", name);
    }

    // ----- INFO -----

    @Override
    public List<String> getInfoStrings() {
        List<String> info = super.getInfoStrings();
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("density", density)
                .addPlaceholder("radius", radius)
                .addPlaceholder("in_sphere", inSphere)
                .build();

        info.add(LocaleManager.getInstance().getLocaleMessage("particle.style.sphere.info", placeholders));
        return info;
    }

    // ----- SET EXTRA SETTINGS -----

    @Override
    public Argument[] getExtraSettingsArguments() {
        List<Argument> arguments = new ArrayList<>();

        arguments.add(new IntegerArgument("density"));
        arguments.add(new DoubleArgument("radius"));
        arguments.add(new BooleanArgument("inSphere"));

        return arguments.toArray(new Argument[0]);
    }

    @Override
    public void setExtraSettings(Object[] args) {
        super.setExtraSettings(args);

        density = (Integer) args[0];
        radius = (Double) args[1];
        inSphere = (Boolean) args[2];
    }

    @Override
    public String getExtraSettings() {
        JsonObject jo = new JsonObject();
        jo.addProperty("density", density);
        jo.addProperty("radius", radius);
        jo.addProperty("in_sphere", inSphere);
        return jo.toString();
    }

    @Override
    public void setExtraSettings(String extra) {
        JsonObject jsonObject = new Gson().fromJson(extra, JsonObject.class);
        density = jsonObject.get("density").getAsInt();
        radius = jsonObject.get("radius").getAsDouble();
        inSphere = jsonObject.get("in_sphere").getAsBoolean();
    }

    // ----- PARTICLE GETTING -----

    @Override
    public ParticleContainer[] getParticleContainers(Location location){
        if (inSphere) return particlesInSphere(location);
        else return particlesOnSphere(location);
    }

    @Override
    public ParticleContainer[] getParticleContainers(Location location1, Location location2) {
        return new ParticleContainer[0];
    }

    private ParticleContainer[] particlesOnSphere(Location center){
        List<ParticleContainer> particles = new ArrayList<>();

        for (int i = 0; i < this.density; i++){
            particles.add(new ParticleContainer(center.clone().add(GeometryUtil.getPointOnSphere(radius)), offset.getX(), offset.getY(), offset.getZ(), speed, count));
        }

        return particles.toArray(new ParticleContainer[0]);
    }

    /**
     * Creates an array of particles inside a sphere volume depending on the parameters of the <code></code>
     * @param center the center of the sphere.
     * @return an array of <code>ParticleContainer</code>'s.
     */
    private ParticleContainer[] particlesInSphere(Location center){
        List<ParticleContainer> particles = new ArrayList<>();

        for (int i = 0; i < this.density; i++){
            particles.add(new ParticleContainer(center.clone().add(GeometryUtil.getPointInSphere(radius)), offset.getX(), offset.getY(), offset.getZ(), speed, count));
        }

        return particles.toArray(new ParticleContainer[0]);
    }

    // ----- BUILDER -----

    public static class ParticleStyleSphereBuilder extends ParticleStyleBuilder<ParticleStyleSphere, ParticleStyleSphereBuilder> {

        double targetRadius = 1;
        double startRadius = 0;
        boolean animateRadius = false;
        int expandTime = 1;

        int density = 1; // TODO: Change default value
        boolean inSphere;

        @Override
        public ParticleStyleSphere build(){
            obj.setRadius(targetRadius);
            obj.setDensity(density);
            obj.setInSphere(inSphere);
            return super.build();
        }

        protected ParticleStyleSphere createObj() { return new ParticleStyleSphere(); }

        protected ParticleStyleSphere.ParticleStyleSphereBuilder getThis() { return this; }

        public ParticleStyleSphereBuilder animateRadius(double startRadius, int expandTime){
            this.startRadius = startRadius;
            this.expandTime = expandTime;
            this.animateRadius = true;
            return getThis();
        }

        public ParticleStyleSphereBuilder setRadius(double targetRadius){
            this.targetRadius = targetRadius;
            return getThis();
        }

        public ParticleStyleSphereBuilder setDensity(int density){
            this.density = density;
            return getThis();
        }

        public ParticleStyleSphereBuilder setInSphere(){
            this.inSphere = true;
            return getThis();
        }
    }

    // ----- GETTERS / SETTERS -----

    public void setDensity(int density) {
        this.density = density;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setInSphere(boolean inSphere) {
        this.inSphere = inSphere;
    }


    // ----- CLONE -----

    public ParticleStyleSphere(ParticleStyleSphere target) {
        super(target);

        this.density = target.density;
        this.radius = target.radius;

        this.inSphere = target.inSphere;
    }

    @Override
    public ParticleStyleSphere clone() {
        return new ParticleStyleSphere(this);
    }
}
