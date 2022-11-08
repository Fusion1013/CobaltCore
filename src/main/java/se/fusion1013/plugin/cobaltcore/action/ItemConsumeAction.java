package se.fusion1013.plugin.cobaltcore.action;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.action.system.*;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;

import javax.swing.plaf.IconUIResource;
import java.util.Map;

public class ItemConsumeAction extends AbstractAction implements IPlayerAction {

    // ----- VARIABLES -----

    private String item = "dirt";
    private int consumeCount = 1;

    // ----- CONSTRUCTORS -----

    public ItemConsumeAction(Map<?, ?> data) {
        super(data);

        if (data.containsKey("item")) item = (String) data.get("item");
        if (data.containsKey("count")) consumeCount = (int) data.get("count");
    }

    // ----- ACTIVATION -----

    @Override
    public boolean activate(Player player) {
        if (player.getGameMode() == GameMode.CREATIVE) return true;
        return PlayerUtil.reduceItemExact(player, item, consumeCount);
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public String getInternalName() {
        return "item_consume_action";
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
}
