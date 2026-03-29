package se.fusion1013.cobaltCore.commands.system;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.CobaltPlugin;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.manager.registry.CobaltRegistry;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CommandManager extends Manager<CobaltCore> {

    private static final CobaltRegistry<ReloadInfo> RELOAD_INFO = new CobaltRegistry<>();
    private static final CobaltRegistry<PastebinInfo> PASTEBIN_INFO = new CobaltRegistry<>();
    private static final CobaltRegistry<ItemInfo> ITEM_INFO = new CobaltRegistry<>();

    public CommandManager(CobaltCore plugin) {
        super(plugin);
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    // ##%%##%%## RELOAD ##%%##%%## //

    public static String[] getReloadOptions() {
        return RELOAD_INFO.getNames();
    }

    public static void registerReloadMethod(String name, Runnable reload, Supplier<String[]> verboseResult) {
        RELOAD_INFO.register(name, new ReloadInfo(name, reload, verboseResult));
    }

    public static ReloadInfo getReload(String key) {
        return RELOAD_INFO.get(key);
    }

    // ##%%##%%## PASTEBIN ##%%##%%## //

    public static String[] getPastebinOptions() {
        return PASTEBIN_INFO.getNames();
    }

    public static void registerPastebinMethod(CobaltPlugin plugin, String name, BiConsumer<Player, YamlConfiguration> yamlConsumer, Runnable postLoad) {
        PASTEBIN_INFO.register(name, new PastebinInfo(plugin, name, yamlConsumer, postLoad));
    }

    public static PastebinInfo getPastebinInfo(String key) {
        return PASTEBIN_INFO.get(key);
    }

    // ##%%##%%## ITEM INFO ##%%##%%## //

    public static String[] getItemInfoOptions() {
        return ITEM_INFO.getNames();
    }

    public static void registerItemInfo(String name, Supplier<String[]> listItems, Function<String, String[]> itemInfo) {
        ITEM_INFO.register(new ItemInfo(name, listItems, itemInfo));
    }

    public static ItemInfo getItemInfo(String key) {
        return ITEM_INFO.get(key);
    }
}
