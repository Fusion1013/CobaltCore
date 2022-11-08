package se.fusion1013.plugin.cobaltcore.entity.modules;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.bar.CustomBossBar;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;

public class EntityBossBarModule extends EntityModule implements ITickExecutable, Cloneable {

    // ----- VARIABLES -----

    NamespacedKey key;
    CustomBossBar bossBar;
    final String bossBarTitle;
    double bossBarActivationRange = -1;
    BarColor bossBarColor = BarColor.WHITE;
    BarStyle bossBarStyle = BarStyle.SOLID;

    // ----- CONSTRUCTORS -----

    /**
     * Adds a boss bar to the entity.
     *
     * @param bossBarTitle the title of the boss bar.
     */
    public EntityBossBarModule(String bossBarTitle) {
        this.bossBarTitle = bossBarTitle;
    }

    public EntityBossBarModule(String bossBarTitle, double bossBarActivationRange) {
        this(bossBarTitle);
        this.bossBarActivationRange = bossBarActivationRange;
    }

    public EntityBossBarModule(String bossBarTitle, double bossBarActivationRange, BarColor bossBarColor) {
        this(bossBarTitle, bossBarActivationRange);
        this.bossBarColor = bossBarColor;
    }

    public EntityBossBarModule(String bossBarTitle, double bossBarActivationRange, BarColor bossBarColor, BarStyle bossBarStyle) {
        this(bossBarTitle, bossBarActivationRange, bossBarColor);
        this.bossBarStyle = bossBarStyle;
    }

    // ----- EXECUTE -----

    @Override
    public void execute(CustomEntity customEntity, ISpawnParameters spawnParameters) {

        // If the entity is not alive, remove the bossbar.
        if (!customEntity.isAlive()) {
            bossBar.remove();
            return;
        }

        Entity entity = customEntity.getSummonedEntity();

        if (entity instanceof LivingEntity living) {

            // If bossbar has not been created yet, create it.
            if (bossBar == null) {
                key = new NamespacedKey(CobaltCore.getInstance(), customEntity.getEntityUuid().toString() + ".bossBar");
                bossBar = new CustomBossBar(entity, bossBarTitle, net.kyori.adventure.bossbar.BossBar.Color.valueOf(bossBarColor.toString()), net.kyori.adventure.bossbar.BossBar.Overlay.valueOf(bossBarStyle.toString().replace("SEGMENTED", "NOTCHED")), .4);
            }

            // Update bossbar
            bossBar.update();
        }
    }

    // ----- GETTERS / SETTERS -----

    public CustomBossBar getBossBar() {
        return bossBar;
    }

    // ----- CLONE -----

    public EntityBossBarModule(EntityBossBarModule target) {
        this.key = null;
        this.bossBar = null;

        this.bossBarTitle = target.bossBarTitle;
        this.bossBarActivationRange = target.bossBarActivationRange;
        this.bossBarColor = target.bossBarColor;
        this.bossBarStyle = target.bossBarStyle;
    }

    public EntityBossBarModule clone() {
        return new EntityBossBarModule(this);
    }

}
