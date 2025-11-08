package se.fusion1013.cobaltCore.commands.item;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.HexUtils;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltCore.util.StringUtil;

public class ItemPropertyCommand {

    public static CommandAPICommand createEditItemPropertyCommand() {
        return new CommandAPICommand("property")
                .withPermission("cobalt.core.commands.item.property")
                .withSubcommand(createEditItemModelCommand())
                .withSubcommand(createEditCustomNameCommand());
    }

    private static CommandAPICommand createEditCustomNameCommand() {
        return new CommandAPICommand("name")
                .withPermission("cobalt.core.commands.edit.item.name")
                .withArguments(new GreedyStringArgument("name"))
                .executesPlayer((player, args) -> {
                    String newName = (String) args.args()[0];
                    ItemStack stack = player.getInventory().getItemInMainHand();
                    ItemMeta meta = stack.getItemMeta();

                    StringPlaceholders placeholders = StringPlaceholders.builder()
                            .addPlaceholder("item", StringUtil.reformatString(stack.getType().toString()))
                            .addPlaceholder("name", newName)
                            .build();

                    if (meta == null) {
                        LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.edit.item.name.fail", placeholders);
                        return;
                    }

                    meta.displayName(Component.text(HexUtils.colorify(newName)));
                    stack.setItemMeta(meta);

                    LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.edit.item.name", placeholders);
                });
    }

    private static CommandAPICommand createEditItemModelCommand() {
        return new CommandAPICommand("model")
                .withPermission("cobalt.core.commands.edit.item.model")
                .withArguments(new GreedyStringArgument("model"))
                .executesPlayer((sender, args) -> {
                    editItemModel(sender, (String) args.args()[0]);
                });
    }

    private static void editItemModel(Player player, String model) {
        ItemStack stack = player.getInventory().getItemInMainHand();
        ItemMeta meta = stack.getItemMeta();

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("item", StringUtil.reformatString(stack.getType().toString()))
                .addPlaceholder("model", model)
                .build();
        if (meta == null) {
            LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.edit.item.item_model.fail", placeholders);
            return;
        }

        meta.setItemModel(NamespacedKey.fromString(model));
        stack.setItemMeta(meta);

        LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.edit.item.item_model", placeholders);
    }
}
