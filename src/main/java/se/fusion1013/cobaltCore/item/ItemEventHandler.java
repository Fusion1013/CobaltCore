package se.fusion1013.cobaltCore.item;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.CobaltCore;

import java.util.HashMap;
import java.util.Map;

public class ItemEventHandler implements Listener {

    @EventHandler
    public void onPlayerToggleFly(PlayerToggleFlightEvent event) {
        Map<String, Object> context = new HashMap<>();
        if (event.isFlying()) executeActivator(event.getPlayer(), ItemActivator.PLAYER_ACTIVATE_FLY, event, context);
        else executeActivator(event.getPlayer(), ItemActivator.PLAYER_DEACTIVATE_FLY, event, context);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Map<String, Object> context = new HashMap<>();
        if (event.isSneaking())
            executeActivator(event.getPlayer(), ItemActivator.PLAYER_ACTIVATE_SNEAK, event, context);
        else executeActivator(event.getPlayer(), ItemActivator.PLAYER_DEACTIVATE_SNEAK, event, context);
    }

    @EventHandler
    public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
        Map<String, Object> context = new HashMap<>();
        if (event.isSprinting())
            executeActivator(event.getPlayer(), ItemActivator.PLAYER_ACTIVATE_SPRINT, event, context);
        else executeActivator(event.getPlayer(), ItemActivator.PLAYER_DEACTIVATE_SPRINT, event, context);
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
        Map<String, Object> context = new HashMap<>();
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_ALL_CLICK, event, context);

        if (event.getAction() == Action.LEFT_CLICK_AIR)
            executeActivator(event.getPlayer(), ItemActivator.PLAYER_LEFT_CLICK_AIR, event, context);
        if (event.getAction() == Action.LEFT_CLICK_BLOCK)
            executeActivator(event.getPlayer(), ItemActivator.PLAYER_LEFT_CLICK_BLOCK, event, context);
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
            executeActivator(event.getPlayer(), ItemActivator.PLAYER_LEFT_CLICK, event, context);

        if (event.getAction() == Action.RIGHT_CLICK_AIR)
            executeActivator(event.getPlayer(), ItemActivator.PLAYER_RIGHT_CLICK_AIR, event, context);
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
            executeActivator(event.getPlayer(), ItemActivator.PLAYER_RIGHT_CLICK_BLOCK, event, context);
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
            executeActivator(event.getPlayer(), ItemActivator.PLAYER_RIGHT_CLICK, event, context);
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        Map<String, Object> context = new HashMap<>();
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_BED_ENTER, event, context);
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        Map<String, Object> context = new HashMap<>();
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_BED_LEAVE, event, context);
    }

    /*
    @EventHandler
    public void onPlayerBeforeDeath(PlayerDeathEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_BEFORE_DEATH, event); // TODO
    }
     */

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Map<String, Object> context = new HashMap<>();
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_BLOCK_BREAK, event, context);
    }

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        Map<String, Object> context = new HashMap<>();
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_BLOCK_PLACE, event, context);
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Map<String, Object> context = new HashMap<>();
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_CHANGE_WORLD, event, context);
    }

    @EventHandler
    public void onPlayerClickAtEntity(PlayerInteractAtEntityEvent event) {
        Map<String, Object> context = new HashMap<>();
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_CLICK_AT_ENTITY, event, context);
    }

    @EventHandler
    public void onPlayerConnect(PlayerJoinEvent event) {
        Map<String, Object> context = new HashMap<>();
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_CONNECTION, event, context);
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        Map<String, Object> context = new HashMap<>();
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_DISCONNECTION, event, context);
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        Map<String, Object> context = new HashMap<>();
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_CONSUME, event, context);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Map<String, Object> context = new HashMap<>();
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_DEATH, event, context);
    }

    @EventHandler
    public void onPlayerDeselectCustomItem(PlayerItemHeldEvent event) {
        Map<String, Object> context = new HashMap<>();
        ItemStack oldItem = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
        ItemStack newItem = event.getPlayer().getInventory().getItemInMainHand();

        ICustomItem oldCustom = CustomItemManager.getCustomItem(oldItem);
        ICustomItem newCustom = CustomItemManager.getCustomItem(newItem);

        if (oldCustom != null)
            oldCustom.activatorTriggeredAsync(ItemActivator.PLAYER_DESELECT_CUSTOM_ITEM, event, context);
        else if (newCustom != null)
            newCustom.activatorTriggeredAsync(ItemActivator.PLAYER_SELECT_CUSTOM_ITEM, event, EquipmentSlot.HAND, context);
    }

    @EventHandler
    public void onPlayerDismount(EntityDismountEvent event) {
        Map<String, Object> context = new HashMap<>();
        if (event.getDismounted() instanceof Player player)
            executeActivator(player, ItemActivator.PLAYER_DISMOUNT, event, context);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Map<String, Object> context = new HashMap<>();
        ItemStack drop = event.getItemDrop().getItemStack();
        ICustomItem custom = CustomItemManager.getCustomItem(drop);

        if (custom != null) custom.activatorTriggeredAsync(ItemActivator.PLAYER_DROP_CUSTOM_ITEM, event, context);
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_DROP_ITEM, event, context);
    }

    @EventHandler
    public void onPlayerEditBook(PlayerEditBookEvent event) {
        Map<String, Object> context = new HashMap<>();
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_EDIT_BOOK, event, context);
    }

    @EventHandler
    public void onPlayerEquip(PlayerArmorChangeEvent event) {
        Map<String, Object> context = new HashMap<>();
        ItemStack newItem = event.getNewItem();
        ItemStack oldItem = event.getOldItem();

        ICustomItem newCustom = CustomItemManager.getCustomItem(newItem);
        ICustomItem oldCustom = CustomItemManager.getCustomItem(oldItem);

        if (newCustom != null)
            newCustom.activatorTriggeredAsync(ItemActivator.PLAYER_EQUIP_CUSTOM_ITEM, event, context);
        if (oldCustom != null)
            oldCustom.activatorTriggeredAsync(ItemActivator.PLAYER_UNEQUIP_CUSTOM_ITEM, event, context);

        executeActivator(event.getPlayer(), ItemActivator.PLAYER_EQUIP_ITEM, event, context);
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_UNEQUIP_ITEM, event, context);
    }

    @EventHandler
    public void onPlayerFertilize(BlockFertilizeEvent event) {
        Map<String, Object> context = new HashMap<>();
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_FERTILIZE_BLOCK, event, context);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Map<String, Object> context = new HashMap<>();
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_FISH, event, context); // TODO: Split into multiple events: [BLOCK,ENTITY,NOTHING,PLAYER]
    }

    @EventHandler
    public void onPlayerItemBreak(PlayerItemBreakEvent event) {
        Map<String, Object> context = new HashMap<>();
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_ITEM_BREAK, event, context);
    }

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {
        Map<String, Object> context = new HashMap<>();
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_JUMP, event, context);
    }

    @EventHandler
    public void onPlayerKill(EntityDamageByEntityEvent event) {
        Map<String, Object> context = new HashMap<>();
        if (event.getDamager() instanceof Player player) {
            if (event.getEntity() instanceof Player player2) {
                if (player2.getHealth() - event.getFinalDamage() <= 0)
                    executeActivator(player, ItemActivator.PLAYER_KILL_PLAYER, event, context);
            }
            if (event.getEntity() instanceof LivingEntity living) {
                if (living.getHealth() - event.getFinalDamage() <= 0)
                    executeActivator(player, ItemActivator.PLAYER_KILL_ENTITY, event, context);
            }
        }
    }

    /*
    @EventHandler
    public void onLaunchProjectile(ProjectileLaunchEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_DEATH, event); // TODO
    }
     */

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Map<String, Object> context = new HashMap<>();
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_MOVE, event, context);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Map<String, Object> context = new HashMap<>();
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_COMMAND_PREPROCESS, event, context);
    }

    @EventHandler
    public void onPlayerCommandSend(PlayerCommandSendEvent event) {
        Map<String, Object> context = new HashMap<>();
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_COMMAND_SEND, event, context);
    }

    @EventHandler
    public void onPlayerHitByEntity(EntityDamageByEntityEvent event) {
        Map<String, Object> context = new HashMap<>();
        if (event.getEntity() instanceof Player player)
            executeActivator(player, ItemActivator.PLAYER_RECEIVE_HIT_BY_ENTITY, event, context);
    }

    @EventHandler
    public void onPlayerHitByGlobal(EntityDamageEvent event) {
        Map<String, Object> context = new HashMap<>();
        if (event.getEntity() instanceof Player player)
            executeActivator(player, ItemActivator.PLAYER_DEATH, event, context);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Map<String, Object> context = new HashMap<>();
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_RESPAWN, event, context);
    }

    @EventHandler
    public void onPlayerPostRespawn(PlayerPostRespawnEvent event) {
        Map<String, Object> context = new HashMap<>();
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_POST_RESPAWN, event, context);
    }

    @EventHandler
    public void onPlayerShear(PlayerShearEntityEvent event) {
        Map<String, Object> context = new HashMap<>();
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_SHEAR_ENTITY, event, context);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Map<String, Object> context = new HashMap<>();
        if (event.getDamager() instanceof Player player) {
            context.put("hit", event.getEntity());
            context.put("hit_center", event.getEntity().getLocation());
            context.put("is_critical", event.isCritical());
            executeActivator(player, ItemActivator.PLAYER_HIT_ENTITY, event, context);
        }
    }

    /*
    @EventHandler
    public void onPlayerTrampleCrop(Block event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_DEATH, event);
    }
     */

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Map<String, Object> context = new HashMap<>();
        if (event.getWhoClicked() instanceof Player player)
            executeActivator(player, ItemActivator.INVENTORY_CLICK, event, context);
    }

    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event) {
        Map<String, Object> context = new HashMap<>();
        ICustomItem item = CustomItemManager.getCustomItem(event.getItemStack());
        if (item != null) item.activatorTriggeredAsync(ItemActivator.HANGING_PLACE, event, context);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBucket(PlayerBucketFillEvent event) {
        Map<String, Object> context = new HashMap<>();
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_BUCKET, event, context);
    }

    // ----- HELPER METHOD -----

    private <T extends Event> void executeActivator(Player player, ItemActivator activator, T event, Map<String, Object> context) {
        context.put("default_entity", player);
        context.put("default_location", player.getLocation());

        ICustomItem[] items = CustomItemManager.getPlayerHeldCustomItem(player);
        if (items[0] != null)
            items[0].activatorTriggeredSync(activator, event, EquipmentSlot.HAND, context); // TODO: Check how this affects performance
        if (items[1] != null) items[1].activatorTriggeredSync(activator, event, EquipmentSlot.OFF_HAND, context);

        CobaltCore.getInstance().getServer().getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> {
            if (items[0] != null)
                items[0].activatorTriggeredAsync(activator, event, EquipmentSlot.HAND, context); // TODO: Event cancelling doesn't really work. Might be because it is running async
            if (items[1] != null) items[1].activatorTriggeredAsync(activator, event, EquipmentSlot.OFF_HAND, context);
        });
    }
}
