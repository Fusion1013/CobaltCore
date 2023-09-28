package se.fusion1013.plugin.cobaltcore.commands.cgive;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.item.ICustomItem;
import se.fusion1013.plugin.cobaltcore.item.category.IItemCategory;
import se.fusion1013.plugin.cobaltcore.util.ItemUtil;

import java.util.ArrayList;
import java.util.List;

public class CGiveCommand {

    public static CommandAPICommand createCgiveCommand() {
        CommandAPICommand cgiveCommand = new CommandAPICommand("cgive")
                .withPermission("commands.core.item");
        addCategoryCommands(cgiveCommand);
        cgiveCommand.register();

        CommandAPICommand giveCommand = new CommandAPICommand("give")
                .withPermission("commands.core.item");
        addCategoryCommands(giveCommand);

        return giveCommand;
    }

    // ----- CATEGORIES -----

    public static void addCategoryCommands(CommandAPICommand parent) {
        IItemCategory[] categories = CustomItemManager.getCustomItemCategories();

        // For each category; Create commands for giving single / all items in the category
        for (IItemCategory category : categories) {
            parent.withSubcommand(
                    new CommandAPICommand(category.getInternalName())
                            .withSubcommand(
                                    new CommandAPICommand("all")
                                            .executesPlayer(((sender, args) -> {
                                                giveAllInCategory(sender, category);
                                            }))
                            )
                            .withSubcommand(
                                    new CommandAPICommand("item")
                                            .withArguments(new StringArgument("item_name").replaceSuggestions(ArgumentSuggestions.strings(info -> CustomItemManager.getItemNamesInCategory(category))))
                                            .executesPlayer(CGiveCommand::giveItem)
                            )
            );

            String[] items = CustomItemManager.getItemNamesInCategory(category);
            for (String item : items) {
                ICustomItem customItem = CustomItemManager.getCustomItem(item);
                if (customItem == null) continue;
                // TODO: Subcategories
            }
        }
    }

    // ----- GIVE METHODS -----

    private static void giveAllInCategory(Player player, IItemCategory category) {
        String[] items = CustomItemManager.getItemNamesInCategory(category);
        List<ItemStack> itemStacks = new ArrayList<>();
        for (String s : items) {
            ItemStack item = CustomItemManager.getCustomItemStack(s);
            if (item != null) itemStacks.add(item);
        }
        ItemUtil.giveShulkerBox(player, itemStacks.toArray(new ItemStack[0]), category.getBoxMaterial(), category.getFormattedName());
    }

    /**
     * Gives a specific item to the player.
     *
     * @param player the player to give the item to.
     * @param args the item to give the player.
     */
    private static void giveItem(Player player, CommandArguments args){
        String itemName = (String)args.args()[0];
        ItemStack is = CustomItemManager.getCustomItemStack(itemName);
        if (is != null) player.getInventory().addItem(is);
    }

}
