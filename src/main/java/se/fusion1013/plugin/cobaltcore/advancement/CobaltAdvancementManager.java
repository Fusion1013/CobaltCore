package se.fusion1013.plugin.cobaltcore.advancement;

import eu.endercentral.crazy_advancements.JSONMessage;
import eu.endercentral.crazy_advancements.NameKey;
import eu.endercentral.crazy_advancements.advancement.Advancement;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;
import eu.endercentral.crazy_advancements.manager.AdvancementManager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;

import java.util.ArrayList;
import java.util.List;

public class CobaltAdvancementManager extends Manager implements Listener {

    // ----- VARIABLES -----

    private static final List<AdvancementManager> ADVANCEMENT_MANAGERS = new ArrayList<>();

    // ----- CONSTRUCTORS -----

    public CobaltAdvancementManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- CREATING ADVANCEMENTS -----

    // TODO: Replace with methods that obscure the API from the user
    public void addAdvancementManager(AdvancementManager manager) {
        ADVANCEMENT_MANAGERS.add(manager);
    }

    // ----- EVENTS -----

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(CobaltCore.getInstance(), () -> {

            Player player = event.getPlayer();
            for (AdvancementManager manager : ADVANCEMENT_MANAGERS) {
                manager.loadProgress(player);
                manager.addPlayer(player);
            }
        }, 20*2);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        saveAdvancementData(event.getPlayer());
    }

    private void saveAdvancementData(Player player) {
        for (AdvancementManager manager : ADVANCEMENT_MANAGERS) {
            manager.saveProgress(player);
        }
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        CobaltCore.getInstance().getServer().getPluginManager().registerEvents(this, CobaltCore.getInstance());
    }

    @Override
    public void disable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            saveAdvancementData(player);
        }
    }

    // ----- GETTERS / SETTERS -----

}
