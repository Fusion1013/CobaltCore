package se.fusion1013.cobaltCore.commands.edit;

import dev.jorel.commandapi.CommandAPICommand;

public class EditCommand {

    public static void register() {
        new CommandAPICommand("edit")
                .withPermission("cobalt.core.commands.edit")
                .withSubcommand(EditItemCommand.register())
                .register();
    }

}
