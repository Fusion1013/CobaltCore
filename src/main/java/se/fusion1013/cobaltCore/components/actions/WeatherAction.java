package se.fusion1013.cobaltCore.components.actions;

import org.bukkit.Location;
import org.bukkit.World;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltCore.variable.IntVariable;

import java.util.List;
import java.util.Map;

public class WeatherAction extends AbstractAction {

    private final IntVariable clearDuration = new IntVariable("clear", -1);
    private final IntVariable rainDuration = new IntVariable("rain", -1);
    private final IntVariable thunderDuration = new IntVariable("thunder", -1);

    @Override
    protected List<AbstractVariable<?, ?, ?, ?>> getVariables() {
        return List.of(clearDuration, rainDuration, thunderDuration);
    }

    @Override
    public void execute(Map<String, Object> context) {
        Location location = getTargetLocation(context);
        if (location == null) return;

        World world = location.getWorld();

        Integer rain = rainDuration.getValue();
        if (rain > 0) {
            world.setStorm(true);
            world.setWeatherDuration(rain);
        } else if (rain == 0) {
            world.setStorm(false);
            world.setWeatherDuration(0);
        }

        Integer thunder = thunderDuration.getValue();
        if (thunder > 0) {
            world.setWeatherDuration(Math.max(thunder, rain));
            world.setStorm(true);
            world.setThunderDuration(thunder);
            world.setThundering(true);
        } else if (thunder == 0) {
            world.setThunderDuration(0);
            world.setThundering(false);
        }

        Integer clear = clearDuration.getValue();
        if (clear > 0) {
            world.setStorm(false);
            world.setClearWeatherDuration(clear);
        }
    }

    @Override
    public String getId() {
        return "weather";
    }
}
