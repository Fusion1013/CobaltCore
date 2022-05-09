package se.fusion1013.plugin.cobaltcore.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.trades.CustomTradesManager;
import se.fusion1013.plugin.cobaltcore.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EntitySpawnEvents implements Listener {

    // ----- EVENT -----

    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {

        // Wandering Trader
        if (event.getEntityType() == EntityType.WANDERING_TRADER) {
            event.getEntity().getPersistentDataContainer().set(Constants.TEMPORARY_BLACKLISTED, PersistentDataType.BYTE, (byte) 1);
        }

    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        
        // Wandering Trader
        if (event.getEntity() instanceof final WanderingTrader wanderingTrader && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.MOUNT) {
            addTrades(wanderingTrader, false);
        }

    }

    // ----- WANDERING TRADER -----

    public void addTrades(WanderingTrader wanderingTrader, boolean refresh) {

        // If the trader is temporarily blacklisted, do not add trades and instead remove the tag.
        if (wanderingTrader.getPersistentDataContainer().has(Constants.TEMPORARY_BLACKLISTED, PersistentDataType.BYTE)) {
            wanderingTrader.getPersistentDataContainer().remove(Constants.TEMPORARY_BLACKLISTED);
            return;
        }

        // Add trades asynchronously
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> {
            List<MerchantRecipe> newTrades = new ArrayList<>();

            // Get custom trades
            MerchantRecipe recipe = CustomTradesManager.getRecipe(); // TODO: Choose more than one?

            Bukkit.getScheduler().runTask(CobaltCore.getInstance(), () -> {
                newTrades.addAll(wanderingTrader.getRecipes()); // Add all old recipes
                Random r = new Random();
                if (recipe != null) newTrades.set(r.nextInt(0, newTrades.size()), recipe); // Replace old trade // TODO: Should be able to change behavior in config

                wanderingTrader.setRecipes(newTrades); // Set all the new recipes
            });
        });
    }

}
