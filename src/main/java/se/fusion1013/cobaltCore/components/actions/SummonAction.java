package se.fusion1013.cobaltCore.components.actions;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.entity.CustomEntityManager;
import se.fusion1013.cobaltCore.entity.ICustomEntity;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltCore.variable.LiteralVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;

import java.util.List;
import java.util.Map;

public class SummonAction extends AbstractAction {

    private final LiteralVariable type = new LiteralVariable("type", new String[]{"entity", "item"});
    private final StringVariable summon = new StringVariable("summon");

    @Override
    protected List<AbstractVariable<?, ?, ?, ?>> getVariables() {
        return List.of(type, summon);
    }

    @Override
    public void execute(Map<String, Object> context) {
        if (type.getValue().equalsIgnoreCase("entity")) {
            summonEntity(context);
        } else if (type.getValue().equalsIgnoreCase("item")) {
            summonItem(context);
        }
    }

    private void summonEntity(Map<String, Object> context) {
        Location location = getTargetLocation(context);
        if (location == null) return;

        World world = location.getWorld();

        ICustomEntity customEntity = CustomEntityManager.getEntityType(summon.getValue());
        if (customEntity != null) {
            CustomEntityManager.spawnEntity(world, location, customEntity);
            return;
        }

        EntityType entityType = EntityType.valueOf(summon.getValue());
        world.spawnEntity(location, entityType);
    }

    private void summonItem(Map<String, Object> context) {
        Location location = getTargetLocation(context);
        if (location == null) return;

        World world = location.getWorld();

        ItemStack itemStack = CustomItemManager.getItemStack(summon.getValue());
        if (itemStack == null) return;

        world.spawn(location, Item.class, CreatureSpawnEvent.SpawnReason.COMMAND, item -> {
            item.setItemStack(itemStack);
        });
    }

    @Override
    public String getId() {
        return "summon";
    }
}
