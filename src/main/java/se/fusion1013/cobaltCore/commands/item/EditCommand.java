package se.fusion1013.cobaltCore.commands.item;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.ItemMeta;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;

public class EditCommand {

    public static void register() {
        new CommandAPICommand("edit")
                .withPermission("cobalt.core.commands.edit")
                .withSubcommand(createEditItemCommand())
                .register();
    }

    private static CommandAPICommand createEditItemCommand() {
        return new CommandAPICommand("item")
                .withPermission("cobalt.core.commands.edit.item")
                .withSubcommand(createEditItemModelCommand());
    }

    private static CommandAPICommand createEditItemModelCommand() {
        return new CommandAPICommand("item_model")
                .withPermission("cobalt.core.commands.edit.item.item_model")
                .withArguments(new StringArgument("model"))
                .executesPlayer((sender, args) -> {
                    editItemModel(sender, (String) args.args()[0]);
                });
    }

    private static void editItemModel(Player player, String model) {
        ItemStack stack = player.getInventory().getItemInMainHand();
        ItemMeta meta = stack.getItemMeta();

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("item", stack.displayName())
                .addPlaceholder("model", model)
                .build();
        if (meta == null) {
            LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.edit.item.item_model.fail", placeholders);
            return;
        }

        meta.setItemModel();
    }

}
