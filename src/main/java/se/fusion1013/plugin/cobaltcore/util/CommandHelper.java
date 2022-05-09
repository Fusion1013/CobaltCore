package se.fusion1013.plugin.cobaltcore.util;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.TextArgument;

public class CommandHelper {

    public Argument getArgumentOfType(Class<?> type, String name) {
        if (Double.class.equals(type)) return new DoubleArgument(name);

        return new TextArgument(name);
    }

}
