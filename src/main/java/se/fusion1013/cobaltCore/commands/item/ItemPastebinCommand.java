package se.fusion1013.cobaltCore.commands.item;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.item.ICustomItem;
import se.fusion1013.cobaltCore.item.loaders.ItemLoader;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.FileUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;

public class ItemPastebinCommand {

    public static CommandAPICommand createItemPastebinCommand() {
        return new CommandAPICommand("pastebin")
                .withPermission("cobalt.core.commands.item.pastebin")
                .withSubcommand(createItemPastebinSaveCommand())
                .withSubcommand(createItemPastebinTestCommand());
    }

    private static CommandAPICommand createItemPastebinTestCommand() {
        return new CommandAPICommand("test")
                .withPermission("cobalt.core.commands.item.pastebin.test")
                .withArguments(new StringArgument("id"))
                .executesPlayer((player, commandArguments) -> {
                    String pasteId = (String) commandArguments.args()[0];

                    try {
                        YamlConfiguration yaml = FileUtil.loadFromPastebin(pasteId);
                        ICustomItem item = ItemLoader.Load(yaml);
                        ItemStack itemStack = item.getItemStack();
                        player.getInventory().addItem(itemStack);

                        StringPlaceholders placeholders = StringPlaceholders.builder()
                                .addPlaceholder("item", item.getInternalName())
                                .addPlaceholder("pastebin", pasteId)
                                .build();
                        LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.pastebin.test.success", placeholders);

                    } catch (Exception e) {
                        StringPlaceholders placeholders = StringPlaceholders.builder()
                                .addPlaceholder("pastebin", pasteId)
                                .build();
                        LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.pastebin.test.error", placeholders);
                    }
                });
    }

    private static CommandAPICommand createItemPastebinSaveCommand() {
        return new CommandAPICommand("save")
                .withPermission("cobalt.core.commands.item.pastebin.save")
                .withArguments(new StringArgument("id"))
                .executesPlayer((player, commandArguments) -> {
                    String pasteId = (String) commandArguments.get("id");

                    try {
                        YamlConfiguration yaml = FileUtil.loadFromPastebin(pasteId);
                        ICustomItem item = ItemLoader.Load(yaml);
                        FileUtil.saveYamlFile(CobaltCore.getInstance(), "items", item.getInternalName(), yaml);
                        CustomItemManager.reloadItems();

                        StringPlaceholders placeholders = StringPlaceholders.builder()
                                .addPlaceholder("item", item.getInternalName())
                                .addPlaceholder("pastebin", pasteId)
                                .build();
                        LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.pastebin.test.success", placeholders);

                    } catch (Exception e) {
                        StringPlaceholders placeholders = StringPlaceholders.builder()
                                .addPlaceholder("pastebin", pasteId)
                                .build();
                        LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.pastebin.test.error", placeholders);
                    }
                });
    }

}
