package se.fusion1013.cobaltCore.commands.entity;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.entity.CustomEntityManager;
import se.fusion1013.cobaltCore.entity.ICustomEntity;
import se.fusion1013.cobaltCore.entity.loader.EntityLoader;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltCore.util.FileUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;

public class EntityPastebinCommand {

    public static CommandAPICommand createItemPastebinCommand() {
        return new CommandAPICommand("pastebin")
                .withPermission(CommandUtil.getPermissionString(CobaltCore.getInstance(), "edit.entity.pastebin"))
                .withSubcommand(createEntityPastebinSaveCommand())
                .withSubcommand(createEntityPastebinTestCommand());
    }

    private static CommandAPICommand createEntityPastebinTestCommand() {
        return new CommandAPICommand("test")
                .withPermission(CommandUtil.getPermissionString(CobaltCore.getInstance(), "edit.entity.pastebin.test"))
                .withArguments(new StringArgument("id"))
                .executesPlayer((player, commandArguments) -> {
                    String pasteId = (String) commandArguments.args()[0];

                    try {
                        YamlConfiguration yaml = FileUtil.loadFromPastebin(pasteId);
                        ICustomEntity customEntity = EntityLoader.loadEntity(yaml);
                        CustomEntityManager.spawnEntity(player.getWorld(), player.getLocation(), customEntity);

                        StringPlaceholders placeholders = StringPlaceholders.builder()
                                .addPlaceholder("entity", customEntity.getInternalName())
                                .addPlaceholder("pastebin", pasteId)
                                .build();
                        LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.pastebin.entity.test.success", placeholders);

                    } catch (Exception e) {
                        StringPlaceholders placeholders = StringPlaceholders.builder()
                                .addPlaceholder("pastebin", pasteId)
                                .build();
                        LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.pastebin.entity.test.error", placeholders);
                    }
                });
    }

    private static CommandAPICommand createEntityPastebinSaveCommand() {
        return new CommandAPICommand("save")
                .withPermission(CommandUtil.getPermissionString(CobaltCore.getInstance(), "edit.entity.pastebin.save"))
                .withArguments(new StringArgument("id"))
                .executesPlayer((player, commandArguments) -> {
                    String pasteId = (String) commandArguments.get("id");

                    try {
                        YamlConfiguration yaml = FileUtil.loadFromPastebin(pasteId);
                        ICustomEntity item = EntityLoader.loadEntity(yaml);
                        FileUtil.saveYamlFile(CobaltCore.getInstance(), "entities", item.getInternalName(), yaml);
                        CustomEntityManager.reloadEntities();

                        StringPlaceholders placeholders = StringPlaceholders.builder()
                                .addPlaceholder("entity", item.getInternalName())
                                .addPlaceholder("pastebin", pasteId)
                                .build();
                        LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.pastebin.entity.test.success", placeholders);

                    } catch (Exception e) {
                        StringPlaceholders placeholders = StringPlaceholders.builder()
                                .addPlaceholder("pastebin", pasteId)
                                .build();
                        LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.pastebin.entity.test.error", placeholders);
                    }
                });
    }
}
