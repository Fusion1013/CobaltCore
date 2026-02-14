package se.fusion1013.cobaltCore.commands.system;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.executors.CommandArguments;

public interface ICommandValue<T extends Argument> {

    void setValueGetter(CommandArguments arguments);

    T getArgument();

    String getName();

}
