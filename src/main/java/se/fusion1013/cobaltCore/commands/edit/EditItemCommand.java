package se.fusion1013.cobaltCore.commands.edit;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.commands.item.ItemPastebinCommand;
import se.fusion1013.cobaltCore.commands.item.ItemPropertyCommand;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.HexUtils;
import se.fusion1013.cobaltCore.util.StringPlaceholders;

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
                .withSubcommand(ItemPastebinCommand.createItemPastebinCommand())
                .withSubcommand(ItemPropertyCommand.createEditItemPropertyCommand());
    }



}
