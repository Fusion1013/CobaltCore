package se.fusion1013.plugin.cobaltcore.world.sound;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandExecutor;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandHandler;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandManager;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandResult;
import se.fusion1013.plugin.cobaltcore.database.sound.area.ISoundAreaDao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltcore.util.kdtree.Hyperpoint;
import se.fusion1013.plugin.cobaltcore.util.kdtree.IMultiPoint;
import se.fusion1013.plugin.cobaltcore.util.kdtree.KDTree;

import java.util.HashMap;
import java.util.Map;

public class SoundAreaManager extends Manager implements Listener, CommandExecutor {

    // ----- VARIABLES -----

    private static Map<Location, SoundArea> soundAreas = new HashMap<>();
    private static final KDTree soundAreaLocations = new KDTree(3); // TODO: Use a priority queue instead (???)

    // ----- CREATE -----

    @CommandHandler(
            parameterNames = {"location","custom_sound","activation_range"}
    )
    public static CommandResult createSoundArea(Location location, String customSound, double activationRange) {
        CustomSound sound = SoundManager.getSound(customSound);
        return createSoundArea(location, sound, activationRange);
    }

    public static CommandResult createSoundArea(Location location, CustomSound sound, double activationRange) {
        return createSoundArea(location, sound.sound, activationRange, sound.length);
    }

    @CommandHandler(
            parameterNames = {"location","sound","activation_range","cooldown"}
    )
    public static CommandResult createSoundArea(Location location, String sound, double activationRange, int cooldown) {

        // Info
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("sound", sound)
                .addPlaceholder("location", location)
                .addPlaceholder("activation_range", activationRange)
                .addPlaceholder("cooldown", cooldown)
                .build();

        // Check if a sound area already exists at this location
        if (soundAreas.get(location) != null) return CommandResult.FAILED.setDescription(LocaleManager.getInstance().getLocaleMessage("sound_area.already_exists", placeholders));

        // Create the sound area
        SoundArea area = new SoundArea(location, activationRange, cooldown, sound);
        soundAreas.put(location, area);

        // Create and insert the hyperpoint into the kd tree
        double[] coords = new double[] {location.getX(), location.getY(), location.getZ()};
        Hyperpoint point = new Hyperpoint(coords);
        soundAreaLocations.insert(point);

        return CommandResult.SUCCESS.setDescription(LocaleManager.getInstance().getLocaleMessage("sound_area.create", placeholders));
    }

    // ----- DELETE -----

    // TODO: Change identification method

    @CommandHandler(
            parameterNames = {"location"}
    )
    public static CommandResult delete(Location location) {
        SoundArea area = soundAreas.get(location);
        if (area == null) return CommandResult.FAILED.setDescription(LocaleManager.getInstance().getLocaleMessage("sound_area.does_not_exist"));

        DataManager.getInstance().getDao(ISoundAreaDao.class).removeSoundArea(area.uuid);
        soundAreas.remove(location);

        return CommandResult.SUCCESS.setDescription(LocaleManager.getInstance().getLocaleMessage("sound_area.delete"));
    }

    // ----- LIST -----

    @CommandHandler(
            parameterNames = {}
    )
    public static CommandResult list() {
        CommandResult result = CommandResult.LIST;

        result.addDescriptionListString(LocaleManager.getInstance().getLocaleMessage("list-header", StringPlaceholders.builder().addPlaceholder("header", "Sound Areas").build()));

        for (SoundArea area : soundAreas.values()) {

            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("sound", area.sound)
                    .addPlaceholder("location", area.location)
                    .addPlaceholder("activation_range", area.activationRange)
                    .addPlaceholder("cooldown", area.cooldown)
                    .build();

            result.addDescriptionListString(LocaleManager.getInstance().getLocaleMessage("sound_area.info.list", placeholders));
        }

        return result;
    }

    // ----- ACTIVATION -----

    // Check if a player has moved into a sound area
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Get the nearest sound area
        Vector pl = player.getLocation().toVector();
        double[] coords = new double[] {pl.getX(), pl.getY(), pl.getZ()};
        // TODO: Make different KDTrees for each dimension
        IMultiPoint point = soundAreaLocations.nearest(new Hyperpoint(coords));

        if (point == null) return;

        // Get the sound area
        Location soundAreaLocation = new Location(player.getWorld(), point.getCoordinate(1), point.getCoordinate(2), point.getCoordinate(3));
        SoundArea area = soundAreas.get(soundAreaLocation);
        if (area == null) return;
        area.attemptActivation(player);
    }

    // ----- CONSTRUCTORS -----

    public SoundAreaManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- RELOADING / DISABLING -----

    private void loadSoundAreaLocations() {
        for (SoundArea area : soundAreas.values()) {
            // Create and insert the hyperpoint into the kd tree
            double[] coords = new double[] {area.location.getX(), area.location.getY(), area.location.getZ()};
            Hyperpoint point = new Hyperpoint(coords);
            soundAreaLocations.insert(point);
        }
    }

    @Override
    public void reload() {
        soundAreas = DataManager.getInstance().getDao(ISoundAreaDao.class).getSoundAreas();
        loadSoundAreaLocations();

        CommandManager.getInstance().registerCommandModule("sound_area", this);
        Bukkit.getPluginManager().registerEvents(this, CobaltCore.getInstance());
    }

    @Override
    public void disable() {
        DataManager.getInstance().getDao(ISoundAreaDao.class).saveSoundAreas(soundAreas);
    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static SoundAreaManager INSTANCE = null;
    /**
     * Returns the object representing this <code>SoundAreaManager</code>.
     *
     * @return The object of this class
     */
    public static SoundAreaManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new SoundAreaManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
