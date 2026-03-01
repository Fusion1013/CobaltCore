package se.fusion1013.cobaltCore.util;

import org.bukkit.Registry;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;

import java.util.List;
import java.util.Random;

public class PotionUtil {

    private static final Random random = new Random();

    public static PotionEffectType getRandomInCategory(PotionEffectTypeCategory category) {
        List<PotionEffectType> potions = Registry.POTION_EFFECT_TYPE.stream().filter(p -> p.getCategory() == category).toList();
        return potions.get(random.nextInt(potions.size()));
    }

}
