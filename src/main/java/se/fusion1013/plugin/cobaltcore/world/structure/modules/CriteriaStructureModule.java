package se.fusion1013.plugin.cobaltcore.world.structure.modules;

import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.util.StructureUtil;
import se.fusion1013.plugin.cobaltcore.world.structure.criteria.IStructureGenerationCriteria;

public class CriteriaStructureModule extends StructureModule implements IStructureModule {

    // ----- VARIABLES -----

    IStructureModule module;
    IStructureGenerationCriteria[] criteria;

    // ----- CONSTRUCTORS -----

    public CriteriaStructureModule(IStructureModule module, IStructureGenerationCriteria... criteria) {
        this.module = module;
        this.criteria = criteria;
    }

    // ----- GENERATION -----

    @Override
    public void execute(Location location, StructureUtil.StructureHolder holder) {
        for (IStructureGenerationCriteria c : criteria) if (!c.generationCriteriaAchieved(location)) return;

        module.execute(location, holder);
    }

    @Override
    public void executeWithSeed(Location location, StructureUtil.StructureHolder holder, long seed) {
        for (IStructureGenerationCriteria c : criteria) if (!c.generationCriteriaAchieved(location)) return;

        module.executeWithSeed(location, holder, seed);
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public StructureModuleType getModuleType() {
        return module.getModuleType();
    }
}
