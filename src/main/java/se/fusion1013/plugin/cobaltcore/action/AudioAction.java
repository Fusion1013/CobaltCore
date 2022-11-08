package se.fusion1013.plugin.cobaltcore.action;

import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.plugin.cobaltcore.action.system.AbstractAction;
import se.fusion1013.plugin.cobaltcore.action.system.IEntityAction;
import se.fusion1013.plugin.cobaltcore.action.system.ILivingEntityAction;
import se.fusion1013.plugin.cobaltcore.action.system.ILocationAction;

import java.util.Map;

public class AudioAction extends AbstractAction implements IEntityAction, ILivingEntityAction, ILocationAction {

    // ----- VARIABLES -----

    private String sound = "cobalt.poof";
    private SoundCategory category = SoundCategory.MASTER;
    private double volume = 1;
    private double pitch = 1;

    private SoundTarget soundTarget = SoundTarget.ALL;

    public AudioAction(Map<?, ?> data) {
        super(data);

        if (data.containsKey("sound")) sound = (String) data.get("sound");
        if (data.containsKey("category")) category = EnumUtils.findEnumInsensitiveCase(SoundCategory.class, (String) data.get("category"));
        if (data.containsKey("volume")) volume = (double) data.get("volume");
        if (data.containsKey("pitch")) pitch = (double) data.get("pitch");

        if (data.containsKey("sound_target")) soundTarget = EnumUtils.findEnumInsensitiveCase(SoundTarget.class, (String) data.get("sound_target"));
    }

    @Override
    public String getInternalName() {
        return "audio_action";
    }

    @Override
    public boolean activate(Entity entity) {
        if (entity instanceof Player player && soundTarget == SoundTarget.SELF) {
            player.playSound(entity.getLocation(), sound, category, (float) volume, (float) pitch);
        } else {
            entity.getLocation().getWorld().playSound(entity.getLocation(), sound, category, (float) volume, (float) pitch);
        }

        return true;
    }

    @Override
    public boolean activate(Location location) {
        location.getWorld().playSound(location, sound, category, (float) volume, (float) pitch);
        return true;
    }

    @Override
    public boolean activate(LivingEntity entity) {
        activate((Entity) entity);
        return true;
    }

    public enum SoundTarget {
        ALL,
        SELF
    }
}
