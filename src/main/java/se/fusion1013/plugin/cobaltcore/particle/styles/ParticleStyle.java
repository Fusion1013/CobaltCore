package se.fusion1013.plugin.cobaltcore.particle.styles;

import dev.jorel.commandapi.arguments.Argument;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.ParticleContainer;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltcore.util.VectorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ParticleStyle implements IParticleStyle, Cloneable {

    // ----- VARIABLES -----

    // Internals
    protected String internalStyleName;
    boolean enabled = true;

    // Display
    String name;

    // Particle
    Particle particle = Particle.FLAME;
    Vector offset = new Vector(0, 0, 0);
    int count = 1;
    double speed = 0;
    Object extra = null;

    // Position
    private Vector positionOffset = new Vector();

    // Rotation
    Vector rotation = new Vector(0, 0, 0);
    double angularVelocityX = 0; // Angular velocity is measured in radians/tick
    double angularVelocityY = 0;
    double angularVelocityZ = 0;

    // Tick skipping
    protected int skipTicks = 0;
    int tick = 0;

    // ----- CONSTRUCTORS -----

    /**
     * Creates a new <code>ParticleStyle</code> with the given parameters.
     * @param internalStyleName the internal name of the <code>ParticleStyle</code>.
     * @param name the name of the <code>ParticleStyle</code>.
     * @param particle the <code>Particle</code> the style uses.
     * @param offset the offset of the <code>ParticleStyle</code>.
     * @param count the particle count.
     * @param speed the particle speed.
     * @param extra extra particle information. Set to null if not used.
     */
    public ParticleStyle(String internalStyleName, String name, Particle particle, Vector offset, int count, double speed, Object extra) {
        this.internalStyleName = internalStyleName;
        this.name = name;
        this.particle = particle;
        this.offset = offset;
        this.count = count;
        this.speed = speed;
        this.extra = extra;
    }

    /**
     * Creates a new <code>ParticleStyle</code> with an internal name.
     *
     * @param internalStyleName the internal name of the particle style.
     * @param name the name of the particle style.
     */
    public ParticleStyle(String internalStyleName, String name) {
        this.internalStyleName = internalStyleName;
        this.name = name;
    }

    /**
     * Creates a clone of a <code>ParticleStyle</code>.
     *
     * @param target the <code>ParticleStyle</code> to clone.
     */
    public ParticleStyle(ParticleStyle target) {
        if (target == null) return;

        this.internalStyleName = target.internalStyleName;
        this.name = target.name;
        this.enabled = target.enabled;

        this.particle = target.particle;
        this.offset = target.offset.clone();
        this.count = target.count;
        this.speed = target.speed;
        this.extra = target.extra;

        this.positionOffset = target.positionOffset.clone();

        this.rotation = target.rotation.clone();
        this.angularVelocityX = target.angularVelocityX;
        this.angularVelocityY = target.angularVelocityY;
        this.angularVelocityZ = target.angularVelocityZ;

        this.skipTicks = target.skipTicks;
        this.tick = target.tick;
    }

    //region DATA_LOADING

    public void loadData(Map<?, ?> data) {
        if (data.containsKey("particle")) particle = EnumUtils.findEnumInsensitiveCase(Particle.class, (String) data.get("particle"));

        if (offset == null) offset = new Vector();
        if (data.containsKey("offset_x")) offset.setX((double) data.get("offset_x"));
        if (data.containsKey("offset_y")) offset.setY((double) data.get("offset_y"));
        if (data.containsKey("offset_z")) offset.setZ((double) data.get("offset_z"));

        if (data.containsKey("count")) count = (int) data.get("count");
        if (data.containsKey("speed")) speed = (double) data.get("speed");

        if (data.containsKey("position_offset_x")) positionOffset.setX((double) data.get("position_offset_x"));
        if (data.containsKey("position_offset_y")) positionOffset.setY((double) data.get("position_offset_y"));
        if (data.containsKey("position_offset_z")) positionOffset.setZ((double) data.get("position_offset_z"));

        if (data.containsKey("skip_ticks")) skipTicks = (int) data.get("skip_ticks");

        // -- Extra particle data
        if (data.containsKey("dust_transition")) {
            Map<?, ?> dustData = (Map<?, ?>) data.get("dust_transition");

            int r1 = (int) dustData.get("r1");
            int g1 = (int) dustData.get("g1");
            int b1 = (int) dustData.get("b1");
            int r2 = (int) dustData.get("r2");
            int g2 = (int) dustData.get("g2");
            int b2 = (int) dustData.get("b2");

            int size = (int) dustData.get("size");

            extra = new Particle.DustTransition(Color.fromBGR(r1, g1, b1), Color.fromRGB(r2, g2, b2), size);
        }
    }

    //endregion

    // ----- ROTATING -----

    public static ParticleContainer[] rotateParticles(ParticleContainer[] particles, Location center, Vector rotation) {
        for (ParticleContainer particleContainer : particles) {
            Vector current = particleContainer.getLocation().toVector();
            Vector delta = new Vector(current.getX() - center.getX(), current.getY() - center.getY(), current.getZ() - center.getZ());
            VectorUtil.rotateVector(delta, rotation.getX(), rotation.getY(), rotation.getZ());
            Vector newPos = new Vector(delta.getX() + center.getX(), delta.getY() + center.getY(), delta.getZ() + center.getZ());
            particleContainer.setLocationPosition(newPos);
        }
        return particles;
    }

    // ----- INFO -----

    @Override
    public List<String> getInfoStrings() {
        List<String> info = new ArrayList<>();
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("name", name)
                .addPlaceholder("particle", particle.name().toLowerCase())
                .addPlaceholder("offset_x", offset.getX())
                .addPlaceholder("offset_y", offset.getY())
                .addPlaceholder("offset_z", offset.getZ())
                .addPlaceholder("count", count)
                .addPlaceholder("speed", speed)
                .addPlaceholder("rotation_x", Math.round(Math.toDegrees(rotation.getX())))
                .addPlaceholder("rotation_y", Math.round(Math.toDegrees(rotation.getY())))
                .addPlaceholder("rotation_z", Math.round(Math.toDegrees(rotation.getZ())))
                .addPlaceholder("angular_velocity_x", Math.toDegrees(angularVelocityX))
                .addPlaceholder("angular_velocity_y", Math.toDegrees(angularVelocityY))
                .addPlaceholder("angular_velocity_z", Math.toDegrees(angularVelocityZ))
                .build();

        info.add(LocaleManager.getInstance().getLocaleMessage("particle.style.abstract.info", placeholders));
        info.add(LocaleManager.getInstance().getLocaleMessage("particle.style.rotation.info", placeholders));
        return info;
    }

    // ----- EXTRA SETTINGS -----

    @Override
    public Argument[] getExtraSettingsArguments() {
        return new Argument[0];
    }

    @Override
    public void setExtraSettings(Object[] args) { }


    // ----- PARTICLE GETTING -----

    @Override
    public ParticleContainer[] getParticles(Location location) {
        // Skip ticks
        if (tick >= skipTicks) {
            tick = 0;
        } else {
            tick++;
            return new ParticleContainer[0];
        }

        // Update rotation of particles
        rotation.setX((rotation.getX() + angularVelocityX) % (Math.PI * 2));
        rotation.setY((rotation.getY() + angularVelocityY) % (Math.PI * 2));
        rotation.setZ((rotation.getZ() + angularVelocityZ) % (Math.PI * 2));

        Location pos = location.clone().add(positionOffset);
        return rotateParticles(getParticleContainers(pos), pos, rotation);
    }

    @Override
    public ParticleContainer[] getParticles(Location location1, Location location2) {
        // Skip ticks
        if (tick >= skipTicks) {
            tick = 0;
        } else {
            tick++;
            return new ParticleContainer[0];
        }

        // Update rotation of particles
        rotation.setX((rotation.getX() + angularVelocityX) % (Math.PI * 2));
        rotation.setY((rotation.getY() + angularVelocityY) % (Math.PI * 2));
        rotation.setZ((rotation.getZ() + angularVelocityZ) % (Math.PI * 2));

        Location pos1 = location1.clone().add(positionOffset);
        Location pos2 = location2.clone().add(positionOffset);
        return rotateParticles(getParticleContainers(pos1, pos2), pos1, rotation);
    }

    public abstract ParticleContainer[] getParticleContainers(Location location);
    public abstract ParticleContainer[] getParticleContainers(Location location1, Location location2);

    // ----- GETTERS / SETTERS -----

    @Override
    public String getInternalName() {
        return internalStyleName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Particle getParticle() {
        return particle;
    }

    @Override
    public Object getData() {
        return extra;
    }

    @Override
    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    @Override
    public void setOffset(Vector offset) {
        this.offset = offset;
    }

    @Override
    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public void setData(Object extra) {
        this.extra = extra;
    }

    @Override
    public void setRotation(Vector rotation) {
        this.rotation = new Vector(Math.toRadians(rotation.getX()), Math.toRadians(rotation.getY()), Math.toRadians(rotation.getZ()));
    }

    @Override
    public void setAngularVelocity(double x, double y, double z) {
        this.angularVelocityX = Math.toRadians(x);
        this.angularVelocityY = Math.toRadians(y);
        this.angularVelocityZ = Math.toRadians(z);
    }

    @Override
    public void updateRotation(Vector rotation, double angularVelocityX, double angularVelocityY, double angularVelocityZ) {
        this.rotation = rotation;
        this.angularVelocityX = angularVelocityX;
        this.angularVelocityY = angularVelocityY;
        this.angularVelocityZ = angularVelocityZ;
    }

    public void setSkipTicks(int skipTicks) {
        this.skipTicks = skipTicks;
    }

    @Override
    public Vector getOffset() {
        return offset;
    }

    @Override
    public double getCount() {
        return count;
    }

    @Override
    public double getSpeed() {
        return speed;
    }

    @Override
    public Vector getRotation() {
        return rotation;
    }

    @Override
    public double getAngularVelocityX() {
        return angularVelocityX;
    }

    @Override
    public double getAngularVelocityY() {
        return angularVelocityY;
    }

    @Override
    public double getAngularVelocityZ() {
        return angularVelocityZ;
    }

    // ----- BUILDER -----

    protected static abstract class ParticleStyleBuilder<T extends ParticleStyle, B extends ParticleStyleBuilder> {

        protected T obj;

        protected String name = "unset";

        Particle particle = Particle.FLAME;
        Vector offset = new Vector(0, 0, 0);
        int count = 1;
        double speed = 0;
        Object extra;

        Vector rotation = new Vector(0, 0, 0);
        double angularVelocityX = 0; // Angular velocity is measured in radians/tick
        double angularVelocityY = 0;
        double angularVelocityZ = 0;

        public ParticleStyleBuilder() {
            this.name = "internal_style";
            obj = createObj();
        }

        public ParticleStyleBuilder(String name){
            this.name = name;
            obj = createObj();
        }

        public T build(){
            obj.setParticle(particle);
            obj.setOffset(offset);
            obj.setCount(count);
            obj.setSpeed(speed);
            obj.setData(extra);
            obj.setRotation(rotation);
            obj.setAngularVelocity(angularVelocityX, angularVelocityY, angularVelocityZ);

            return obj;
        }

        protected abstract T createObj();
        protected abstract B getThis();

        public B setAngularVelocity(double x, double y, double z) {
            this.angularVelocityX = x;
            this.angularVelocityY = y;
            this.angularVelocityZ = z;
            return getThis();
        }

        public B setRotation(Vector rotation) {
            this.rotation = rotation;
            return getThis();
        }

        public B setExtra(Object extra) {
            this.extra = extra;
            return getThis();
        }

        public B setParticle(Particle particle){
            this.particle = particle;
            return getThis();
        }

        public B setOffset(Vector offset){
            this.offset = offset;
            return getThis();
        }

        public B setCount(int count){
            this.count = count;
            return getThis();
        }

        public B setSpeed(double speed){
            this.speed = speed;
            return getThis();
        }

    }

    // ----- CLONING -----

    public abstract ParticleStyle clone();

    /**
     * Creates a clone of an array of <code>ParticleContainer</code>'s.
     *
     * @param particles an array of <code>ParticleContainer</code>'s.
     * @return an array of <code>ParticleContainer</code>'s.
     */
    public static ParticleContainer[] cloneContainers(ParticleContainer[] particles) {
        if (particles == null) return new ParticleContainer[0];
        ParticleContainer[] clone = new ParticleContainer[particles.length];
        for (int i = 0; i < clone.length; i++) clone[i] = particles[i].clone();
        return clone;
    }
}
