package se.fusion1013.plugin.cobaltcore.action;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;
import se.fusion1013.plugin.cobaltcore.action.system.*;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;

import javax.swing.plaf.IconUIResource;
import java.util.Map;

public class ItemConsumeAction extends AbstractAction implements IPlayerAction {

    //region FIELDS

    private String item = "dirt";
    private int consumeCount = 1;

    //endregion

    //region CONSTRUCTION

    public ItemConsumeAction(Map<?, ?> data) {
        super(data);

        if (data.containsKey("item")) item = (String) data.get("item");
        if (data.containsKey("count")) consumeCount = (int) data.get("count");
    }

    //endregion

    //region ACTIVATION

    @Override
    public IActionResult activate(Player player) {
        if (player.getGameMode() == GameMode.CREATIVE) return new ActionResult(true);
        return new ActionResult(PlayerUtil.reduceItemExact(player, item, consumeCount));
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
    public IActionResult activate() {
        if (extraData.containsKey("player")) {
            Player player = (Player) extraData.get("player");
            return activate(player);
        } else if (extraData.containsKey("living_entity")) {
            LivingEntity livingEntity = (LivingEntity) extraData.get("living_entity");
            return activate(livingEntity);
        } else if (extraData.containsKey("entity")) {
            Entity entity = (Entity) extraData.get("entity");
            return activate(entity);
        }

        return new ActionResult(false);
    }

    //endregion

    //region GETTERS/SETTERS

    @Override
    public String getInternalName() {
        return "item_consume_action";
    }

    //endregion
}
