package se.fusion1013.plugin.cobaltcore.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandExecutor;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandHandler;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandResult;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.stream.Stream;

/**
 * The command generator will automatically generate commands using the <code>CommandManager</code>
 */
public class CommandGenerator {

    // ----- REGISTER -----

    public static void register() {
        // Loop through all Modules
        for (String executorId : CommandManager.getInstance().getCommandModuleIdentifiers()) {
            CommandExecutor executor = CommandManager.getInstance().getCommandModule(executorId);
            Class<?> executorClass = executor.getClass();

            if (executorClass == null) continue;

            // Add subcommand for executor
            CommandAPICommand mainCommand = new CommandAPICommand(executorId);
            mainCommand.withPermission("cobalt.core.command." + executorId);

            // Loop through methods that integrate the InstanceHandler and create subcommands for them
            Class<?> klass = executorClass;
            while (klass != Object.class) {
                for (final Method method : klass.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(CommandHandler.class)) {
                        CommandHandler annotInstance = method.getAnnotation(CommandHandler.class);

                        CommandAPICommand mainSubCommand = new CommandAPICommand(method.getName());
                        mainSubCommand.withPermission(annotInstance.permission());

                        // Construct command arguments
                        constructCommandArguments(method, annotInstance, klass, mainSubCommand);

                        // Create command executor
                        mainSubCommand.executes(((sender, args) -> {
                            // Execute the method with the arguments
                            try {
                                CommandResult result = (CommandResult) method.invoke(executor, formatArguments(args, method.getParameters()));
                                if (sender instanceof Player player) handleResult(player, executorId, result);
                            } catch (InvocationTargetException | IllegalAccessException ex) {
                                ex.printStackTrace();
                            }
                        }));

                        // Create Subcommands
                        CommandAPICommand currentSub = mainCommand;

                        for (String sub : annotInstance.subCommands()) {
                            if (!sub.equalsIgnoreCase("")) {
                                CommandAPICommand newSub = new CommandAPICommand(sub);
                                currentSub.withSubcommand(newSub);
                                currentSub = newSub;
                            }
                        }

                        currentSub.withSubcommand(mainSubCommand);

                    }
                }

                klass = klass.getSuperclass();
            }
            mainCommand.register();
        }
    }

    private static void constructCommandArguments(Method method, CommandHandler annotInstance, Class<?> klass, CommandAPICommand command) {
        // Construct command arguments
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];
            // TODO: Switch over to custom command system
            // TODO: Split into different methods
            CommandHandler.ParameterType overrideType = CommandHandler.ParameterType.NONE;
            if (annotInstance.overrideTypes().length > i) overrideType = annotInstance.overrideTypes()[i];
            Argument argument = constructArgument(parameter.getType(), annotInstance.parameterNames()[i], overrideType); // TODO: Pass argument name
            if (argument != null) {

                // Attempt to retrieve command suggestions
                if (annotInstance.commandSuggestionMethods().length > i) {
                    String methodName = annotInstance.commandSuggestionMethods()[i];

                    // Attempt to call the suggestions method
                    try {
                        if (!methodName.equalsIgnoreCase("")) {
                            Method suggestionMethod = klass.getMethod(methodName);

                            // Replace suggestions
                            argument.replaceSuggestions(ArgumentSuggestions.strings(suggestionInfo -> {
                                try {
                                    return (String[]) suggestionMethod.invoke(null); // TODO: Pass object instance
                                } catch (IllegalAccessException | InvocationTargetException ex) {
                                    ex.printStackTrace();
                                }

                                return new String[0];
                            }));
                        }
                    } catch (NoSuchMethodException ex) {
                        ex.printStackTrace();
                    }
                }

                // Add argument to command
                command.withArguments(argument);
            }
        }
    }

    // ----- HELPER METHODS -----

    private static void handleResult(Player player, String moduleId, CommandResult result) { // TODO
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("module_type", moduleId)
                .addPlaceholder("description", result.getDescription())
                .build();

        switch (result) {
            case LIST -> {
                for (String s : result.getDescriptionList()) {
                    StringPlaceholders placeholders2 = StringPlaceholders.builder()
                            .addPlaceholder("description", s)
                            .build();
                    LocaleManager.getInstance().sendMessage("", player, "commands.generated.result", placeholders2);
                }
            }
            default -> LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.generated.result", placeholders);
        }
        // TODO: Add case for lists
    }

    private static Object[] formatArguments(Object[] args, Parameter[] parameters) {
        for (int i = 0; i < args.length; i++) {
            Class<?> type = parameters[i].getType();

            if (type.equals(BarColor.class)) args[i] = BarColor.valueOf(((String) args[i]).toUpperCase()); // TODO: Check if string is in enum
            if (type.equals(BarStyle.class)) args[i] = BarStyle.valueOf(((String) args[i]).toUpperCase());
        }

        return args;
    }

    private static Argument constructArgument(Class<?> type, String parameterName, CommandHandler.ParameterType override) {
        switch (override) {
            case TEXT: return new TextArgument(parameterName);
            case LOCATION_BLOCK: return new LocationArgument(parameterName, LocationType.BLOCK_POSITION);
        }

        if (type.equals(Location.class)) return new LocationArgument(parameterName);
        if (type.equals(String.class)) return new StringArgument(parameterName);
        if (type.equals(Entity.class)) return new EntitySelectorArgument(parameterName, EntitySelector.ONE_ENTITY);

        // Numerics
        if (type.equals(Double.class)) return new DoubleArgument(parameterName);
        if (type.equals(double.class)) return new DoubleArgument(parameterName);
        if (type.equals(Integer.class)) return new IntegerArgument(parameterName);
        if (type.equals(int.class)) return new IntegerArgument(parameterName);

        if (type.equals(BarColor.class)) return new StringArgument("color").replaceSuggestions(ArgumentSuggestions.strings(suggestionInfo -> Stream.of(BarColor.values()).map(BarColor::toString).map(String::toLowerCase).toArray(String[]::new)));
        if (type.equals(BarStyle.class)) return new StringArgument("style").replaceSuggestions(ArgumentSuggestions.strings(suggestionInfo -> Stream.of(BarStyle.values()).map(BarStyle::toString).map(String::toLowerCase).toArray(String[]::new)));

        if (type.equals(BossBar.Color.class)) return new StringArgument("color").replaceSuggestions(ArgumentSuggestions.strings(suggestionInfo -> Stream.of(BossBar.Color.values()).map(BossBar.Color::toString).map(String::toLowerCase).toArray(String[]::new)));
        if (type.equals(BossBar.Overlay.class)) return new StringArgument("overlay").replaceSuggestions(ArgumentSuggestions.strings(suggestionInfo -> Stream.of(BossBar.Overlay.values()).map(BossBar.Overlay::toString).map(String::toLowerCase).toArray(String[]::new)));

        return null;
    }
}
