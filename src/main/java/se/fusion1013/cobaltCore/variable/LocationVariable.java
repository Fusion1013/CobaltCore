package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LocationArgument;
import org.bukkit.Location;

public class LocationVariable extends AbstractVariable<Location, LocationArgument, LocationVariable> {

    public LocationVariable(String name) {
        super(name, null);
    }

    public LocationVariable(String name, Location defaultValue) {
        super(name, defaultValue);
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
