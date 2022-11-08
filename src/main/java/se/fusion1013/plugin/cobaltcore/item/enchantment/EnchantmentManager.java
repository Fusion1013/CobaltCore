package se.fusion1013.plugin.cobaltcore.item.enchantment;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.ItemUtil;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentManager extends Manager implements Listener {

    // ----- VARIABLES -----

    public static final List<ICobaltEnchantment[]> REGISTERED_ENCHANTMENTS = new ArrayList<>();

    // ----- REGISTER -----

    private static final Class<CobaltEnchantment> CORE_ENCHANTMENTS = registerEnchantment(CobaltEnchantment.class);

    /**
     * Registers a new <code>ICobaltEnchantment</code>.
     *
     * @param enchantment the <code>ICobaltEnchantment</code> to register.
     * @return the <code>ICobaltEnchantment</code>.
     */
    public static <T extends Enum<T>> Class<T> registerEnchantment(Class<T> enchantment) {
        ICobaltEnchantment[] enchantments = new ICobaltEnchantment[enchantment.getEnumConstants().length];
        for (int i = 0; i < enchantments.length; i++) enchantments[i] = (ICobaltEnchantment) enchantment.getEnumConstants()[i];
        REGISTERED_ENCHANTMENTS.add(enchantments);
        return enchantment;
    }

    // ----- LISTENERS -----

    @EventHandler
    public void anvilPrepareEvent(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        ItemStack firstItem = inventory.getFirstItem();
        ItemStack secondItem = inventory.getSecondItem();

        if (firstItem == null || secondItem == null) return;

        ItemStack resultItem = inventory.getResult();
        if (resultItem == null) return;

        resultItem = ItemUtil.mergeAnvilItems(firstItem, secondItem, resultItem);

        event.setResult(resultItem);
        event.getInventory().setResult(resultItem);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();
        if (damager instanceof LivingEntity livingDamager) {
            EntityEquipment eq = livingDamager.getEquipment();
            if (eq == null) return;
            ItemStack heldItem = eq.getItemInMainHand();

            // -- Check enchants that trigger on hit

            if (damaged instanceof LivingEntity livingDamaged) {

                // Wither
                int witherLevel = CobaltEnchantment.WITHER.getLevel(heldItem);
                if (witherLevel > 0) executeWither(livingDamaged, witherLevel);

                // Poison
                int poisonLevel = CobaltEnchantment.POISON.getLevel(heldItem);
                if (poisonLevel > 0) executePoison(livingDamaged, poisonLevel);

                // -- Check enchants that trigger on entity death
                if (livingDamaged.getHealth() - event.getFinalDamage() <= 0) { // TODO: Check totem

                    // Lifesteal
                    executeLifesteal(livingDamager);

                }
            }
        }
    }

    private void executeLifesteal(LivingEntity damager) {

        int lifestealLevel = 0;

        if (damager.getEquipment() != null) {
            for (ItemStack item : damager.getEquipment().getArmorContents()) {
                lifestealLevel += CobaltEnchantment.LIFESTEAL.getLevel(item);
            }
            lifestealLevel += CobaltEnchantment.LIFESTEAL.getLevel(damager.getEquipment().getItemInMainHand());
            lifestealLevel += CobaltEnchantment.LIFESTEAL.getLevel(damager.getEquipment().getItemInOffHand());
        }

        // Heal entity
        if (lifestealLevel > 0) {
            AttributeInstance maxHealthAttribute = damager.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            double maxHealth = 20;
            if (maxHealthAttribute != null) maxHealth = maxHealthAttribute.getValue();
            damager.setHealth(Math.min(damager.getHealth() + lifestealLevel, maxHealth));

            // Play effects
            World world = damager.getWorld();
            Location location = damager.getLocation();

            world.spawnParticle(Particle.HEART, location, 2, .2, .1, .2, 0);
            if (damager instanceof Player player)
                player.playSound(location, Sound.ENTITY_WITCH_DRINK, SoundCategory.PLAYERS, 1, 1);
        }
    }

    // TODO: Tweak values
    private void executePoison(LivingEntity entity, int level) {
        int duration = 60;
        if (entity.hasPotionEffect(PotionEffectType.POISON)) {
            duration = entity.getPotionEffect(PotionEffectType.POISON).getDuration() + 20;
            duration = Math.min(duration, level * 3 * 20); // Increased duration caps at level * 3s
        }

        entity.addPotionEffect(
                new PotionEffect(PotionEffectType.POISON, duration, level-1, false, true)
        );
    }

    private void executeWither(LivingEntity entity, int level) {
        int duration = 60;
        if (entity.hasPotionEffect(PotionEffectType.WITHER)) {
            duration = entity.getPotionEffect(PotionEffectType.WITHER).getDuration() + 20;
            duration = Math.min(duration, level * 3 * 20); // Increased duration caps at level * 3s
        }

        entity.addPotionEffect(
                new PotionEffect(PotionEffectType.WITHER, duration, level-1, false, true)
        );
    }

    // ----- GETTERS / SETTERS -----

    public static int getTotalEnchantLevel(ICobaltEnchantment enchantment, Entity entity) {
        int level = 0;

        if (entity instanceof LivingEntity living) {
            EntityEquipment equipment = living.getEquipment();
            if (equipment == null) return level;

            for (ItemStack stack : equipment.getArmorContents()) level += enchantment.getLevel(stack);
            level += enchantment.getLevel(equipment.getItemInMainHand());
            level += enchantment.getLevel(equipment.getItemInOffHand());
        }

        return level;
    }

    public static String[] getEnchantmentNames() {
        List<String> names = new ArrayList<>();
        for (ICobaltEnchantment[] registeredEnchantments : REGISTERED_ENCHANTMENTS) {
            for (ICobaltEnchantment enchantment : registeredEnchantments) {
                names.add(enchantment.getInternalName());
            }
        }
        for (Enchantment enchantment : Enchantment.values()) {
            names.add(enchantment.getKey().getKey());
        }
        return names.toArray(new String[0]);
    }

    public static EnchantmentWrapper getEnchantment(String enchantmentName, int level, boolean ignoreLevelRestriction) {

        ICobaltEnchantment cobaltEnchantment = getEnchantment(enchantmentName);
        if (cobaltEnchantment != null) return new EnchantmentWrapper(cobaltEnchantment, level, ignoreLevelRestriction);

        for (Enchantment enchantment : Enchantment.values())
            if (enchantment.getKey().getKey().equalsIgnoreCase(enchantmentName))
                return new EnchantmentWrapper(enchantment, level, ignoreLevelRestriction);

        return null;
    }

    public static ICobaltEnchantment getEnchantment(String name) {
        for (ICobaltEnchantment[] registeredEnchantments : REGISTERED_ENCHANTMENTS) {
            for (ICobaltEnchantment enchantment : registeredEnchantments) {
                if (enchantment.getInternalName().equalsIgnoreCase(name)) return enchantment;
            }
        }
        return null;
    }

    // ----- CONSTRUCTORS -----

    public EnchantmentManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltCore.getInstance());
    }

    @Override
    public void disable() {

    }
}
