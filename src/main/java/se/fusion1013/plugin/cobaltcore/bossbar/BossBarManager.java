package se.fusion1013.plugin.cobaltcore.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Entity;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandExecutor;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandManager;
import se.fusion1013.plugin.cobaltcore.config.ConfigManager;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandHandler;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandResult;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

import java.util.*;

/**
 * Keeps track of custom boss bars.
 */
public class BossBarManager extends Manager implements Runnable, CommandExecutor {

    // ----- VARIABLES -----

    private static Map<String, CustomBossBar> bossBarMap = new HashMap<>(); // Stores custom boss bars.

    // ----- CONSTRUCTORS -----
    
    public BossBarManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- BOSS BAR UPDATING -----

    private void updateBossBars() {
        for (String id : bossBarMap.keySet()) {
            CustomBossBar bossBar = bossBarMap.get(id);
            bossBar.update();

            if (!bossBar.isValid()) bossBarMap.remove(id);
        }
    }

    private void removeBossBars() {
        for (String id : bossBarMap.keySet()) {
            CustomBossBar bossBar = bossBarMap.get(id);
            bossBar.remove();
            bossBarMap.remove(id);
        }
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        CommandManager.getInstance().registerCommandModule("cbossbar", BossBarManager.getInstance());

        int period = (int) ConfigManager.getInstance().getFromConfig(CobaltCore.getInstance(), "cobalt.yml", "bossbar-update-period");
        Bukkit.getScheduler().runTaskTimerAsynchronously(CobaltCore.getInstance(), this, 0, period); // TODO: Add config value for period
    }

    @Override
    public void disable() {
        removeBossBars();
    }

    // ----- CREATION -----

    /**
     * Creates a new <code>CustomBossBar</code>.
     *
     * @param identifier the <code>CustomBossBar</code>'s unique identifier
     * @param owner the owner of the <code>CustomBossBar</code>.
     * @param title the title of the <code>CustomBossBar</code>.
     * @param color the color of the <code>CustomBossBar</code>.
     * @param style the style of the <code>CustomBossBar</code>
     * @return the result of the operation.
     */
    @CommandHandler(
            parameterNames = {"identifier", "owner", "title", "color", "style"}
    )
    public static CommandResult create(String identifier, Entity owner, String title, BarColor color, BarStyle style) {
        return create(identifier, owner, title, color, style, -1);
    }

    /**
     * Creates a new <code>CustomBossBar</code>.
     *
     * @param identifier the <code>CustomBossBar</code>'s unique identifier.
     * @param owner the owner of the <code>CustomBossBar</code>.
     * @param title the title of the <code>CustomBossBar</code>.
     * @param color the color of the <code>CustomBossBar</code>.
     * @param style the style of the <code>CustomBossBar</code>
     * @param activationRange the activation range of the <code>CustomBossBar</code>.
     * @return the result of the operation.
     */
    @CommandHandler(
            parameterNames = {"identifier", "owner", "title", "color", "style", "activationRange"}
    )
    public static CommandResult create(String identifier, Entity owner, String title, BarColor color, BarStyle style, double activationRange) {
        CommandResult result = CommandResult.CREATED;

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("id", identifier)
                .build();

        if (bossBarMap.get(identifier) != null) {
            result = CommandResult.FAILED;
            result.setDescription(LocaleManager.getInstance().getLocaleMessage("bossbar.id_already_exists", placeholders));
            return result;
        }

        CustomBossBar bossBar = new CustomBossBar(owner, title, color, style, activationRange);
        bossBarMap.put(identifier, bossBar);

        result.setDescription(LocaleManager.getInstance().getLocaleMessage("bossbar.create", placeholders));
        return result;
    }

    // ----- DELETION -----

    /**
     * Deletes a <code>CustomBossBar</code>.
     *
     * @param identifier the <code>CustomBossBar</code>'s unique identifier.
     * @return the result of the operation.
     */
    @CommandHandler(
            parameterNames = {"identifier"},
            commandSuggestionMethods = {"getBossBarNames"}
    )
    public static CommandResult delete(String identifier) {
        CommandResult result;
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("id", identifier)
                .build();

        CustomBossBar bossBar = bossBarMap.get(identifier);

        if (bossBar == null) {
            result = CommandResult.FAILED;
            result.setDescription(LocaleManager.getInstance().getLocaleMessage("bossbar.does_not_exist", placeholders));
            return result;
        }

        bossBar.remove();
        bossBarMap.remove(identifier);

        result = CommandResult.DELETED;
        result.setDescription(LocaleManager.getInstance().getLocaleMessage("bossbar.delete", placeholders));
        return result;
    }

    // ----- GETTERS / SETTERS -----

    public static String[] getBossBarNames() {
        return bossBarMap.keySet().toArray(new String[0]);
    }

    public String[] getListInfo() {
        List<String> info = new ArrayList<>();
        for (String id : bossBarMap.keySet()) {
            CustomBossBar bossBar = bossBarMap.get(id);
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("id", id)
                    .addPlaceholder("owner", bossBar.getOwner().getName())
                    .build();

            info.add(LocaleManager.getInstance().getLocaleMessage("bossbar.info.list", placeholders));
        }

        return info.toArray(new String[0]);
    }

    // ----- RUNNABLE -----

    @Override
    public void run() {
        updateBossBars();
    }


    // ----- INSTANCE VARIABLE & METHOD -----

    private static BossBarManager INSTANCE = null;
    /**
     * Returns the object representing this <code>BossBarManager</code>.
     *
     * @return The object of this class.
     */
    public static BossBarManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new BossBarManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
