package se.fusion1013.plugin.cobaltcore.item;

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
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityDismountEvent;
import se.fusion1013.plugin.cobaltcore.CobaltCore;

public class ItemEventHandler implements Listener {

    @EventHandler
    public void onPlayerToggleFly(PlayerToggleFlightEvent event) {
        if (event.isFlying()) executeActivator(event.getPlayer(), ItemActivator.PLAYER_ACTIVATE_FLY, event);
        else executeActivator(event.getPlayer(), ItemActivator.PLAYER_DEACTIVATE_FLY, event);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) executeActivator(event.getPlayer(), ItemActivator.PLAYER_ACTIVATE_SNEAK, event);
        else executeActivator(event.getPlayer(), ItemActivator.PLAYER_DEACTIVATE_SNEAK, event);
    }

    @EventHandler
    public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
        if (event.isSprinting()) executeActivator(event.getPlayer(), ItemActivator.PLAYER_ACTIVATE_SPRINT, event);
        else executeActivator(event.getPlayer(), ItemActivator.PLAYER_DEACTIVATE_SPRINT, event);
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_ALL_CLICK, event);

        if (event.getAction() == Action.LEFT_CLICK_AIR) executeActivator(event.getPlayer(), ItemActivator.PLAYER_LEFT_CLICK_AIR, event);
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) executeActivator(event.getPlayer(), ItemActivator.PLAYER_LEFT_CLICK_BLOCK, event);
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) executeActivator(event.getPlayer(), ItemActivator.PLAYER_LEFT_CLICK, event);

        if (event.getAction() == Action.RIGHT_CLICK_AIR) executeActivator(event.getPlayer(), ItemActivator.PLAYER_RIGHT_CLICK_AIR, event);
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) executeActivator(event.getPlayer(), ItemActivator.PLAYER_RIGHT_CLICK_BLOCK, event);
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) executeActivator(event.getPlayer(), ItemActivator.PLAYER_RIGHT_CLICK, event);
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_BED_ENTER, event);
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_BED_LEAVE, event);
    }

    /*
    @EventHandler
    public void onPlayerBeforeDeath(PlayerDeathEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_BEFORE_DEATH, event); // TODO
    }
     */

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_BLOCK_BREAK, event);
    }

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_BLOCK_PLACE, event);
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_CHANGE_WORLD, event);
    }

    @EventHandler
    public void onPlayerClickAtEntity(PlayerInteractAtEntityEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_CLICK_AT_ENTITY, event);
    }

    @EventHandler
    public void onPlayerConnect(PlayerJoinEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_CONNECTION, event);
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_DISCONNECTION, event);
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_CONSUME, event);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_DEATH, event);
    }

    @EventHandler
    public void onPlayerDeselectCustomItem(PlayerItemHeldEvent event) {
        ItemStack oldItem = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
        ItemStack newItem = event.getPlayer().getInventory().getItemInMainHand();

        ICustomItem oldCustom = CustomItemManager.getCustomItem(oldItem);
        ICustomItem newCustom = CustomItemManager.getCustomItem(newItem);

        if (oldCustom != null) oldCustom.activatorTriggeredAsync(ItemActivator.PLAYER_DESELECT_CUSTOM_ITEM, event);
        else if (newCustom != null) newCustom.activatorTriggeredAsync(ItemActivator.PLAYER_SELECT_CUSTOM_ITEM, event, EquipmentSlot.HAND);
    }

    @EventHandler
    public void onPlayerDismount(EntityDismountEvent event) {
        if (event.getDismounted() instanceof Player player) executeActivator(player, ItemActivator.PLAYER_DISMOUNT, event);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack drop = event.getItemDrop().getItemStack();
        ICustomItem custom = CustomItemManager.getCustomItem(drop);

        if (custom != null) custom.activatorTriggeredAsync(ItemActivator.PLAYER_DROP_CUSTOM_ITEM, event);
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_DROP_ITEM, event);
    }

    @EventHandler
    public void onPlayerEditBook(PlayerEditBookEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_EDIT_BOOK, event);
    }

    @EventHandler
    public void onPlayerEquip(PlayerArmorChangeEvent event) {
        ItemStack newItem = event.getNewItem();
        ItemStack oldItem = event.getOldItem();

        ICustomItem newCustom = CustomItemManager.getCustomItem(newItem);
        ICustomItem oldCustom = CustomItemManager.getCustomItem(oldItem);

        if (newCustom != null) newCustom.activatorTriggeredAsync(ItemActivator.PLAYER_EQUIP_CUSTOM_ITEM, event);
        if (oldCustom != null) oldCustom.activatorTriggeredAsync(ItemActivator.PLAYER_UNEQUIP_CUSTOM_ITEM, event);

        executeActivator(event.getPlayer(), ItemActivator.PLAYER_EQUIP_ITEM, event);
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_UNEQUIP_ITEM, event);
    }

    @EventHandler
    public void onPlayerFertilize(BlockFertilizeEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_FERTILIZE_BLOCK, event);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_FISH, event); // TODO: Split into multiple events: [BLOCK,ENTITY,NOTHING,PLAYER]
    }

    @EventHandler
    public void onPlayerItemBreak(PlayerItemBreakEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_ITEM_BREAK, event);
    }

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_JUMP, event);
    }

    @EventHandler
    public void onPlayerKill(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (event.getEntity() instanceof Player player2) {
                if (player2.getHealth() - event.getFinalDamage() <= 0) executeActivator(player, ItemActivator.PLAYER_KILL_PLAYER, event);
            }
            if (event.getEntity() instanceof LivingEntity living) {
                if (living.getHealth() - event.getFinalDamage() <= 0) executeActivator(player, ItemActivator.PLAYER_KILL_ENTITY, event);
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
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_MOVE, event);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_COMMAND_PREPROCESS, event);
    }

    @EventHandler
    public void onPlayerCommandSend(PlayerCommandSendEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_COMMAND_SEND, event);
    }

    @EventHandler
    public void onPlayerHitByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player) executeActivator(player, ItemActivator.PLAYER_RECEIVE_HIT_BY_ENTITY, event);
    }

    @EventHandler
    public void onPlayerHitByGlobal(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) executeActivator(player, ItemActivator.PLAYER_DEATH, event);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_RESPAWN, event);
    }

    @EventHandler
    public void onPlayerPostRespawn(PlayerPostRespawnEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_POST_RESPAWN, event);
    }

    @EventHandler
    public void onPlayerShear(PlayerShearEntityEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_SHEAR_ENTITY, event);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) executeActivator(player, ItemActivator.PLAYER_HIT_ENTITY, event);
    }

    /*
    @EventHandler
    public void onPlayerTrampleCrop(Block event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_DEATH, event);
    }
     */

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) executeActivator(player, ItemActivator.INVENTORY_CLICK, event);
    }

    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event) {
        ICustomItem item = CustomItemManager.getCustomItem(event.getItemStack());
        if (item != null) item.activatorTriggeredAsync(ItemActivator.HANGING_PLACE, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBucket(PlayerBucketFillEvent event) {
        executeActivator(event.getPlayer(), ItemActivator.PLAYER_BUCKET, event);
    }

    // ----- HELPER METHOD -----

    private <T extends Event> void executeActivator(Player player, ItemActivator activator, T event) {
        ICustomItem[] items = CustomItemManager.getPlayerHeldCustomItem(player);
        if (items[0] != null) items[0].activatorTriggeredSync(activator, event, EquipmentSlot.HAND); // TODO: Check how this affects performance
        if (items[1] != null) items[1].activatorTriggeredSync(activator, event, EquipmentSlot.OFF_HAND);

        CobaltCore.getInstance().getServer().getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> {
            if (items[0] != null) items[0].activatorTriggeredAsync(activator, event, EquipmentSlot.HAND); // TODO: Event cancelling doesn't really work. Might be because it is running async
            if (items[1] != null) items[1].activatorTriggeredAsync(activator, event, EquipmentSlot.OFF_HAND);
        });
    }
}
