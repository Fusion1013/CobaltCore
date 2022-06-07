package se.fusion1013.plugin.cobaltcore.world.structure.modules;

import org.bukkit.Location;
import org.bukkit.Material;
import se.fusion1013.plugin.cobaltcore.util.StructureUtil;
import se.fusion1013.plugin.cobaltcore.world.structure.Dilapidate;

public class DilapidateStructureModule extends StructureModule {

    /*
    * NOTE: Dilapidate might interact in strange ways with connected structures, as dilapidate works on a cubic volume.
    * As such it might intersect with other 'rooms' and dilapidate them as well, depending on the layout of the structure.
    */

    // ----- VARIABLES -----

    Dilapidate dilapidate;
    int passes = 3;

    // ----- CONSTRUCTORS -----

    public DilapidateStructureModule(Dilapidate dilapidate) {
        this.dilapidate = dilapidate;
    }

    // ----- EXECUTING -----

    @Override
    public void executeWithSeed(Location location, StructureUtil.StructureHolder holder, long seed) {
        execute(location, holder);
    }

    @Override
    public void execute(Location location, StructureUtil.StructureHolder holder) {
        for (int i = 0; i < passes; i++) {
            dilapidate.run(location, holder.width, holder.height, holder.depth);
        }
    }

    // ----- BUILDER -----

    public static class DilapidateStructureModuleBuilder extends StructureModuleBuilder<DilapidateStructureModule, DilapidateStructureModuleBuilder> {

        // ----- VARIABLES -----

        Dilapidate dilapidate;

        // ----- CONSTRUCTORS -----

        public DilapidateStructureModuleBuilder(Dilapidate dilapidate) {
            this.dilapidate = dilapidate;
        }

        // ----- BUILDING -----

        @Override
        public DilapidateStructureModule build() {
            super.build();
            return obj;
        }

        // ----- HELPER METHODS -----

        @Override
        protected DilapidateStructureModule createObj() {
            return new DilapidateStructureModule(dilapidate);
        }

        @Override
        protected DilapidateStructureModuleBuilder getThis() {
            return this;
        }
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public StructureModuleType getModuleType() {
        return StructureModuleType.POST;
    }
}
