package se.fusion1013.cobaltCore.commands.cgive;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.item.ICustomItem;
import se.fusion1013.cobaltCore.item.loaders.ItemLoader;
import se.fusion1013.cobaltCore.item.section.ItemSection;
import se.fusion1013.cobaltCore.util.FileUtil;
import se.fusion1013.cobaltCore.util.ItemUtil;

import java.util.ArrayList;
import java.util.List;

public class CGiveCommand {
    public static void createCgiveCommand() {
        new CommandAPICommand("cgive")
                .withPermission("commands.core.item")
                .withSubcommand(createPastebinCommand())
                .withArguments(new StringArgument("item").replaceSuggestions(ArgumentSuggestions.strings(info -> CustomItemManager.getCustomItemNames())))
                .executesPlayer(CGiveCommand::giveItem)
                .register();
    }

    // ----- PASTEBIN -----

    private static CommandAPICommand createPastebinCommand() {
        return new CommandAPICommand("pastebin")
                .withPermission("commands.core.item.pastebin")
                .withArguments(new StringArgument("id"))
                .executesPlayer((player, commandArguments) -> {
                    String pasteId = (String) commandArguments.args()[0];

                    try {
                        YamlConfiguration yaml = FileUtil.loadFromPastebin(pasteId);
                        ICustomItem item = ItemLoader.Load(yaml);
                        ItemStack itemStack = item.getItemStack();
                        player.getInventory().addItem(itemStack);
                    } catch (Exception e) {
                        player.sendMessage("Error: " + e.getMessage());
                    }
                });
    }

    // ----- CATEGORIES -----

    public static void addCategoryCommands(CommandAPICommand parent) {
        ItemSection[] categories = CustomItemManager.getCustomItemCategories();

        // For each category; Create commands for giving single / all items in the category
        for (ItemSection category : categories) {
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

    private static void giveAllInCategory(Player player, ItemSection category) {
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
