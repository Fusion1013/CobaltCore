package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LocationArgument;
import org.bukkit.Location;
import se.fusion1013.cobaltCore.variable.provider.LocationValueProvider;

public class LocationVariable extends AbstractVariable<Location, LocationValueProvider, LocationArgument, LocationVariable> {

    public LocationVariable(String name) {
        super(name, null);
    }

    public LocationVariable(String name, Location defaultValue) {
        super(name, new LocationValueProvider(name, defaultValue));
    }

    @Override
    public LocationVariable getSelf() {
        return this;
    }

    @Override
    public LocationArgument getArgument() {
        LocationArgument argument = new LocationArgument(getName());
        argument.setOptional(optionalArgument);
        if (argumentSuggestions != null)
            argument.replaceSuggestions(ArgumentSuggestions.strings(argumentSuggestions.get()));
        return argument;
    }
}
