package se.fusion1013.plugin.cobaltcore.action;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.action.system.AbstractAction;
import se.fusion1013.plugin.cobaltcore.action.system.ILocationAction;
import se.fusion1013.plugin.cobaltcore.util.BlockUtil;

import java.util.Map;

public class ExplodeAction extends AbstractAction implements ILocationAction {

    private int explosionRadius;
    private boolean setFire = false;
    private boolean destroysBlocks = false;

    protected ExplodeAction(Map<?, ?> data) {
        super(data);

        if (data.containsKey("explosion_radius")) explosionRadius = (int) data.get("explosion_radius");
        if (data.containsKey("set_fire")) setFire = (boolean) data.get("set_fire");
        if (data.containsKey("destroys_blocks")) destroysBlocks = (boolean) data.get("destroys_blocks");
    }

    @Override
    public String getInternalName() {
        return "explode_action";
    }

    @Override
    public boolean activate(Location location) {

        // TODO: Tweak
        World world = location.getWorld();
        BlockUtil.createExplosion(location, world, explosionRadius, false, setFire, destroysBlocks);

        return true;
    }
}
