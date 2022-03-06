package se.fusion1013.plugin.cobaltcore.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.item.CustomItem;
import se.fusion1013.plugin.cobaltcore.manager.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.manager.CustomTradesManager;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

public class TradeCommand {

    // ----- REGISTER -----

    public static void register() {
        new CommandAPICommand("trades")
                .withPermission("cobalt.core.command.trades")
                .withSubcommand(new CommandAPICommand("merchant")
                        .withSubcommand(createMerchantTradesCommand())
                        .withSubcommand(createRemoveMerchantTradeCommand())
                        .withSubcommand(createListMerchantTradesCommand()))
                .withSubcommand(new CommandAPICommand("villager")
                        .withSubcommand(createVillagerTradesCommand()))
                .register();
    }

    // ----- LIST MERCHANT TRADES COMMAND -----

    private static CommandAPICommand createListMerchantTradesCommand() {
        return new CommandAPICommand("list")
                .withPermission("cobalt.core.command.trades.list")
                .executesPlayer(((sender, args) -> {
                    StringPlaceholders placeholders = StringPlaceholders.builder()
                            .addPlaceholder("recipe_type", "merchant")
                            .addPlaceholder("header", "Merchant Trades").build();

                    MerchantRecipe[] recipes = CustomTradesManager.getRecipes();
                    Integer[] weights = CustomTradesManager.getWeights();

                    // If no trades were found, do not print the list header and instead give a failure message
                    if (recipes.length <= 0) {
                        LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), sender, "commands.trades.no_trades_found", placeholders);
                        return;
                    } else {
                        LocaleManager.getInstance().sendMessage("", sender, "list-header", placeholders);
                    }

                    // List all trades
                    for (int i = 0; i < recipes.length; i++) {
                        MerchantRecipe recipe = recipes[i];
                        int weight = weights[i];
                        StringPlaceholders placeholders1 = StringPlaceholders.builder()
                                .addPlaceholder("cost_count", recipe.getIngredients().get(0).getAmount())
                                .addPlaceholder("cost_item", CustomItemManager.getItemName(recipe.getIngredients().get(0)))
                                .addPlaceholder("result_count", recipe.getResult().getAmount())
                                .addPlaceholder("result_item", CustomItemManager.getItemName(recipe.getResult()))
                                .addPlaceholder("weight", weight)
                                .build();
                        LocaleManager.getInstance().sendMessage("", sender, "commands.trades.trade_info", placeholders1);
                    }
                }));
    }

    // ----- REMOVE MERCHANT TRADE COMMAND -----

    private static CommandAPICommand createRemoveMerchantTradeCommand() {
        return new CommandAPICommand("remove")
                .withPermission("cobalt.core.command.trades.remove")
                .withArguments(new TextArgument("trade").replaceSuggestions(info -> CustomTradesManager.getRecipeNames()))
                .executesPlayer(((sender, args) -> {
                    String tradeName = (String) args[0];
                    CustomTradesManager.removeMerchantRecipe(tradeName);

                    StringPlaceholders placeholders = StringPlaceholders.builder()
                            .addPlaceholder("recipe", tradeName)
                            .build();
                    LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), sender, "commands.trades.remove_recipe", placeholders);
                }));
    }

    // ----- MERCHANT TRADES COMMAND -----

    private static CommandAPICommand createMerchantTradesCommand() {
        return new CommandAPICommand("create")
                .withPermission("cobalt.core.command.trades.merchant")
                .withArguments(new TextArgument("cost").replaceSuggestions(info -> CustomItemManager.getItemNames()))
                .withArguments(new IntegerArgument("cost_count", 1))
                .withArguments(new TextArgument("result").replaceSuggestions(info -> CustomItemManager.getItemNames()))
                .withArguments(new IntegerArgument("result_count", 1))
                .withArguments(new IntegerArgument("max_uses", 1))
                .withArguments(new IntegerArgument("weight", 1))
                .executesPlayer(((sender, args) -> {
                    // Get cost
                    String costItemName = (String) args[0];
                    ItemStack cost = CustomItemManager.getItemStack(costItemName);
                    int costCount = (Integer) args[1];
                    cost.setAmount(costCount);

                    // Get result
                    String resultItemName = (String) args[2];
                    ItemStack result = CustomItemManager.getItemStack(resultItemName);
                    int resultCount = (Integer) args[3];
                    result.setAmount(resultCount);

                    // Get number of times trade can be used
                    int maxUses = (Integer) args[4];

                    // Create recipe
                    MerchantRecipe recipe = new MerchantRecipe(result, maxUses);
                    recipe.addIngredient(cost);

                    // Add recipe to manager
                    CustomTradesManager.addMerchantRecipe(recipe, (Integer) args[5]);

                    // Send success message
                    StringPlaceholders placeholders = StringPlaceholders.builder()
                            .addPlaceholder("recipe_type", "merchant")
                            .addPlaceholder("cost_count", costCount)
                            .addPlaceholder("cost_item", costItemName)
                            .addPlaceholder("result_count", resultCount)
                            .addPlaceholder("result_item", resultItemName)
                            .addPlaceholder("uses", maxUses)
                            .build();
                    LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), sender, "commands.trades.add_recipe", placeholders);
                }));
    }

    // ----- VILLAGER TRADES COMMAND -----

    private static CommandAPICommand createVillagerTradesCommand() {
        return new CommandAPICommand("create");
    }

}
