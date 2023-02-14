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
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.CobaltPlugin;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandManager;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.item.crafting.IRecipeWrapper;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CobaltAdvancementManager extends Manager implements Listener {

    //region FIELDS

    private static final List<IAdvancementWrapper> WRAPPERS_TO_PROCESS = new ArrayList<>();

    // private static final List<AdvancementManager> ADVANCEMENT_MANAGERS = new ArrayList<>();

    public static final Map<String, AdvancementManager> ADVANCEMENT_MANAGERS = new HashMap<>();

    //endregion

    // ----- CONSTRUCTORS -----

    public CobaltAdvancementManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- CREATING ADVANCEMENTS -----

    // TODO: Replace with methods that obscure the API from the user
    public void addAdvancementManager(String key, AdvancementManager manager) {
        ADVANCEMENT_MANAGERS.put(key, manager);
    }

    //region ADVANCEMENT FILE LOADING

    public static void loadAdvancementFiles(CobaltPlugin plugin, boolean overwrite) {
        File dataFolder = plugin.getDataFolder();
        File advancementFolder = new File(dataFolder, "advancements/");
        loadAdvancementsFromFolders(plugin, advancementFolder, overwrite);

        loadAdvancementsFromResources(plugin);

        registerAdvancements();
    }

    private static void loadAdvancementsFromResources(CobaltPlugin plugin) {
        String[] advancementFileNames = FileUtil.getResources(plugin.getClass(), "advancements");
        for (String s : advancementFileNames) {
            File file = FileUtil.getOrCreateFileFromResource(plugin, "advancements/" + s);
            if (file.exists()) plugin.getLogger().info("Found advancement file: " + file.getAbsolutePath());

            loadAdvancement(plugin, file, false);
        }
    }

    public static void loadAdvancementsFromFolders(CobaltPlugin plugin, File rootFolder, boolean overwrite) {
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
            return;
        }

        plugin.getLogger().info("Loading advancements from folder '" + rootFolder.getName() + "'...");

        int advancementsLoaded = 0;
        File[] files = rootFolder.listFiles();
        for (File file : files) {
            if (file.isDirectory()) loadAdvancementsFromFolders(plugin, file, overwrite);
            else {
                loadAdvancement(plugin, file, overwrite);
                advancementsLoaded++;
            }
        }

        plugin.getLogger().info("Loaded " + advancementsLoaded + " advancements from folder " + rootFolder.getName());
    }

    private static void loadAdvancement(CobaltPlugin plugin, File file, boolean overwrite) {
        YamlConfiguration yaml = new YamlConfiguration();
        try {
            yaml.load(file);
        } catch (IOException | InvalidConfigurationException ex) {
            ex.printStackTrace();
        }

        AdvancementWrapper wrapper = new AdvancementWrapper(plugin, yaml);
        WRAPPERS_TO_PROCESS.add(wrapper);
    }

    //endregion

    //region ADVANCEMENT REGISTRY

    private static void registerAdvancements() {
        int previousSize;
        do {
            previousSize = WRAPPERS_TO_PROCESS.size();
            for (int i = WRAPPERS_TO_PROCESS.size() - 1; i >= 0; i--) {
                IAdvancementWrapper wrapper = WRAPPERS_TO_PROCESS.get(i);

                AdvancementManager manager = ADVANCEMENT_MANAGERS.computeIfAbsent(wrapper.getPlugin().getInternalName() + "." + wrapper.getNamespace(), k -> new AdvancementManager(new NameKey(wrapper.getPlugin().getInternalName() + "." + wrapper.getNamespace(), wrapper.getNamespace())));
                boolean registered = wrapper.register(manager);
                if (registered) WRAPPERS_TO_PROCESS.remove(i);
            }
        } while (WRAPPERS_TO_PROCESS.size() > 0 && WRAPPERS_TO_PROCESS.size() < previousSize);

        if (WRAPPERS_TO_PROCESS.size() > 0) {
            CobaltCore.getInstance().getLogger().warning("Failed to load " + WRAPPERS_TO_PROCESS.size() + " advancements");
            for (IAdvancementWrapper wrapper : WRAPPERS_TO_PROCESS) CobaltCore.getInstance().getLogger().info(wrapper.getPlugin() + "." + wrapper.getNamespace());
        }
    }

    //endregion

    //region EVENTS

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(CobaltCore.getInstance(), () -> {

            Player player = event.getPlayer();
            for (AdvancementManager manager : ADVANCEMENT_MANAGERS.values()) {
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
        for (AdvancementManager manager : ADVANCEMENT_MANAGERS.values()) {
            manager.saveProgress(player);
        }
    }

    //endregion

    //region RELOADING / DISABLING

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

    //endregion

}
