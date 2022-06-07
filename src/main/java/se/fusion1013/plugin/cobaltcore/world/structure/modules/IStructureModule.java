package se.fusion1013.plugin.cobaltcore.world.structure.modules;

import org.bukkit.Location;
import org.bukkit.event.Event;
import se.fusion1013.plugin.cobaltcore.util.StructureUtil;

public interface IStructureModule {

    /**
     * Executes this <code>IStructureModule</code>.
     *
     * @param location the location of the structure.
     * @param holder the structure.
     */
    void execute(Location location, StructureUtil.StructureHolder holder);

    /**
     * Executes this <code>StructureModule</code> with a given seed.
     *
     * @param location the location of the structure.
     * @param holder the structure.
     * @param seed the seed.
     */
    void executeWithSeed(Location location, StructureUtil.StructureHolder holder, long seed);

    /**
     * Gets the type of this structure module.
     *
     * @return the type of this structure module.
     */
    StructureModuleType getModuleType();

    default <T extends Event> void onEvent(T event, Location location, StructureUtil.StructureHolder holder) {}

}
