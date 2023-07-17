package se.fusion1013.plugin.cobaltcore.action;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.action.system.ActionResult;
import se.fusion1013.plugin.cobaltcore.action.system.IActionResult;
import se.fusion1013.plugin.cobaltcore.action.system.ILivingEntityAction;
import se.fusion1013.plugin.cobaltcore.action.system.IPlayerAction;

import java.util.Map;

public class IsSneakingAction implements IPlayerAction {

    @Override
    public String getInternalName() {
        return "is_sneaking_action";
    }

    @Override
    public IActionResult activate(Entity entity) {
        if (entity instanceof Player player) return activate(player);
        return new ActionResult(false);
    }

    @Override
    public IActionResult activate(LivingEntity entity) {
        if (entity instanceof Player player) return activate(player);
        return new ActionResult(false);
    }

    @Override
    public IActionResult activate(Player player) {
        return new ActionResult(player.isSneaking());
    }

    @Override
    public void setExtraData(String key, Object data) {

    }

    @Override
    public boolean isCancelAction() {
        return false;
    }

    @Override
    public IActionResult activate() {
        return new ActionResult(false);
    }

    @Override
    public IActionResult activate(Map<String, Object> data) {
        return new ActionResult(false);
    }
}
