package se.fusion1013.plugin.cobaltcore.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityBossBarModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityEquipmentModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityHealthModule;
import se.fusion1013.plugin.cobaltcore.manager.Manager;

import java.util.*;

public class CustomEntityManager extends Manager {

    // ----- VARIABLES -----

    private static List<ICustomEntity> inbuiltCustomEntities = new ArrayList<>();
    private static Map<UUID, ICustomEntity> summonedCustomEntities = new HashMap<>(); // TODO: When it is queried, check if any of the entities need to be removed if they have died.

    // ----- REGISTERED ENTITIES -----

    public static final ICustomEntity TEST_ENTITY = register(new CustomEntity.CustomEntityBuilder("test_entity", EntityType.ZOMBIE)
            .addExecuteOnSpawnModule(new EntityHealthModule(10))
            .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.CHEST, new ItemStack(Material.IRON_CHESTPLATE)))
            .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HAND, new ItemStack(Material.IRON_SWORD)))
            .addExecuteOnTickModule(new EntityBossBarModule("Test Entity", 10, BarColor.BLUE, BarStyle.SEGMENTED_6))
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
