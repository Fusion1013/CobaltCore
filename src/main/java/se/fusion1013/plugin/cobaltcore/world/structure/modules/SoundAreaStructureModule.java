package se.fusion1013.plugin.cobaltcore.world.structure.modules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.util.StructureUtil;
import se.fusion1013.plugin.cobaltcore.world.sound.SoundArea;
import se.fusion1013.plugin.cobaltcore.world.sound.SoundAreaManager;

public class SoundAreaStructureModule extends StructureModule implements IStructureModule {

    // ----- VARIABLES -----

    SoundArea soundArea;
    Material replaceMaterial;

    // ----- CONSTRUCTORS -----

    public SoundAreaStructureModule(SoundArea soundArea, Material replaceMaterial) {
        this.soundArea = soundArea;
        this.replaceMaterial = replaceMaterial;
    }

    // ----- SPAWNER PLACING -----

    @Override
    public void execute(Location location, StructureUtil.StructureHolder holder) {
        executeWithSeed(location, holder, 0);
    }

    @Override
    public void executeWithSeed(Location location, StructureUtil.StructureHolder holder, long seed) {
        for (int x = 0; x < holder.width; x++) {
            for (int y = 0; y < holder.height; y++) {
                for (int z = 0; z < holder.depth; z++) {
                    Location replaceLocation = location.clone().add(new Vector(x, y, z));
                    if (replaceLocation.getBlock().getType() == replaceMaterial) {
                        SoundAreaManager.createSoundArea(replaceLocation, soundArea.sound, soundArea.activationRange, soundArea.cooldown);
                        replaceLocation.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
    }

    @Override
    public StructureModuleType getModuleType() {
        return StructureModuleType.POST;
    }

}
