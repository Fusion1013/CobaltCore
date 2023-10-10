package se.fusion1013.plugin.cobaltcore.action.encounter;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.util.INameProvider;

import java.util.*;

public class Encounter implements IEncounter, INameProvider {

    //region FIELDS

    // -- Generic Information
    private final String internalName;

    // -- Timekeeping
    private double duration = 0; // Measured in milliseconds
    private final Map<String, Double> timers = new HashMap<>();
    private final Map<String, Long> times = new HashMap<>();

    // -- Extra Data
    private final Map<String, Map<String, Object>> extraData = new HashMap<>();

    // -- Encounter Events (Actions)
    private final List<EncounterEvent> events = new ArrayList<>();

    //endregion

    //region CONSTRUCTORS

    public Encounter(YamlConfiguration yaml) {
        this(yaml.getString("internal_name"), yaml);
    }

    public Encounter(String internalName, YamlConfiguration yaml) {

        this.internalName = internalName;

        loadEvents(yaml);

        // Determine duration of encounter
        for (EncounterEvent event : events) if (event.getEndTime() > duration) duration = event.getEndTime();
    }

    public Encounter(JsonObject json) {
        this(json.get("internal_name").getAsString(), json);
    }

    public Encounter(String internalName, JsonObject json) {
        this.internalName = internalName;
    }

    private void loadEvents(YamlConfiguration yaml) {
        if (!yaml.contains("events")) return;

        boolean timeInSeconds = false;
        if (yaml.contains("time_in_seconds")) timeInSeconds = yaml.getBoolean("time_in_seconds");

        List<Map<?, ?>> encounterEvents = yaml.getMapList("events");
        for (Map<?, ?> eventData : encounterEvents) {

            for (Object key : eventData.keySet()) {
                Map<?, ?> internalData = (Map<?, ?>) eventData.get(key);
                this.events.add(
                        new EncounterEvent((String) key, internalData, timeInSeconds)
                );
            }
        }
    }

    //endregion

    //region TRIGGER

    @Override
    public BukkitTask trigger(Location location, String id) {
        // Prepare the encounter
        timers.put(id, 0D);
        times.put(id, System.currentTimeMillis());
        for (EncounterEvent event : events) event.executed = false;

        CobaltCore.getInstance().getLogger().info("Executing encounter '" + internalName + "'");

        return Bukkit.getScheduler().runTaskTimer(CobaltCore.getInstance(), () -> {

            double timer = timers.get(id);
            long time = times.get(id);

            // Increment Timer
            timer += System.currentTimeMillis() - time;
            time = System.currentTimeMillis();

            // Execute events
            for (EncounterEvent event : events) {
                List<Map<String, Object>> newDataList = event.attemptExecute(location, timer);
                if (newDataList == null) continue;

                Map<String, Object> currentData = extraData.computeIfAbsent(id, k -> new HashMap<>());
                for (Map<String, Object> newData : newDataList) {
                    currentData.putAll(newData);
                }
            }

            if (timer >= duration) {
                extraData.remove(id);
                EncounterManager.cancelEncounter(id);
            }

            timers.put(id, timer);
            times.put(id, time);

        }, 0, 1);
    }

    //endregion

    //region BUILDER

    public Encounter addEvent(EncounterEvent event) {
        events.add(event);
        if (duration < event.getEndTime()) duration = event.getEndTime();
        return this;
    }

    //endregion

    //region GETTERS/SETTERS

    public List<EncounterEvent> getEvents() {
        return events;
    }

    @Override
    public String getInternalName() {
        return internalName;
    }

    public int getEventCount() {
        return events.size();
    }

    //endregion
}
