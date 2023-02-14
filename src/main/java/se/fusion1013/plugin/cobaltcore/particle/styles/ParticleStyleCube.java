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
import se.fusion1013.plugin.cobaltcore.util.VectorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParticleStyleCube extends ParticleStyle implements IParticleStyle {

    // ----- VARIABLES -----

    private double edgeLength = 1;
    private int particlesPerEdge = 4;

    // TODO: Cube modes (edge, face, full)

    // ----- CONSTRUCTORS -----

    public ParticleStyleCube() {
        super("cube", "cube_internal");
    }

    public ParticleStyleCube(String name, Particle particle, Vector offset, int count, double speed, Object extra) {
        super("cube", name, particle, offset, count, speed, extra);
    }

    public ParticleStyleCube(String name) {
        super("cube", name);
    }

    //region DATA_LOADING

    @Override
    public void loadData(Map<?, ?> data) {
        super.loadData(data);

        if (data.containsKey("edge_length")) edgeLength = (double) data.get("edge_length");
        if (data.containsKey("particles_per_edge")) particlesPerEdge = (int) data.get("particles_per_edge");
    }

    //endregion

    // ----- INFO -----

    @Override
    public List<String> getInfoStrings() {
        List<String> info = super.getInfoStrings();
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("edge_length", edgeLength)
                .addPlaceholder("particles_per_edge", particlesPerEdge)
                .build();
        info.add(LocaleManager.getInstance().getLocaleMessage("particle.style.cube.info", placeholders));
        return info;
    }


    // ----- SET EXTRA SETTINGS -----

    @Override
    public void setExtraSetting(String key, Object value) {
        switch (key) {
            case "edgeLength" -> edgeLength = (double) value;
            case "particlesPerEdge" -> particlesPerEdge = (int) value;
        }
    }

    @Override
    public Argument[] getExtraSettingsArguments() {
        List<Argument> arguments = new ArrayList<>();

        arguments.add(new DoubleArgument("edgeLength"));
        arguments.add(new IntegerArgument("particlesPerEdge", 0, Integer.MAX_VALUE));

        return arguments.toArray(new Argument[0]);
    }

    @Override
    public void setExtraSettings(Object[] args) {
        super.setExtraSettings(args);

        edgeLength = (double) args[0];
        particlesPerEdge = (int) args[1];
    }

    @Override
    public String getExtraSettings() {
        JsonObject jo = new JsonObject();
        jo.addProperty("edge_length", edgeLength);
        jo.addProperty("particles_per_edge", particlesPerEdge);
        return jo.toString();
    }

    @Override
    public void setExtraSettings(String extra) {
        JsonObject jsonObject = new Gson().fromJson(extra, JsonObject.class);
        edgeLength = jsonObject.get("edge_length").getAsDouble();
        particlesPerEdge = jsonObject.get("particles_per_edge").getAsInt();
    }

    // ----- PARTICLE GETTING -----

    @Override
    public ParticleContainer[] getParticleContainers(Location location) {
        List<ParticleContainer> particles = new ArrayList<>();

        double a = this.edgeLength / 2;
        double angleX, angleY;
        Vector v = new Vector();
        for (int i = 0; i < 4; i++) {
            angleY = i * Math.PI / 2;
            for (int j = 0; j < 2; j++) {
                angleX = j * Math.PI;
                for (int p = 0; p <= this.particlesPerEdge; p++) {
                    v.setX(a).setY(a);
                    v.setZ(this.edgeLength * p / this.particlesPerEdge - a);
                    VectorUtil.rotateAroundAxisX(v, angleX);
                    VectorUtil.rotateAroundAxisY(v, angleY);
                    particles.add(new ParticleContainer(location.clone().add(v), offset.getX(), offset.getY(), offset.getZ(), speed, count));
                }
            }
            for (int p = 0; p <= this.particlesPerEdge; p++) {
                v.setX(a).setZ(a);
                v.setY(this.edgeLength * p / this.particlesPerEdge - a);
                VectorUtil.rotateAroundAxisY(v, angleY);
                particles.add(new ParticleContainer(location.clone().add(v), offset.getX(), offset.getY(), offset.getZ(), speed, count));
            }
        }

        return particles.toArray(new ParticleContainer[0]);
        // return particles.toArray(new ParticleContainer[0]);
    }

    @Override
    public ParticleContainer[] getParticleContainers(Location location1, Location location2) {
        return new ParticleContainer[0];
    }

    // ----- BUILDER -----

    public static class ParticleStyleCubeBuilder extends ParticleStyleBuilder<ParticleStyleCube, ParticleStyleCubeBuilder>{

        private double edgeLength = 1;
        private int particlesPerEdge = 4;

        public ParticleStyleCubeBuilder() {
            super();
        }

        public ParticleStyleCubeBuilder(String name) {
            super(name);
        }

        @Override
        public ParticleStyleCube build() {

            obj.setEdgeLength(edgeLength);
            obj.setParticlesPerEdge(particlesPerEdge);

            return super.build();
        }

        public ParticleStyleCubeBuilder setEdgeLength(double edgeLength){
            this.edgeLength = edgeLength;
            return getThis();
        }

        public ParticleStyleCubeBuilder setParticlesPerEdge(int particlesPerEdge) {
            this.particlesPerEdge = particlesPerEdge;
            return getThis();
        }

        @Override
        protected ParticleStyleCube createObj() {
            return new ParticleStyleCube();
        }

        @Override
        protected ParticleStyleCubeBuilder getThis() {
            return this;
        }
    }

    // ----- GETTERS / SETTERS -----

    public double getEdgeLength() {
        return edgeLength;
    }

    public int getParticlesPerEdge() {
        return particlesPerEdge;
    }

    public void setEdgeLength(double edgeLength) {
        this.edgeLength = edgeLength;
    }

    public void setParticlesPerEdge(int particlesPerEdge) {
        this.particlesPerEdge = particlesPerEdge;
    }


    // ----- CLONE -----

    public ParticleStyleCube(ParticleStyleCube target) {
        super(target);
        this.edgeLength = target.edgeLength;
        this.particlesPerEdge = target.particlesPerEdge;
    }

    @Override
    public ParticleStyleCube clone() {
        return new ParticleStyleCube(this);
    }
}
