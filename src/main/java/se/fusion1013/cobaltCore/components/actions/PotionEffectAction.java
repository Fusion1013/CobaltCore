package se.fusion1013.cobaltCore.components.actions;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import se.fusion1013.cobaltCore.variable.*;

import java.util.List;
import java.util.Map;

public class PotionEffectAction extends AbstractAction {

    private final PotionTypeVariable potionType = new PotionTypeVariable("type", PotionEffectType.SPEED);
    private final DurationVariable duration = new DurationVariable("duration", 100);
    private final IntVariable amplifier = new IntVariable("amplifier", 0);
    private final BooleanVariable ambient = new BooleanVariable("ambient", false);
    private final BooleanVariable particles = new BooleanVariable("particles", false);
    private final BooleanVariable icon = new BooleanVariable("icon", false);

    @Override
    protected List<AbstractVariable<?, ?, ?, ?>> getVariables() {
        return List.of(potionType, duration, amplifier, ambient, particles, icon);
    }

    @Override
    public void execute(Map<String, Object> context) {
        LivingEntity entity = getTargetLivingEntity(context);
        entity.addPotionEffect(new PotionEffect(potionType.getValue(), duration.getValue(), amplifier.getValue(), ambient.getValue(), particles.getValue(), icon.getValue()));
    }

    @Override
    public String getId() {
        return "effect";
    }
}
