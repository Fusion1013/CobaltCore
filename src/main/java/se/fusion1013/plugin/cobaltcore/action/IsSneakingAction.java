package se.fusion1013.plugin.cobaltcore.action;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.action.system.ILivingEntityAction;
import se.fusion1013.plugin.cobaltcore.action.system.IPlayerAction;

public class IsSneakingAction implements IPlayerAction {

    @Override
    public String getInternalName() {
        return "is_sneaking_action";
    }

    @Override
    public boolean activate(Entity entity) {
        if (entity instanceof Player player) return activate(player);
        return false;
    }

    @Override
    public boolean activate(LivingEntity entity) {
        if (entity instanceof Player player) return activate(player);
        return false;
    }

    @Override
    public boolean activate(Player player) {
        return player.isSneaking();
    }
}
