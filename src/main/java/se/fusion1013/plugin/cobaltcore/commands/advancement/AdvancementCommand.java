package se.fusion1013.plugin.cobaltcore.commands.advancement;

import dev.jorel.commandapi.CommandAPICommand;
import eu.endercentral.crazy_advancements.advancement.Advancement;
import eu.endercentral.crazy_advancements.advancement.progress.GenericResult;
import eu.endercentral.crazy_advancements.manager.AdvancementManager;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.advancement.CobaltAdvancementManager;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

import java.util.Map;

public class AdvancementCommand {

    public static void register() {

        new CommandAPICommand("cadvancement")
                .withPermission("cobalt.core.commands.advancement")
                .withSubcommand(createGrantCommand())
                .withSubcommand(createRevokeCommand())
                .register();
    }

    //region REVOKE

    private static CommandAPICommand createRevokeCommand() {
        CommandAPICommand command = new CommandAPICommand("revoke")
                .withPermission("cobalt.core.commands.advancement.revoke")
                .withSubcommand(createRevokeAllCommand());

        // Add subcommands for each advancement manager
        Map<String, AdvancementManager> managers = CobaltAdvancementManager.ADVANCEMENT_MANAGERS;
        for (String m : managers.keySet()) {
            command.withSubcommand(createManagerCommand(m, managers.get(m), false));
        }

        return command;
    }

    //region REVOKE ALL

    private static CommandAPICommand createRevokeAllCommand() {
        return new CommandAPICommand("all")
                .withPermission("cobalt.core.commands.advancement.revoke_all")
                .executesPlayer(AdvancementCommand::revokeAllAdvancement);
    }

    private static void revokeAllAdvancement(Player player, Object[] args) {
        for (AdvancementManager manager : CobaltAdvancementManager.ADVANCEMENT_MANAGERS.values()) {
            for (Advancement advancement : manager.getAdvancements()) {
                manager.revokeAdvancement(player, advancement);
            }
        }

        // Create locale placeholders
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("player", player.getName())
                .build();
        LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.advancement.revoke_all.success", placeholders);
    }

    //endregion

    private static void revokeAdvancement(Player player, AdvancementManager manager, Advancement advancement) {
        // Create locale placeholders
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("advancement", advancement.getName().getKey())
                .addPlaceholder("player", player.getName())
                .build();
        GenericResult result = manager.revokeAdvancement(player.getUniqueId(), advancement);

        if (result == GenericResult.CHANGED) LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.advancement.revoke.success", placeholders);
        else if (result == GenericResult.UNCHANGED) LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.advancement.revoke.failed", placeholders);
    }

    //endregion

    //region GRANT

    private static CommandAPICommand createGrantCommand() {
        CommandAPICommand command = new CommandAPICommand("grant")
                .withPermission("cobalt.core.commands.advancement.grant")
                .withSubcommand(createGrantAllCommand());

        // Add subcommands for each advancement manager
        Map<String, AdvancementManager> managers = CobaltAdvancementManager.ADVANCEMENT_MANAGERS;
        for (String m : managers.keySet()) {
            command.withSubcommand(createManagerCommand(m, managers.get(m), true));
        }

        return command;
    }

    private static void grantAdvancement(Player player, AdvancementManager manager, Advancement advancement) {
        // Create locale placeholders
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("advancement", advancement.getName().getKey())
                .addPlaceholder("player", player.getName())
                .build();
        GenericResult result = manager.grantAdvancement(player.getUniqueId(), advancement);

        if (result == GenericResult.CHANGED) LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.advancement.grant.success", placeholders);
        else if (result == GenericResult.UNCHANGED) LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.advancement.grant.failed", placeholders);
    }

    //region GRANT ALL

    private static CommandAPICommand createGrantAllCommand() {
        return new CommandAPICommand("all")
                .withPermission("cobalt.core.commands.advancement.grant_all")
                .executesPlayer(AdvancementCommand::grantAllAdvancement);
    }

    private static void grantAllAdvancement(Player player, Object[] args) {
        for (AdvancementManager manager : CobaltAdvancementManager.ADVANCEMENT_MANAGERS.values()) {
            for (Advancement advancement : manager.getAdvancements()) {
                manager.grantAdvancement(player, advancement);
            }
        }

        // Create locale placeholders
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("player", player.getName())
                .build();
        LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.advancement.grant_all.success", placeholders);
    }

    //endregion

    //endregion

    private static CommandAPICommand createManagerCommand(String key, AdvancementManager manager, boolean grant) {
        CommandAPICommand command = new CommandAPICommand(key);

        for (Advancement a : manager.getAdvancements()) {
            command.withSubcommand(
                    new CommandAPICommand(a.getName().getKey())
                            .executesPlayer((sender, args) -> {
                                if (grant) grantAdvancement(sender, manager, a);
                                else revokeAdvancement(sender, manager, a);
                            })
            );
        }

        return command;
    }
}
