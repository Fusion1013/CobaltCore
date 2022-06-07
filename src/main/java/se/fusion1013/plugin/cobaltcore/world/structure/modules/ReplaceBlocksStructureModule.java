package se.fusion1013.plugin.cobaltcore.world.structure.modules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.util.StructureUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ReplaceBlocksStructureModule extends StructureModule {

    // TODO: Keep rotation of blocks

    // ----- VARIABLES -----

    Map<Material, Material[]> materials;

    // ----- CONSTRUCTORS -----

    public ReplaceBlocksStructureModule(Map<Material, Material[]> materials) {
        this.materials = materials;
    }

    // ----- EXECUTOR -----


    @Override
    public void executeWithSeed(Location location, StructureUtil.StructureHolder holder, long seed) {
        execute(location, holder, new Random(seed));
    }

    @Override
    public void execute(Location location, StructureUtil.StructureHolder holder) {
        execute(location, holder, new Random());
    }

    private void execute(Location location, StructureUtil.StructureHolder holder, Random r) {
        // Replace certain materials
        for (Material mat : materials.keySet()) {
            Material[] replaceArray = materials.get(mat);

            for (int x = 0; x < holder.width; x++) {
                for (int y = 0; y < holder.height; y++) {
                    for (int z = 0; z < holder.depth; z++) {

                        Location replaceLocation = location.clone().add(new Vector(x, y, z));
                        if (replaceLocation.getBlock().getType() == mat) {
                            Material replaceMaterial = replaceArray[r.nextInt(0, replaceArray.length)];
                            replaceLocation.getBlock().setType(replaceMaterial);
                        }
                    }
                }
            }
        }
    }

    // ----- BUILDER -----

    public static class ReplaceBlocksStructureModuleBuilder extends StructureModuleBuilder<ReplaceBlocksStructureModule, ReplaceBlocksStructureModule.ReplaceBlocksStructureModuleBuilder> {

        // ----- VARIABLES -----

        Map<Material, Material[]> materials = new HashMap<>();

        // ----- CONSTRUCTORS -----

        public ReplaceBlocksStructureModuleBuilder() {}

        // ----- BUILDING -----

        @Override
        public ReplaceBlocksStructureModule build() {
            super.build();
            return obj;
        }

        // ----- SETTERS -----

        public ReplaceBlocksStructureModuleBuilder addMaterial(Material toReplace, Material... replaceWith) {
            materials.put(toReplace, replaceWith);
            return getThis();
        }

        // ----- HELPER METHODS -----

        @Override
        protected ReplaceBlocksStructureModule createObj() {
            return new ReplaceBlocksStructureModule(materials);
        }

        @Override
        protected ReplaceBlocksStructureModule.ReplaceBlocksStructureModuleBuilder getThis() {
            return this;
        }
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public StructureModuleType getModuleType() {
        return StructureModuleType.POST;
    }
}
