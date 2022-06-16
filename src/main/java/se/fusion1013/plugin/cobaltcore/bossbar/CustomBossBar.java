package se.fusion1013.plugin.cobaltcore.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltCore;

public class CustomBossBar {

    // ----- VARIABLES -----

    // Given variables
    private final Entity owner;
    private final String title;
    private final BarColor color;
    private final BarStyle style;

    private double activationRange = -1;

    // Created variables
    NamespacedKey key;
    BossBar bossBar;

    // ----- CONSTRUCTORS -----

    public CustomBossBar(Entity owner, String title, BarColor color, BarStyle style) {
        this.owner = owner;
        this.title = title;
        this.color = color;
        this.style = style;

        update();
    }

    public CustomBossBar(Entity owner, String title, BarColor color, BarStyle style, double activationRange) {
        this.owner = owner;
        this.title = title;
        this.color = color;
        this.style = style;
        this.activationRange = activationRange;

        update();
    }

    // ----- UPDATING -----

    public void update() {

        // If the entity is not alive, remove the bossbar
        if (!owner.isValid()) {
            remove();
            return;
        }

        if (owner instanceof LivingEntity living) {

            // If bossbar has not been created yet, create it
            if (bossBar == null) {
                key = new NamespacedKey(CobaltCore.getInstance(), owner.getUniqueId() + ".bossbar");
                bossBar = Bukkit.createBossBar(key, title, color, style);
            }

            // Set bossbar value
            double currentHealth = living.getHealth();
            AttributeInstance attribute = living.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (attribute == null) return;
            double maxHealth = attribute.getBaseValue();
            bossBar.setProgress(Math.max(0, Math.min(currentHealth / maxHealth, 1)));

            // Add players to the bossbar
            for (Player player : Bukkit.getOnlinePlayers()) {
                // If activation range has not been set, add all players
                if (activationRange < 0) {
                    bossBar.addPlayer(player);
                    continue;
                }

                // Get distance to player.
                double distanceSquared = player.getLocation().distanceSquared(living.getLocation());

                // If the distance to the player is less than the activation range of the bossbar, show the player the bossbar.
                // Otherwise remove the player.
                if (distanceSquared < activationRange * activationRange) {
                    bossBar.addPlayer(player);
                } else {
                    bossBar.removePlayer(player);
                }
            }
        }
    }

    public void remove() {
        bossBar.removeAll();
        Bukkit.removeBossBar(key);
    }

    // ----- VALIDITY CHECKS -----

    public boolean isValid() {
        return owner.isValid();
    }

    // ----- GETTERS / SETTERS -----


    public Entity getOwner() {
        return owner;
    }

    public String getTitle() {
        return title;
    }

    public BarColor getColor() {
        return color;
    }

    public BarStyle getStyle() {
        return style;
    }
}
