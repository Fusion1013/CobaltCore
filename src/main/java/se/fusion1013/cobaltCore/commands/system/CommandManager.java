package se.fusion1013.cobaltCore.commands.system;

import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.manager.registry.CobaltRegistry;

import java.util.function.Supplier;

public class CommandManager extends Manager<CobaltCore> {

    private static final CobaltRegistry<ReloadInfo> RELOAD_INFO = new CobaltRegistry<>();

    public CommandManager(CobaltCore plugin) {
        super(plugin);
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    public static String[] getReloadOptions() {
        return RELOAD_INFO.getNames();
    }

    public static void registerReloadMethod(String name, Runnable reload, Supplier<String[]> verboseResult) {
        RELOAD_INFO.register(name, new ReloadInfo(name, reload, verboseResult));
    }

    public static ReloadInfo getReload(String key) {
        return RELOAD_INFO.get(key);
    }
}
