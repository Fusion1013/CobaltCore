package se.fusion1013.plugin.cobaltcore.world.structure.modules;

import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.util.StructureUtil;

/**
 * Activates all stored structure modules.
 */
public class MultiStructureModule extends StructureModule implements IStructureModule {

    // ----- VARIABLES -----

    StructureModuleType type;
    IStructureModule[] modules;

    // ----- CONSTRUCTORS -----

    public MultiStructureModule(StructureModuleType type, IStructureModule... modules) {
        this.type = type;
        this.modules = modules;
    }

    // ----- EXECUTE -----

    @Override
    public void execute(Location location, StructureUtil.StructureHolder holder) {
        for (IStructureModule module : modules) module.execute(location, holder);
    }

    @Override
    public void executeWithSeed(Location location, StructureUtil.StructureHolder holder, long seed) {
        for (IStructureModule module : modules) module.executeWithSeed(location, holder, seed);
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public StructureModuleType getModuleType() {
        return type;
    }
}
