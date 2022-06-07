package se.fusion1013.plugin.cobaltcore.world.structure.structure;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.NoiseGenerator;
import se.fusion1013.plugin.cobaltcore.util.StructureUtil;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.IStructureModule;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.StructureModuleType;

import java.util.*;

public class SimpleStructure extends AbstractStructure implements IStructure {

    // ----- VARIABLES -----

    boolean onGround = false;

    Vector offset = new Vector();

    // Pre-gen Structure
    StructureUtil.StructureHolder structureHolder;

    // Static
    static final Material[] nonGroundBlocks = new Material[] {Material.AIR, Material.CAVE_AIR, Material.WATER};

    // ----- CONSTRUCTORS -----

    public SimpleStructure(SimpleStructure target) {
        super(target);

        this.onGround = target.onGround;
        this.offset = target.offset;
        this.structureHolder = target.structureHolder;
    }

    public SimpleStructure(Plugin ownerPlugin, int id, String structureName, String structureFilePath) {
        super(ownerPlugin, id, structureName, structureFilePath);

        if (!structureFilePath.equalsIgnoreCase("")) {
            structureHolder = StructureUtil.preLoadStructure(ownerPlugin, structureFilePath);
            if (structureHolder == null) {
                ownerPlugin.getLogger().info("Could not create structure " + structureName + ", file " + structureFilePath + " not found");
            }
        }
    }

    // ----- GENERATION METHOD -----

    @Override
    public void generate(Location location) {

        // Run all pre-generation Structure Modules
        for (IStructureModule module : structureModules) if (module.getModuleType() == StructureModuleType.PRE) module.execute(location, structureHolder);

        // TODO: Move the below ones to structure modules
        // TODO: Move pre-gen and post-gen to abstract method (???)

        // If onGround, shift structure down to ground
        if (onGround) {
            for (int y = location.getBlockY(); y > location.getWorld().getMinHeight(); y--) {
                location.setY(y);

                boolean isGround = true;
                for (Material mat : nonGroundBlocks) {
                    if (location.getBlock().getType() == mat) isGround = false;
                }
                if (isGround) break;
            }
        }

        // Offset
        location.add(offset);

        // Place the structure
        structureHolder.placeStructure(location);

        // Post Process functions
        // postProcess(location, structureHolder); // TODO: Move to structure modules

        for (IStructureModule module : structureModules) if (module.getModuleType() == StructureModuleType.POST) module.execute(location, structureHolder);
    }

    @Override
    public void rotate() {
        this.structureHolder = structureHolder.getRotatedCopy();
    }

    // ----- BUILDER -----

    protected static abstract class AbstractSimpleStructureBuilder<T extends SimpleStructure, B extends AbstractSimpleStructureBuilder> extends AbstractStructureBuilder<T, B> {

        // ----- VARIABLES -----

        boolean onGround = false;

        Vector offset = new Vector();

        // ----- CONSTRUCTORS -----

        public AbstractSimpleStructureBuilder(Plugin ownerPlugin, int id, String structureName, String structureFilePath) {
            super(ownerPlugin, id, structureName, structureFilePath);
        }

        // ----- BUILDER -----

        @Override
        public T build() {
            super.build();

            obj.setOnGround(onGround);
            obj.setOffset(offset);
            obj.setStructureModules(structureModules);

            return obj;
        }

        // ----- GETTERS / SETTERS -----

        public B addStructureModule(IStructureModule module) {
            structureModules.add(module);
            return getThis();
        }

        public B setOnGround(boolean onGround) {
            this.onGround = onGround;
            return getThis();
        }

        public B setOffset(Vector offset) {
            this.offset = offset;
            return getThis();
        }
    }

    public static class SimpleStructureBuilder extends AbstractSimpleStructureBuilder<SimpleStructure, SimpleStructureBuilder> {

        // ----- CONSTRUCTORS -----

        public SimpleStructureBuilder(Plugin ownerPlugin, int id, String structureName, String structureFilePath) {
            super(ownerPlugin, id, structureName, structureFilePath);
        }

        // ----- HELPER METHODS -----

        @Override
        protected SimpleStructure createObj() {
            return new SimpleStructure(plugin, id, structureName, structureFilePath);
        }

        @Override
        protected SimpleStructureBuilder getThis() {
            return this;
        }
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public NoiseGenerator getNoiseGenerator() {
        return noiseGenerator;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void setOffset(Vector offset) {
        this.offset = offset;
    }

    public void setStructureModules(List<IStructureModule> structureModules) {
        this.structureModules = structureModules;
    }

    @Override
    public StructureUtil.StructureHolder getStructureHolder() {
        return structureHolder;
    }

    @Override
    public IStructure getRotatedClone() {
        SimpleStructure structure = new SimpleStructure(this);
        structure.rotate();
        return structure;
    }

    // ----- ENUM -----

    public enum StructureType {
        SURFACE,
        UNDERGROUND
    }


}
