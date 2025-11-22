package se.fusion1013.cobaltCore.commands;

import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.item.ICustomItem;

public class IsCustomItemCommand {

    public static void register() {
        new CommandAPICommand("is_custom")
                .executesPlayer((sender, args) -> {
                    ItemStack item = sender.getInventory().getItemInMainHand();
                    ICustomItem customItem = CustomItemManager.getCustomItem(item);
                    if (customItem == null) {
                        sender.sendMessage("Not a custom item");
                    } else {
                        sender.sendMessage("It is a custom item: " + customItem.getInternalName());
                    }
                }).register();
    }

}
