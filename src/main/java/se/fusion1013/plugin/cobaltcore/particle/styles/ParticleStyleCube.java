package se.fusion1013.plugin.cobaltcore.particle.styles;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.ParticleContainer;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltcore.util.VectorUtil;

import java.util.ArrayList;
import java.util.List;

public class ParticleStyleCube extends ParticleStyle implements IParticleStyle {

    // ----- VARIABLES -----

    private double edgeLength;
    private int particlesPerEdge;

    // TODO: Cube modes (edge, face, full)

    // ----- CONSTRUCTORS -----

    public ParticleStyleCube(String name, Particle particle, Vector offset, int count, double speed, Object extra) {
        super("cube", name, particle, offset, count, speed, extra);
    }

    public ParticleStyleCube(String name) {
        super("cube", name);
    }

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
