package se.fusion1013.plugin.cobaltcore.particle.styles;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.ParticleContainer;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltcore.util.VectorUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParticleStyleIcosphere extends ParticleStyle implements IParticleStyle, Cloneable {

    // ----- VARIABLES -----

    private double ticksPerSpawn = 50;
    private double radius = 3;
    private int particlesPerLine = 8;
    private int divisions = 1;
    private int step = 1;

    // ----- CONSTRUCTORS -----

    public ParticleStyleIcosphere() {
        super("icosphere", "icosphere_internal");
    }

    public ParticleStyleIcosphere(String name, Particle particle, Vector offset, int count, double speed, Object extra) {
        super("icosphere", name, particle, offset, count, speed, extra);
    }

    public ParticleStyleIcosphere(String name) {
        super("icosphere", name);
    }

    // ----- INFO -----

    @Override
    public List<String> getInfoStrings() {
        List<String> info = super.getInfoStrings();
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("ticks_per_spawn", ticksPerSpawn)
                .addPlaceholder("radius", radius)
                .addPlaceholder("particles_per_line", particlesPerLine)
                .addPlaceholder("divisions", divisions)
                .build();
        info.add(LocaleManager.getInstance().getLocaleMessage("particle.style.icosphere.info", placeholders));
        return info;
    }

    // ----- SET EXTRA SETTINGS -----

    @Override
    public void setExtraSetting(String key, Object value) {
        switch (key) {
            case "ticksPerSpawn" -> ticksPerSpawn = (double) value;
            case "radius" -> radius = (double) value;
            case "particlesPerLine" -> particlesPerLine = (int) value;
            case "divisions" -> divisions = (int) value;
        }
    }

    @Override
    public Argument[] getExtraSettingsArguments() {
        List<Argument> arguments = new ArrayList<>();

        arguments.add(new DoubleArgument("ticksPerSpawn"));
        arguments.add(new DoubleArgument("radius"));
        arguments.add(new IntegerArgument("particlesPerLine"));
        arguments.add(new IntegerArgument("divisions"));

        return arguments.toArray(new Argument[0]);
    }

    @Override
    public void setExtraSettings(Object[] args) {
        super.setExtraSettings(args);

        this.ticksPerSpawn = (double)args[0];
        this.radius = (double)args[1];
        this.particlesPerLine = (int)args[2];
        this.divisions = (int)args[3];
    }

    @Override
    public String getExtraSettings() {
        JsonObject jo = new JsonObject();
        jo.addProperty("ticks_per_spawn", ticksPerSpawn);
        jo.addProperty("radius", radius);
        jo.addProperty("particles_per_line", particlesPerLine);
        jo.addProperty("divisions", divisions);
        return jo.toString();
    }

    @Override
    public void setExtraSettings(String extra) {
        JsonObject jsonObject = new Gson().fromJson(extra, JsonObject.class);
        ticksPerSpawn = jsonObject.get("ticks_per_spawn").getAsDouble();
        radius = jsonObject.get("radius").getAsDouble();
        particlesPerLine = jsonObject.get("particles_per_line").getAsInt();
        divisions = jsonObject.get("divisions").getAsInt();
    }

    // ----- PARTICLE GETTING -----

    @Override
    public ParticleContainer[] getParticleContainers(Location location) {
        List<ParticleContainer> particles = new ArrayList<>();
        if (this.step < ticksPerSpawn) {
            this.step++;
            return particles.toArray(new ParticleContainer[0]);
        }

        Icosahedron icosahedron = new Icosahedron(this.divisions, this.radius);
        Set<Vector> points = new HashSet<>();
        for (Icosahedron.Triangle triangle : icosahedron.getTriangles())
            points.addAll(this.getPointsAlongTriangle(triangle, this.particlesPerLine));

        double multiplier = ((double) this.step / this.ticksPerSpawn);
        double xRotation = multiplier * this.angularVelocityX;
        double yRotation = multiplier * this.angularVelocityY;
        double zRotation = multiplier * this.angularVelocityZ;

        for (Vector point : points) {
            VectorUtil.rotateVector(point, xRotation, yRotation, zRotation);
            particles.add(new ParticleContainer(location.clone().add(point), offset.getX(), offset.getY(), offset.getZ(), speed, count));
        }

        this.step = 0;

        return particles.toArray(new ParticleContainer[0]);
    }

    @Override
    public ParticleContainer[] getParticleContainers(Location location1, Location location2) {
        return new ParticleContainer[0];
    }

    private Set<Vector> getPointsAlongTriangle(Icosahedron.Triangle triangle, int pointsPerLine) {
        Set<Vector> points = new HashSet<>();
        points.addAll(this.getPointsAlongLine(triangle.point1, triangle.point2, pointsPerLine));
        points.addAll(this.getPointsAlongLine(triangle.point2, triangle.point3, pointsPerLine));
        points.addAll(this.getPointsAlongLine(triangle.point3, triangle.point1, pointsPerLine));
        return points;
    }

    private Set<Vector> getPointsAlongLine(Vector point1, Vector point2, int pointsPerLine) {
        double distance = point1.distance(point2);
        Vector angle = point2.clone().subtract(point1).normalize();
        double distanceBetween = distance / pointsPerLine;

        Set<Vector> points = new HashSet<>();
        for (double i = 0; i < distance; i += distanceBetween)
            points.add(point1.clone().add(angle.clone().multiply(i)));

        return points;
    }

    // ----- BUILDER -----

    public static class ParticleStyleIcosphereBuilder extends ParticleStyleBuilder<ParticleStyleIcosphere, ParticleStyleIcosphereBuilder>{

        double ticksPerSpawn;
        double radius;
        int particlesPerLine;
        int divisions;
        double angularVelocityX;
        double angularVelocityY;
        double angularVelocityZ;

        public ParticleStyleIcosphereBuilder() {
            super();
        }

        public ParticleStyleIcosphereBuilder(String name) {
            super(name);
        }

        @Override
        public ParticleStyleIcosphere build() {
            obj.ticksPerSpawn = ticksPerSpawn;
            obj.radius = radius;
            obj.particlesPerLine = particlesPerLine;
            obj.divisions = divisions;
            obj.angularVelocityX = angularVelocityX;
            obj.angularVelocityY = angularVelocityY;
            obj.angularVelocityZ = angularVelocityZ;

            return super.build();
        }

        @Override
        protected ParticleStyleIcosphere createObj() {
            return new ParticleStyleIcosphere();
        }

        @Override
        protected ParticleStyleIcosphereBuilder getThis() {
            return this;
        }

        public ParticleStyleIcosphereBuilder setTicksPerSpawn(double ticksPerSpawn){
            this.ticksPerSpawn = ticksPerSpawn;
            return getThis();
        }

        public ParticleStyleIcosphereBuilder setRadius(double radius) {
            this.radius = radius;
            return getThis();
        }

        public ParticleStyleIcosphereBuilder setParticlesPerLine(int particlesPerLine) {
            this.particlesPerLine = particlesPerLine;
            return getThis();
        }

        public ParticleStyleIcosphereBuilder setDivisions(int divisions) {
            this.divisions = divisions;
            return getThis();
        }
    }

    // ----- ICOSPHERE GENERATION -----

    /**
     * Largely taken from https://www.javatips.net/api/vintagecraft-master/src/main/java/at/tyron/vintagecraft/Client/Render/Math/Icosahedron.java
     */
    public static class Icosahedron {

        public static double X = 0.525731112119133606f;
        public static double Z = 0.850650808352039932f;

        public static double[][] vdata = {{-X, 0, Z}, {X, 0, Z}, {-X, 0, -Z}, {X, 0, -Z}, {0, Z, X}, {0, Z, -X},
                {0, -Z, X}, {0, -Z, -X}, {Z, X, 0}, {-Z, X, 0}, {Z, -X, 0}, {-Z, -X, 0}};

        public static int[][] tindx = {{0, 4, 1}, {0, 9, 4}, {9, 5, 4}, {4, 5, 8}, {4, 8, 1}, {8, 10, 1}, {8, 3, 10},
                {5, 3, 8}, {5, 2, 3}, {2, 7, 3}, {7, 10, 3}, {7, 6, 10}, {7, 11, 6}, {11, 0, 6}, {0, 1, 6}, {6, 1, 10},
                {9, 0, 11}, {9, 11, 2}, {9, 2, 5}, {7, 2, 11}};

        public Icosahedron(int depth, double radius) {
            for (int[] ints : tindx)
                this.subdivide(vdata[ints[0]], vdata[ints[1]], vdata[ints[2]], depth, radius);
        }

        private final List<Triangle> triangles = new ArrayList<>();

        private void addTriangle(double[] vA0, double[] vB1, double[] vC2, double radius) {
            Triangle triangle = new Triangle(
                    new Vector(vA0[0], vA0[1], vA0[2]).multiply(radius),
                    new Vector(vB1[0], vB1[1], vB1[2]).multiply(radius),
                    new Vector(vC2[0], vC2[1], vC2[2]).multiply(radius)
            );
            this.triangles.add(triangle);
        }

        private void subdivide(double[] vA0, double[] vB1, double[] vC2, int depth, double radius) {
            double[] vAB = new double[3];
            double[] vBC = new double[3];
            double[] vCA = new double[3];

            if (depth == 0) {
                this.addTriangle(vA0, vB1, vC2, radius);
                return;
            }

            for (int i = 0; i < 3; i++) {
                vAB[i] = (vA0[i] + vB1[i]) / 2;
                vBC[i] = (vB1[i] + vC2[i]) / 2;
                vCA[i] = (vC2[i] + vA0[i]) / 2;
            }

            double modAB = mod(vAB);
            double modBC = mod(vBC);
            double modCA = mod(vCA);

            for (int i = 0; i < 3; i++) {
                vAB[i] /= modAB;
                vBC[i] /= modBC;
                vCA[i] /= modCA;
            }

            this.subdivide(vA0, vAB, vCA, depth - 1, radius);
            this.subdivide(vB1, vBC, vAB, depth - 1, radius);
            this.subdivide(vC2, vCA, vBC, depth - 1, radius);
            this.subdivide(vAB, vBC, vCA, depth - 1, radius);
        }

        public static double mod(double[] v) {
            return Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        }

        public List<Triangle> getTriangles() {
            return this.triangles;
        }

        public static class Triangle {
            public Vector point1;
            public Vector point2;
            public Vector point3;

            public Triangle(Vector point1, Vector point2, Vector point3) {
                this.point1 = point1;
                this.point2 = point2;
                this.point3 = point3;
            }
        }
    }

    // ----- CLONE -----

    public ParticleStyleIcosphere(ParticleStyleIcosphere target) {
        super(target);

        this.ticksPerSpawn = target.ticksPerSpawn;
        this.radius = target.radius;
        this.particlesPerLine = target.particlesPerLine;
        this.divisions = target.divisions;
        this.step = 0;
    }

    @Override
    public ParticleStyleIcosphere clone() {
        return new ParticleStyleIcosphere(this);
    }
}
