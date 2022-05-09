package se.fusion1013.plugin.cobaltcore.commands.system;

import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;

import java.util.HashMap;
import java.util.Map;

public class CommandManager extends Manager {

    // ----- VARIABLES -----

    private final Map<String, CommandExecutor> commandModuleMap = new HashMap<>();

    // ----- CONSTRUCTORS -----

    public CommandManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- MODULE GETTING -----

    public CommandExecutor[] getCommandModules() {
        return commandModuleMap.values().toArray(new CommandExecutor[0]);
    }

    public String[] getCommandModuleIdentifiers() {
        return commandModuleMap.keySet().toArray(new String[0]);
    }

    public CommandExecutor getCommandModule(String identifier) {
        return commandModuleMap.get(identifier);
    }

    // ----- REGISTER -----

    public <T> void registerCommandModule(String identifier, CommandExecutor executor) {
        commandModuleMap.put(identifier, executor);
    }

    @Override
    public void reload() {}

    @Override
    public void disable() {}

    // ----- INSTANCE VARIABLE & METHOD -----

    private static CommandManager INSTANCE = null;
    /**
     * Returns the object representing this <code>ModuleManager</code>.
     *
     * @return The object of this class.
     */
    public static CommandManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new CommandManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
