package se.fusion1013.plugin.cobaltcore.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.entity.modules.*;
import se.fusion1013.plugin.cobaltcore.entity.modules.ability.ChargeAbility;
import se.fusion1013.plugin.cobaltcore.manager.Manager;

import java.util.*;

public class CustomEntityManager extends Manager {

    // ----- VARIABLES -----

    private static final List<ICustomEntity> inbuiltCustomEntities = new ArrayList<>();
    private static final Map<UUID, ICustomEntity> summonedCustomEntities = new HashMap<>(); // TODO: When it is queried, check if any of the entities need to be removed if they have died.

    // ----- REGISTERED ENTITIES -----

    public static final ICustomEntity TEST_ENTITY = register(new CustomEntity.CustomEntityBuilder("test_entity", EntityType.ZOMBIE)
            .addExecuteOnSpawnModule(new EntityHealthModule(10))
            .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.CHEST, new ItemStack(Material.IRON_CHESTPLATE)))
            .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HAND, new ItemStack(Material.IRON_SWORD)))
            .addExecuteOnSpawnModule(new EntityPotionEffectModule(new PotionEffect(PotionEffectType.GLOWING, 1000000, 1, false, false)))
            .addExecuteOnSpawnModule(new EntitySpawnMethodModule((customEntity -> {
                Entity entity = customEntity.getSummonedEntity();
                if (entity instanceof Zombie zombie) zombie.setAdult();
            })))
            .addExecuteOnTickModule(new EntityBossBarModule("Test Entity", 10, BarColor.BLUE, BarStyle.SEGMENTED_6))
            .addExecuteOnDeathModule(new EntityDropModule(100, 2, new ItemStack(Material.DIAMOND), 0.2))
            .addAbilityModule(new ChargeAbility(8, 1, 5))
            .setCustomName("Test Entity")
            .build());

    // ----- REGISTER -----

    public static ICustomEntity register(ICustomEntity entity) {
        inbuiltCustomEntities.add(entity);
        return entity;
    }

    // ----- ENTITY MANAGING -----

    /**
     * Removes an entity from the summoned entities map.
     *
     * @param uuid the uuid of the entity to remove.
     * @return true if the entity was removed.
     */
    public static boolean removeEntity(UUID uuid) {
        ICustomEntity entity = summonedCustomEntities.remove(uuid);
        return entity != null;
    }

    /**
     * Force summons a <code>CustomEntity</code>, without checking spawning requirements.
     *
     * @param entityName the name of the entity to summon.
     * @param location the location to summon the entity.
     * @return true if the entity was summoned.
     */
    public static boolean forceSummonEntity(String entityName, Location location) {
        ICustomEntity entityToSummon = getCustomEntity(entityName);
        if (entityToSummon == null) return false;

        CustomEntity summoned = entityToSummon.forceSpawn(location);
        if (summoned == null) return false;

        summonedCustomEntities.put(summoned.getEntityUuid(), summoned);
        return true;
    }

    /**
     * Gets the registered <code>CustomEntity</code> with the registered name.
     *
     * @param entityName the internal name of the entity.
     * @return the <code>CustomEntity</code>. Returns null if not found.
     */
    private static ICustomEntity getCustomEntity(String entityName) {
        for (ICustomEntity entity : inbuiltCustomEntities) if (entity.getInternalName().equalsIgnoreCase(entityName)) return entity;
        return null;
    }

    /**
     * Gets an array of <code>CustomEntity</code> names.
     *
     * @return an array of registered <code>CustomEntity</code> names.
     */
    public static String[] getInternalEntityNames() {
        String[] names = new String[inbuiltCustomEntities.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = inbuiltCustomEntities.get(i).getInternalName();
        }
        return names;
    }

    // ----- CONSTRUCTORS -----

    public CustomEntityManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static CustomEntityManager INSTANCE = null;
    /**
     * Returns the object representing this <code>CustomEntityManager</code>.
     *
     * @return The object of this class
     */
    public static CustomEntityManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new CustomEntityManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
