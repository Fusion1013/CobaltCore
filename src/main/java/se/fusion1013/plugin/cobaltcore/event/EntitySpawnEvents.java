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

        List<MerchantRecipe> newTrades = new ArrayList<>(wanderingTrader.getRecipes()); // Create list with all old trades

        // Add new trades
        Random r = new Random();
        for (int i = 0; i < newTrades.size(); i++) {
            if (r.nextDouble() < .1) {
                MerchantRecipe newRecipe = CustomTradesManager.getRecipe();
                if (newRecipe != null && !newTrades.contains(newRecipe)) newTrades.set(i, newRecipe);
            }
        }

        wanderingTrader.setRecipes(newTrades); // Set all the new recipes
    }

}
