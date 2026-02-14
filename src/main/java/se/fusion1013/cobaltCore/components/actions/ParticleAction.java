package se.fusion1013.cobaltCore.components.actions;

import dev.jorel.commandapi.wrappers.ParticleData;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltCore.variable.DoubleVariable;
import se.fusion1013.cobaltCore.variable.IntVariable;
import se.fusion1013.cobaltCore.variable.ParticleVariable;

import java.util.List;
import java.util.Map;

public class ParticleAction extends AbstractAction {

    private final ParticleVariable particle = new ParticleVariable("type", new ParticleData(Particle.CRIT, null));
    private final IntVariable count = new IntVariable("count", 1);
    private final IntVariable xPosition = new IntVariable("x", 0);
    private final IntVariable yPosition = new IntVariable("y", 0);
    private final IntVariable zPosition = new IntVariable("z", 0);
    private final DoubleVariable speed = new DoubleVariable("speed", 0);

    @Override
    public void execute(Map<String, Object> context) {
        Location location = getTargetLocation(context);
        World world = location.getWorld();
        world.spawnParticle(particle.getParticle(),
                location,
                count.getValue(),
                xPosition.getValue(),
                yPosition.getValue(),
                zPosition.getValue(),
                speed.getValue(),
                particle.getData()
        );
    }

    @Override
    protected List<AbstractVariable<?, ?, ?, ?>> getVariables() {
        return List.of(particle, count, xPosition, yPosition, zPosition, speed);
    }

    @Override
    public String getId() {
        return "particle";
    }
}
