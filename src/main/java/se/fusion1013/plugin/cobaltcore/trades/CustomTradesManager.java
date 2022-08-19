package se.fusion1013.plugin.cobaltcore.trades;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandExecutor;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandHandler;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandManager;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandResult;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.database.trades.ITradesDao;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.PreGeneratedWeightsRandom;
import se.fusion1013.plugin.cobaltcore.util.RandomCollection;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomTradesManager extends Manager implements CommandExecutor {

    // ----- VILLAGER -----

    private static Map<String, VillagerType> villagerTypes = new HashMap<>();

    public static VillagerType getVillagerType(String internalName) {
        return villagerTypes.get(internalName);
    }

    public static VillagerType registerVillagerType(VillagerType villagerType) {
        return villagerTypes.put(villagerType.getInternalName(), villagerType);
    }

    // ----- WANDERING TRADER -----

    private static RandomCollection<MerchantRecipePlaceholder> wanderingTrades = new RandomCollection<>();

    /**
     * Gets an array of all trade weights.
     *
     * @return an array of trade weights.
     */
    public static Double[] getWeights() {
        return wanderingTrades.getWeights();
    }

    /**
     * Gets an array of merchant recipes.
     *
     * @return an array of merchant recipes.
     */
    public static MerchantRecipePlaceholder[] getRecipes() {
        return wanderingTrades.getValues().toArray(new MerchantRecipePlaceholder[0]);
    }

    /**
     * Returns an array of all custom recipe names.
     *
     * @return an array of custom recipe names.
     */
    public static String[] getRecipeNames() {
        List<String> recipes = new ArrayList<>();
        for (MerchantRecipePlaceholder mr : wanderingTrades.getValues()) {
            recipes.add("\"" + mr.costItemName + "->" + mr.resultItemName + "\"");
        }
        return recipes.toArray(new String[0]);
    }

    // ----- LIST MERCHANT RECIPES -----

    @CommandHandler(
            parameterNames = {""},
            permission = "cobalt.core.command.trades.list"
    )
    public static CommandResult list() {
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("recipe_type", "merchant")
                .addPlaceholder("header", "Merchant Trades").build();

        MerchantRecipePlaceholder[] recipes = getRecipes();
        Double[] weights = getWeights();

        CommandResult commandResult = CommandResult.LIST;

        // If no trades were found, return failure message
        if (recipes.length <= 0) {
            commandResult = CommandResult.FAILED;
            commandResult.setDescription(LocaleManager.getInstance().getLocaleMessage("commands.trades.no_trades_found", placeholders));
        } else {
            commandResult.addDescriptionListString(LocaleManager.getInstance().getLocaleMessage("list-header", placeholders));
        }

        // List all trades
        for (int i = 0; i < recipes.length; i++) {
            CustomTradesManager.MerchantRecipePlaceholder recipe = recipes[i];
            double weight = weights[i];
            StringPlaceholders placeholders1 = StringPlaceholders.builder()
                    .addPlaceholder("cost_count", recipe.getCostAmount())
                    .addPlaceholder("cost_item", recipe.getCostItemName())
                    .addPlaceholder("result_count", recipe.getResultAmount())
                    .addPlaceholder("result_item", recipe.getResultItemName())
                    .addPlaceholder("weight", weight)
                    .build();
            commandResult.addDescriptionListString(LocaleManager.getInstance().getLocaleMessage("commands.trades.trade_info", placeholders1));
        }

        return commandResult;
    }

    // ----- REMOVE MERCHANT RECIPES -----

    @CommandHandler(
            parameterNames = {"trade"},
            commandSuggestionMethods = {"getRecipeNames"},
            permission = "commands.core.command.trades.remove",
            overrideTypes = {CommandHandler.ParameterType.TEXT}
    )
    public static CommandResult remove(String trade) {

        // Create information placeholder
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("recipe", trade)
                .build();

        // Remove recipe // TODO: Check if it was actually removed
        removeMerchantRecipe(trade);

        // Set command result
        CommandResult commandResult = CommandResult.SUCCESS;
        commandResult.setDescription(LocaleManager.getInstance().getLocaleMessage("commands.trades.remove_recipe", placeholders));
        return commandResult;
    }

    /**
     * Removes a merchant recipe.
     *
     * @param joinedName the joined name of the two items. "item1->item2"
     */
    public static void removeMerchantRecipe(String joinedName) {
        String[] split = joinedName.split("->");
        removeMerchantRecipe(split[0], split[1]);
    }

    /**
     * Removes a merchant recipe.
     *
     * @param costItem the name of the cost item.
     * @param resultItem the name of the result item.
     */
    public static void removeMerchantRecipe(String costItem, String resultItem) {
        DataManager.getInstance().getDao(ITradesDao.class).saveMerchantTrades(wanderingTrades.getValues(), wanderingTrades.getWeights());
        DataManager.getInstance().getDao(ITradesDao.class).removeMerchantTrade(costItem, resultItem);
        wanderingTrades = DataManager.getInstance().getDao(ITradesDao.class).getMerchantTrades(); // TODO: Replace with removal from the list instead. This is terrible
    }

    // ----- CREATE MERCHANT RECIPES -----

    @CommandHandler(
            parameterNames = {"cost", "costCount", "result", "resultCount", "maxUses", "weight"},
            commandSuggestionMethods = {"getItemNames", "", "getItemNames"},
            permission = "cobalt.core.command.trades.merchant",
            overrideTypes = {CommandHandler.ParameterType.TEXT, CommandHandler.ParameterType.NONE, CommandHandler.ParameterType.TEXT}
    )
    public static CommandResult create(String cost, int costCount, String result, int resultCount, int maxUses, int weight) {

        // Create a new recipe
        MerchantRecipePlaceholder placeholder = new MerchantRecipePlaceholder(cost, costCount, result, resultCount, maxUses);
        addMerchantRecipe(placeholder, weight);

        CommandResult commandResult = CommandResult.SUCCESS;

        // Set success message
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("recipe_type", "merchant")
                .addPlaceholder("cost_count", costCount)
                .addPlaceholder("cost_item", cost)
                .addPlaceholder("result_count", resultCount)
                .addPlaceholder("result_item", result)
                .addPlaceholder("uses", maxUses)
                .build();
        commandResult.setDescription(LocaleManager.getInstance().getLocaleMessage("commands.trades.add_recipe", placeholders));
        return commandResult;
    }

    public static String[] getItemNames() {
        return CustomItemManager.getItemNames();
    }

    /**
     * Adds a new <code>MerchantRecipePlaceholder</code> to the random distribution.
     *
     * @param recipe the recipe to add.
     * @param weight the weight of the recipe.
     */
    public static void addMerchantRecipe(MerchantRecipePlaceholder recipe, double weight) {
        wanderingTrades.addItem(weight, recipe);
    }

    /**
     * Gets a randomly chosen <code>MerchantRecipe</code> from the list, taking the weights into account.
     * @return a weighted random <code>MerchantRecipe</code>.
     */
    public static MerchantRecipe getRecipe() {
        MerchantRecipePlaceholder mrp = wanderingTrades.next();

        if (mrp == null) return null;

        ItemStack result = CustomItemManager.getItemStack(mrp.resultItemName);
        result.setAmount(mrp.resultAmount);

        ItemStack cost = CustomItemManager.getItemStack(mrp.costItemName);
        cost.setAmount(mrp.costAmount);

        MerchantRecipe mr = new MerchantRecipe(result, mrp.maxUses);
        mr.addIngredient(cost);

        return mr;
    }

    // ----- CONSTRUCTORS -----

    public CustomTradesManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        CommandManager.getInstance().registerCommandModule("trades", getInstance());
        wanderingTrades = DataManager.getInstance().getDao(ITradesDao.class).getMerchantTrades();
    }

    @Override
    public void disable() {
        DataManager.getInstance().getDao(ITradesDao.class).saveMerchantTrades(wanderingTrades.getValues(), wanderingTrades.getWeights());
    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static CustomTradesManager INSTANCE = null;
    /**
     * Returns the object representing this <code>CustomTradesManager</code>.
     *
     * @return The object of this class.
     */
    public static CustomTradesManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new CustomTradesManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }

    public static class MerchantRecipePlaceholder {

        String costItemName;
        int costAmount;

        String resultItemName;
        int resultAmount;

        int maxUses;

        public MerchantRecipePlaceholder(String costItem, int costAmount, String resultItem, int resultAmount, int maxUses) {
            this.costItemName = costItem;
            this.costAmount = costAmount;
            this.resultItemName = resultItem;
            this.resultAmount = resultAmount;
            this.maxUses = maxUses;
        }

        public String getCostItemName() {
            return costItemName;
        }

        public String getResultItemName() {
            return resultItemName;
        }

        public int getMaxUses() {
            return maxUses;
        }

        public int getCostAmount() {
            return costAmount;
        }

        public int getResultAmount() {
            return resultAmount;
        }
    }

}
