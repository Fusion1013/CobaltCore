package se.fusion1013.cobaltCore.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.components.ComponentManager;
import se.fusion1013.cobaltCore.components.IComponent;
import se.fusion1013.cobaltCore.util.CommandUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ComponentCommand {

    public static void register() {
        new CommandAPICommand("component")
                .withPermission(CommandUtil.getPermissionString(CobaltCore.getInstance(), "component"))
                .withSubcommand(createListCommand())
                .withSubcommand(createTriggerCommand())
                .register();
    }

    private static CommandAPICommand createTriggerCommand() {
        return new CommandAPICommand("trigger")
                .withPermission(CommandUtil.getPermissionString(CobaltCore.getInstance(), "component.trigger"))
                .withArguments(new StringArgument("id").replaceSuggestions(ArgumentSuggestions.strings((k) -> ComponentManager.getComponentNames())))
                .executesPlayer(ComponentCommand::triggerComponent);
    }

    private static void triggerComponent(Player player, CommandArguments args) {
        String id = (String) args.get("id");
        IComponent component = ComponentManager.getComponent(id);

        Map<String, Object> context = new HashMap<>();
        context.put("default_entity", player);
        context.put("default_location", player.getLocation());

        component.execute(context);
    }

    private static CommandAPICommand createListCommand() {
        return new CommandAPICommand("list")
                .withPermission(CommandUtil.getPermissionString(CobaltCore.getInstance(), "component.list"))
                .executesPlayer(ComponentCommand::listComponents);
    }

    private static void listComponents(Player player, CommandArguments commandArguments) {
        Collection<IComponent> components = ComponentManager.getComponents();
        for (IComponent component : components) {
            player.sendMessage(component.getInternalName());
        }
    }

}
