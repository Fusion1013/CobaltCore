package se.fusion1013.plugin.cobaltcore.bar;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.config.ConfigManager;
import se.fusion1013.plugin.cobaltcore.util.ColorUtil;
import se.fusion1013.plugin.cobaltcore.util.StringUtil;
import se.fusion1013.plugin.cobaltcore.util.animation.EasingUtil;

import java.util.Random;

public class CustomBossBar {

    // ----- VARIABLES -----

    private final Entity owner;
    private final String title;
    private final Component titleComponent;
    private BossBar adventureBossBar;
    private final BossBar.Color bossBarColor;
    private final BossBar.Overlay bossBarOverlay;
    private final double animationSpeed;
    private double activationRange = -1;

    private boolean isActive = true;

    // Value tracking
    private float currentValue = 1;
    private float targetValue = 1;

    // Animation
    private float tick;

    // ----- CONSTRUCTORS -----

    public CustomBossBar(Entity owner, String title, BossBar.Color color, BossBar.Overlay overlay, double animationSpeed) {
        this.owner = owner;
        this.title = title;
        this.titleComponent = Component.text(title);
        this.bossBarColor = color;
        this.bossBarOverlay = overlay;
        this.animationSpeed = animationSpeed;

        update();
        // update();
    }

    public CustomBossBar(Entity owner, String title, BossBar.Color color, BossBar.Overlay overlay, double animationSpeed, double activationRange) {
        this.owner = owner;
        this.title = title;
        this.titleComponent = Component.text(title);
        this.bossBarColor = color;
        this.bossBarOverlay = overlay;
        this.activationRange = activationRange;
        this.animationSpeed = animationSpeed;


        update();
        // update();
    }

    // ----- ANIMATION -----

    private void animate() {
        if (adventureBossBar == null) return;

        int period = (int) ConfigManager.getInstance().getFromConfig(CobaltCore.getInstance(), "cobalt.yml", "bossbar-update-period");

        // Animate text
        int middle = title.length() / 2;
        int hideAmount = Math.max(0, middle - ((int) tick / 2));

        Random r = new Random();
        StringBuilder randomStart = new StringBuilder();
        StringBuilder randomEnd = new StringBuilder();
        for (int i = 0; i < title.length(); i++) {
            char c1 = (char)(r.nextInt(26) + 'a');
            char c2 = (char)(r.nextInt(26) + 'a');
            if (r.nextDouble(hideAmount+1) >= i+1) randomStart.append(c1);
            else randomStart.append(" ");
            if (r.nextDouble(hideAmount+1) >= i+1) randomEnd.append(c2);
            else randomEnd.append(" ");
        }
        randomStart.reverse();

        String startText = title.substring(0, hideAmount);
        String middleText = title.substring(Math.min(hideAmount, middle), Math.max(title.length() - hideAmount, middle));
        String endText = title.substring(title.length() - hideAmount);

        float colorRatio = EasingUtil.easeInOutSine(tick, 0, 1, 30);
        TextColor color = ColorUtil.fade(colorRatio, NamedTextColor.YELLOW, NamedTextColor.GOLD);

        Component text = Component.text(randomStart + startText).font(Key.key("minecraft:fin_small")).color(color)
                .append(Component.text(middleText).font(Key.key("minecraft:default")).color(color))
                .append(Component.text(endText + randomEnd).font(Key.key("minecraft:fin_small")).color(color));

        adventureBossBar.name(text);

        // Animate value
        if (tick < 15) {
            currentValue = Math.min(EasingUtil.easeInOutSine(tick, 0, targetValue, 15), 1);
        } else {
            currentValue = targetValue;
        }

        adventureBossBar.progress(currentValue);

        if (tick < 10000) tick += period * animationSpeed;
    }

    // ----- UPDATING -----

    public void update() {
        if (!isActive) return;

        // If the entity is not alive, remove the bossbar
        if (!owner.isValid()) {
            remove();
            return;
        }

        // If the bossbar does not exist, initialize it
        if (adventureBossBar == null) initBossBar();

        // Animate
        animate();

        // Set bossbar value
        if (owner instanceof LivingEntity living) {
            double currentHealth = living.getHealth();
            AttributeInstance attribute = living.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (attribute == null) return;
            double maxHealth = attribute.getBaseValue();
            targetValue = (float) Math.max(0, Math.min(currentHealth / maxHealth, 1));
        }

        // Add players to the bossbar
        for (Player player : Bukkit.getOnlinePlayers()) {
            // If activation range has not been set, add all players
            if (activationRange < 0) {
                player.showBossBar(adventureBossBar);
                continue;
            }

            // Get distance to player.
            double distanceSquared = player.getLocation().distanceSquared(owner.getLocation());

            // If the distance to the player is less than the activation range of the bossbar, show the player the bossbar.
            // Otherwise remove the player.
            if (distanceSquared < activationRange * activationRange) {
                player.showBossBar(adventureBossBar);
            } else {
                player.hideBossBar(adventureBossBar);
            }
        }
    }

    private void initBossBar() {
        if (adventureBossBar == null) {
            adventureBossBar = BossBar.bossBar(titleComponent, .8f, bossBarColor, bossBarOverlay);
        }
    }

    /*
    public void update() {

        /*

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

            // Animate the bossbar
            animate();

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
    */

    public void remove() {
        for (Player p : Bukkit.getOnlinePlayers()) p.hideBossBar(adventureBossBar);
        adventureBossBar = null;
        isActive = false;
    }

    // ----- VALIDITY CHECKS -----

    public boolean isValid() {
        return owner.isValid();
    }

    // ----- GETTERS / SETTERS -----

    public Entity getOwner() {
        return owner;
    }

    public BossBar getAdventureBossBar() {
        return adventureBossBar;
    }

    public String getTitle() {
        return title;
    }
}
