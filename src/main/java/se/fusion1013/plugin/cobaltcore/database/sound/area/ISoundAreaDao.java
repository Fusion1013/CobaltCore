package se.fusion1013.plugin.cobaltcore.database.sound.area;

import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.database.system.IDao;
import se.fusion1013.plugin.cobaltcore.world.sound.SoundArea;

import java.util.Map;
import java.util.UUID;

public interface ISoundAreaDao extends IDao {

    Map<Location, SoundArea> getSoundAreas();

    void saveSoundAreas(Map<Location, SoundArea> soundAreaMap);

    void removeSoundArea(UUID uuid);

}
