package se.fusion1013.cobaltCore.commands;

import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AcceptCommand {

    private static final Map<UUID, IAcceptDelegate> ACCEPT_DELEGATE_MAP = new HashMap<>();

    public static void setPendingAcceptRequest(Player player, IAcceptDelegate delegate) {
        ACCEPT_DELEGATE_MAP.put(player.getUniqueId(), delegate);
    }

    public static void register() {
        new CommandAPICommand("accept")
                .withPermission("cobalt.core.commands.accept")
                .executes((commandSender, commandArguments) -> {
                    if (commandSender instanceof Player player) {
                        IAcceptDelegate delegate = ACCEPT_DELEGATE_MAP.get(player.getUniqueId());

                        if (delegate == null) {
                            LocaleManager.getInstance().sendMessage(player, "commands.accept.fail");
                        } else {
                            delegate.onAccept(commandSender);
                            setPendingAcceptRequest(player, null);
                        }
                    }
                })
                .register();

        new CommandAPICommand("deny")
                .withPermission("cobalt.core.commands.deny")
                .executes((commandSender, commandArguments) -> {
                    if (commandSender instanceof Player player) {
                        LocaleManager.getInstance().sendMessage(player, "commands.deny");
                        setPendingAcceptRequest(player, null);
                    }
                }).register();
    }

}
