package se.fusion1013.plugin.cobaltcore.entity.modules;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;

import java.util.Random;

public class EntityAmbientSoundModule extends EntityModule implements ITickExecutable {

    // ----- VARIABLES -----

    private Sound sound;
    private String soundString;

    private final float volume;
    private final float pitch;
    private final int randomDelay;

    Random r = new Random();

    // ----- CONSTRUCTOR -----

    /**
     * Creates a new <code>EntityAmbientSoundModule</code>.
     *
     * @param sound the sound that it plays.
     * @param randomDelay the random delay between two executions of the sound. Higher randomDelay, higher delay.
     * @param pitch the pitch of the sound.
     * @param volume the volume of the sound.
     */
    public EntityAmbientSoundModule(Sound sound, float volume, float pitch, int randomDelay) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.randomDelay = randomDelay;
    }

    /**
     * Creates a new <code>EntityAmbientSoundModule</code>.
     *
     * @param sound the sound that it plays.
     * @param randomDelay the random delay between two executions of the sound. Higher randomDelay, higher delay.
     * @param pitch the pitch of the sound.
     * @param volume the volume of the sound.
     */
    public EntityAmbientSoundModule(String sound, float volume, float pitch, int randomDelay) {
        this.soundString = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.randomDelay = randomDelay;
    }

    // ----- EXECUTE -----

    @Override
    public void execute(CustomEntity customEntity) {
        Location currentLocation = customEntity.getSummonedEntity().getLocation();
        World currentWorld = currentLocation.getWorld();

        if (currentWorld == null) return;

        if (sound != null) if (r.nextInt(0, randomDelay) == 0) currentWorld.playSound(currentLocation, sound, volume, pitch); // TODO: Check if this is the correct order of volume / pitch
        if (soundString != null) if (r.nextInt(0, randomDelay) == 0) currentWorld.playSound(currentLocation, soundString, volume, pitch);
    }

    // ----- CLONE -----

    public EntityAmbientSoundModule(EntityAmbientSoundModule target) {
        if (target.sound != null) this.sound = target.sound;
        if (target.soundString != null) this.soundString = target.soundString;
        this.volume = target.volume;
        this.pitch = target.pitch;
        this.randomDelay = target.randomDelay;
        this.r = new Random();
    }

    @Override
    public EntityAmbientSoundModule clone() {
        return new EntityAmbientSoundModule(this);
    }
}
