package se.fusion1013.plugin.cobaltcore.world.sound;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SoundArea {

    // ----- VARIABLES -----

    public final UUID uuid;

    public final Location location;
    public final double activationRange;
    public final int cooldown; // Time in milliseconds
    public final String sound;

    private Map<Player, Long> playerActivationTimes = new HashMap<>();

    // ----- CONSTRUCTORS -----

    public SoundArea(Location location, double activationRange, int cooldown, String sound) {
        uuid = UUID.randomUUID();

        this.location = location;
        this.activationRange = activationRange;
        this.cooldown = cooldown;
        this.sound = sound;
    }

    public SoundArea(UUID uuid, Location location, double activationRange, int cooldown, String sound) {
        this.uuid = uuid;
        this.location = location;
        this.activationRange = activationRange;
        this.cooldown = cooldown;
        this.sound = sound;
    }

    // ----- ACTIVATION -----

    public boolean attemptActivation(Player player) {
        Location playerLocation = player.getLocation();
        double distance = location.distance(playerLocation);
        long currentTime = System.currentTimeMillis();
        long lastActivationTime;

        if (playerActivationTimes.get(player) == null) lastActivationTime = currentTime-cooldown;
        else lastActivationTime = playerActivationTimes.get(player);

        if (distance < activationRange && lastActivationTime + cooldown <= currentTime) {
            player.playSound(playerLocation, sound, 1, 1);
            playerActivationTimes.put(player, currentTime);

            return true;
        } else {
            return false;
        }
    }

}
