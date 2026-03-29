package se.fusion1013.cobaltCore.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.commands.system.CommandManager;
import se.fusion1013.cobaltCore.commands.system.ItemInfo;
import se.fusion1013.cobaltCore.commands.system.PastebinInfo;
import se.fusion1013.cobaltCore.commands.system.ReloadInfo;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltCore.util.FileUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;

import java.util.Optional;
import java.util.function.Supplier;

public class CobaltCommand {

    private static final LocaleManager LOCALE = LocaleManager.getInstance();

    public static void register() {
        new CommandAPICommand("cobalt")
                .withSubcommand(createReloadCommand())
                .withSubcommand(createPastebinCommand())
                .withSubcommand(createListCommand())
                .withSubcommand(createInfoCommand())
                .register();
    }

    // ##%%##%%## INFO COMMAND ##%%##%%## //

    private static CommandAPICommand createInfoCommand() {
        return new CommandAPICommand("info")
                .withPermission(CommandUtil.getPermissionString(CobaltCore.getInstance(), "info"))
                .withArguments(new StringArgument("option").replaceSuggestions(ArgumentSuggestions.strings(k -> CommandManager.getItemInfoOptions())))
                .withArguments(new StringArgument("item").replaceSuggestions(ArgumentSuggestions.strings(k -> {
                    String option = (String) k.previousArgs().get("option");
                    ItemInfo info = CommandManager.getItemInfo(option);
                    return info.listItems().get();
                })))
                .executesPlayer(CobaltCommand::itemInfo);
    }

    private static void itemInfo(Player player, CommandArguments args) {
        String option = (String) args.get("option");
        String item = (String) args.get("item");
        ItemInfo info = CommandManager.getItemInfo(option);
        String[] itemInfo = info.itemInfo().apply(item);
        LOCALE.sendMessage(CobaltCore.getInstance(), player, "commands.core.cobalt.info.header", StringPlaceholders.builder()
                .addPlaceholder("item", item)
                .addPlaceholder("type", option).build());
        for (String s : itemInfo) {
            LOCALE.sendMessage("", player, "commands.core.cobalt.info.item", StringPlaceholders.builder()
                    .addPlaceholder("item", s).build());
        }
    }

    // ##%%##%%## LIST COMMAND ##%%##%%## //

    private static CommandAPICommand createListCommand() {
        return new CommandAPICommand("list")
                .withPermission(CommandUtil.getPermissionString(CobaltCore.getInstance(), "list"))
                .withArguments(new StringArgument("option").replaceSuggestions(ArgumentSuggestions.strings(k -> CommandManager.getItemInfoOptions())))
                .executesPlayer(CobaltCommand::listItems);
    }

    private static void listItems(Player player, CommandArguments args) {
        String option = (String) args.get("option");
        ItemInfo info = CommandManager.getItemInfo(option);
        LOCALE.sendMessage(CobaltCore.getInstance(), player, "commands.core.cobalt.list.header", StringPlaceholders.builder()
                .addPlaceholder("item", option).build());
        for (String s : info.listItems().get()) {
            LOCALE.sendMessage("", player, "commands.core.cobalt.list.item", StringPlaceholders.builder()
                    .addPlaceholder("item", s).build());
        }
    }

    // ##%%##%%## PASTEBIN COMMAND ##%%##%%## //

    private static CommandAPICommand createPastebinCommand() {
        return new CommandAPICommand("pastebin")
                .withPermission(CommandUtil.getPermissionString(CobaltCore.getInstance(), "pastebin"))
                .withArguments(new StringArgument("type").replaceSuggestions(ArgumentSuggestions.strings(c -> CommandManager.getPastebinOptions())))
                .withArguments(new MultiLiteralArgument("action", "test", "save"))
                .withArguments(new StringArgument("pastebin"))
                .executesPlayer(CobaltCommand::executePastebinCommand);
    }

    private static void executePastebinCommand(Player sender, CommandArguments args) {
        String type = (String) args.get("type");
        String action = (String) args.get("action");
        String pasteId = (String) args.get("pastebin");

        PastebinInfo pastebinInfo = CommandManager.getPastebinInfo(type);
        if (pastebinInfo == null) return;

        try {
            YamlConfiguration yaml = FileUtil.loadFromPastebin(pasteId);

            if (action.equalsIgnoreCase("save"))
                FileUtil.saveYamlFile(pastebinInfo.plugin(), pastebinInfo.name(), yaml.getString("internal_name"), yaml);

            pastebinInfo.yamlConsumer().accept(sender, yaml);
            pastebinInfo.postLoad().run();
        } catch (Exception e) {
            if (sender instanceof Player player) {
                StringPlaceholders placeholders = StringPlaceholders.builder()
                        .addPlaceholder("pastebin", pasteId)
                        .build();
                LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.pastebin.test.error", placeholders);

                player.sendMessage(e.getMessage());
            }

            e.printStackTrace();
        }
    }

    // ##%%##%%## RELOAD COMMAND ##%%##%%## //

    private static CommandAPICommand createReloadCommand() {
        return new CommandAPICommand("reload")
                .withPermission("commands.core.reload")
                .withArguments(new StringArgument("reload").replaceSuggestions(ArgumentSuggestions.strings(c -> CommandManager.getReloadOptions())))
                .withOptionalArguments(new GreedyStringArgument("options"))
                .executes((sender, args) -> {
                    String reloadOption = (String) args.get("reload");
                    ReloadInfo reloadInfo = CommandManager.getReload(reloadOption);
                    reload(sender, args, reloadInfo.getInternalName(), reloadInfo.reload(), reloadInfo.verboseResult());
                });
    }

    private static void reload(CommandSender sender, CommandArguments args, String type, Runnable run, Supplier<String[]> verboseResult) {
        try {
            Optional<Object> options = args.getOptional("options");
            String commandOptions = options.map(o -> (String) o).orElse("");

            boolean verbose = commandOptions.contains("-v");

            run.run();

            if (sender instanceof Player player) {
                LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.reload",
                        StringPlaceholders.builder()
                                .addPlaceholder("type", type)
                                .addPlaceholder("count", verboseResult.get().length)
                                .build()
                );
                if (verbose) {
                    for (String s : verboseResult.get()) {
                        LocaleManager.getInstance().sendMessage("", player, "commands.core.reload.item", StringPlaceholders.builder().addPlaceholder("name", s).build());
                    }
                }
            }
        } catch (Exception ex) {
            CobaltCore.getInstance().getLogger().warning("Encountered issue while reloading " + type + ": " + ex.getMessage());

            if (sender instanceof Player player) {
                StringPlaceholders placeholders = StringPlaceholders.builder()
                        .addPlaceholder("action", "Reloading " + type)
                        .addPlaceholder("stacktrace", ex.getMessage())
                        .build();
                LocaleManager.getInstance().sendMessage(player, "commands.error", placeholders);
            }
        }
    }

}
