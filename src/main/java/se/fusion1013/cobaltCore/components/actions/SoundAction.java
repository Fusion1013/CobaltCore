package se.fusion1013.cobaltCore.components.actions;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltCore.variable.DoubleVariable;
import se.fusion1013.cobaltCore.variable.SoundVariable;

import java.util.List;
import java.util.Map;

public class SoundAction extends AbstractAction {

    private final SoundVariable sound = new SoundVariable("sound", Sound.ENTITY_WITHER_SPAWN);
    private final DoubleVariable volume = new DoubleVariable("volume", 1);
    private final DoubleVariable pitch = new DoubleVariable("pitch", 1);

    @Override
    protected List<AbstractVariable<?, ?, ?, ?>> getVariables() {
        return List.of(sound);
    }

    @Override
    public void execute(Map<String, Object> context) {
        Location location = getTargetLocation(context);
        World world = location.getWorld();
        world.playSound(location, sound.getValue(), volume.getValue().floatValue(), pitch.getValue().floatValue());
    }

    @Override
    public String getId() {
        return "";
    }
}
