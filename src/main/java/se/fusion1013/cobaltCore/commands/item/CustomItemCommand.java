package se.fusion1013.cobaltCore.commands.item;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.item.ICustomItem;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;

import java.util.List;

public class CustomItemCommand {

    public static CommandAPICommand createCustomItemInfoCommand() {
        return new CommandAPICommand("getinfo")
                .withPermission(CommandUtil.getPermissionString(CobaltCore.getInstance(), "edit.item.getinfo"))
                .executesPlayer(CustomItemCommand::displayCustomItemInfo);
    }

    private static void displayCustomItemInfo(Player player, CommandArguments commandArguments) {
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        if (mainHandItem.getType() == Material.AIR) {
            LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.edit.item.getinfo.no_held_item");
            return;
        }

        ICustomItem customItem = CustomItemManager.getCustomItem(mainHandItem);
        if (customItem == null) {
            LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.edit.item.getinfo.no_custom_item");
            return;
        }

        List<String> info = customItem.getLocalizedInfo();
        info.forEach(player::sendMessage);
    }
}
