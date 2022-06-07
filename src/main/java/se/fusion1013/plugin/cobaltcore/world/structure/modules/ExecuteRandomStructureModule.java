package se.fusion1013.plugin.cobaltcore.world.structure.modules;

import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.util.StructureUtil;

import java.util.Random;

public class ExecuteRandomStructureModule extends StructureModule {

    // TODO: Use weights

    // ----- VARIABLES -----

    StructureModuleType type;
    IStructureModule[] modules;

    // ----- CONSTRUCTORS -----

    public ExecuteRandomStructureModule(StructureModuleType type, IStructureModule... modules) {
        this.type = type;
        this.modules = modules;
    }

    // ----- EXECUTE -----


    @Override
    public void executeWithSeed(Location location, StructureUtil.StructureHolder holder, long seed) {
        Random r = new Random(seed);
        modules[r.nextInt(modules.length)].execute(location, holder);
    }

    @Override
    public void execute(Location location, StructureUtil.StructureHolder holder) {
        Random r = new Random();
        modules[r.nextInt(modules.length)].execute(location, holder);
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public StructureModuleType getModuleType() {
        return type;
    }
}
