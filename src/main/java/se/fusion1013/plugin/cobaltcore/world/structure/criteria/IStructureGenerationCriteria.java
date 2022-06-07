package se.fusion1013.plugin.cobaltcore.world.structure.criteria;

import org.bukkit.Location;
import org.bukkit.World;
import se.fusion1013.plugin.cobaltcore.state.CobaltState;

public interface IStructureGenerationCriteria {
    /**
     * Checks if all conditions have been achieved to generate a structure.
     *
     * @return if all criteria have been achieved.
     */
    boolean generationCriteriaAchieved(Location location);
}
