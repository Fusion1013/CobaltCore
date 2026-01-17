package se.fusion1013.cobaltCore.commands.edit;

import dev.jorel.commandapi.CommandAPICommand;
import se.fusion1013.cobaltCore.commands.item.CustomItemCommand;
import se.fusion1013.cobaltCore.commands.item.ItemPastebinCommand;
import se.fusion1013.cobaltCore.commands.item.ItemPropertyCommand;

/**
 * Registers the '/edit item' command.
 * Command Syntax:
 * <code>/edit item property</code>
 * <code>/edit item pastebin test</code>
 * <code>/edit item pastebin save</code>
 */
public class EditItemCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("item")
                .withPermission("cobalt.core.commands.edit.item")
                .withSubcommand(CustomItemCommand.createCustomItemInfoCommand())
                .withSubcommand(ItemPastebinCommand.createItemPastebinCommand())
                .withSubcommand(ItemPropertyCommand.createEditItemPropertyCommand());
    }


}
