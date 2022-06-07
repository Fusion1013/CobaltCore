package se.fusion1013.plugin.cobaltcore.world.sound;

import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Holder for a custom sound
 */
public class CustomSound {

    // ----- VARIABLES -----

    final String sound;
    final int length;

    // ----- CONSTRUCTORS -----

    /**
     * Creates a new <code>CustomSound</code>.
     *
     * @param sound the sound.
     * @param length the length of the sound in milliseconds.
     */
    public CustomSound(String sound, int length) {
        this.sound = sound;
        this.length = length;
    }

    // ----- ACTIVATION -----

    public void playSound(Player player, Location location, float volume, float pitch) {
        player.playSound(location, sound, volume, pitch);
    }

    public void playSound(Player player, Location location, SoundCategory category, float volume, float pitch) {
        player.playSound(location, sound, category, volume, pitch);
    }

    public void playSound(World world, Location location, float volume, float pitch) {
        world.playSound(location, sound, volume, pitch);
    }

    public void playSound(World world, Location location, SoundCategory category, float volume, float pitch) {
        world.playSound(location, sound, category, volume, pitch);
    }

    // ----- GETTERS / SETTERS -----

    public String getSound() {
        return sound;
    }

    public int getLength() {
        return length;
    }
}
