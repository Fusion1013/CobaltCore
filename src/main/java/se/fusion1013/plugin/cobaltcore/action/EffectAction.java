package se.fusion1013.plugin.cobaltcore.action;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.plugin.cobaltcore.action.system.AbstractAction;
import se.fusion1013.plugin.cobaltcore.action.system.ActionResult;
import se.fusion1013.plugin.cobaltcore.action.system.IActionResult;
import se.fusion1013.plugin.cobaltcore.action.system.ILivingEntityAction;

import java.util.Map;

public class EffectAction extends AbstractAction implements ILivingEntityAction {

    private PotionEffectType effectType;
    private int duration;
    private int amplifier;
    private boolean ambient;
    private boolean particles;
    private boolean icon;
    private final PotionEffect effect;

    protected EffectAction(Map<?, ?> data) {
        super(data);

        if (data.containsKey("effect")) this.effectType = PotionEffectType.getByName(data.get("effect").toString());
        if (data.containsKey("duration")) this.duration = (int) data.get("duration");
        if (data.containsKey("amplifier")) this.amplifier = (int) data.get("amplifier");
        if (data.containsKey("ambient")) this.ambient = (boolean) data.get("ambient");
        if (data.containsKey("particles")) this.particles = (boolean) data.get("particles");
        if (data.containsKey("icon")) this.icon = (boolean) data.get("icon");

        if (effectType == null) effectType = PotionEffectType.ABSORPTION;

        effect = new PotionEffect(effectType, duration, amplifier, ambient, particles, icon);
    }

    @Override
    public String getInternalName() {
        return "effect_action";
    }

    @Override
    public IActionResult activate() {
        if (extraData.containsKey("living_entity")) return activate((LivingEntity) extraData.get("living_entity"));
        if (extraData.containsKey("entity")) return activate((Entity) extraData.get("entity"));
        return new ActionResult(false);
    }

    @Override
    public IActionResult activate(Entity entity) {
        if (entity instanceof LivingEntity living) return activate(living);
        return new ActionResult(false);
    }

    @Override
    public IActionResult activate(LivingEntity entity) {
        entity.addPotionEffect(effect);
        return new ActionResult(true);
    }
}
