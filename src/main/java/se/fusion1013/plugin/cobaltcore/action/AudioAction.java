package se.fusion1013.plugin.cobaltcore.action;

import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.plugin.cobaltcore.action.system.*;

import java.util.Map;

public class AudioAction extends AbstractAction implements IEntityAction, ILivingEntityAction, ILocationAction {

    //region FIELDS

    private String sound = "cobalt.poof";
    private SoundCategory category = SoundCategory.MASTER;
    private double volume = 1;
    private double pitch = 1;

    private SoundTarget soundTarget = SoundTarget.ALL;

    //endregion

    //region CONSTRUCTOR

    public AudioAction(Map<?, ?> data) {
        super(data);

        if (data.containsKey("sound")) sound = (String) data.get("sound");
        if (data.containsKey("category")) category = EnumUtils.findEnumInsensitiveCase(SoundCategory.class, (String) data.get("category"));
        if (data.containsKey("volume")) volume = (double) data.get("volume");
        if (data.containsKey("pitch")) pitch = (double) data.get("pitch");

        if (data.containsKey("sound_target")) soundTarget = EnumUtils.findEnumInsensitiveCase(SoundTarget.class, (String) data.get("sound_target"));
    }

    //endregion

    //region GETTERS/SETTERS

    @Override
    public String getInternalName() {
        return "audio_action";
    }

    //endregion

    //region ACTIVATION

    @Override
    public IActionResult activate() {
        if (extraData.containsKey("entity")) { // Prioritize entity activation
            Entity entity = (Entity) extraData.get("entity");
            return activate(entity);
        } else if (extraData.containsKey("location")) {
            Location location = (Location) extraData.get("location");
            return activate(location);
        }

        return new ActionResult(false);
    }

    @Override
    public IActionResult activate(Entity entity) {
        if (entity instanceof Player player && soundTarget == SoundTarget.SELF) {
            player.playSound(entity.getLocation(), sound, category, (float) volume, (float) pitch);
        } else {
            entity.getLocation().getWorld().playSound(entity.getLocation(), sound, category, (float) volume, (float) pitch);
        }

        return new ActionResult(true);
    }

    @Override
    public IActionResult activate(Location location) {
        location.getWorld().playSound(location, sound, category, (float) volume, (float) pitch);
        return new ActionResult(true);
    }

    @Override
    public IActionResult activate(LivingEntity entity) {
        return activate((Entity) entity);
    }

    //endregion

    public enum SoundTarget {
        ALL,
        SELF
    }
}
