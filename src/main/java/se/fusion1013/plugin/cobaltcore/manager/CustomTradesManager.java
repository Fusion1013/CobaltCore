package se.fusion1013.plugin.cobaltcore.manager;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.SQLite;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.util.PreCalculateWeightsRandom;

import java.util.ArrayList;
import java.util.List;

public class CustomTradesManager extends Manager {

    // ----- WANDERING TRADER -----

    private static PreCalculateWeightsRandom<MerchantRecipePlaceholder> wanderingTrades = new PreCalculateWeightsRandom<>();

    /**
     * Gets an array of all trade weights.
     *
     * @return an array of trade weights.
     */
    public static Integer[] getWeights() {
        return wanderingTrades.getTrueWeights().toArray(new Integer[0]);
    }

    /**
     * Gets an array of merchant recipes.
     *
     * @return an array of merchant recipes.
     */
    public static MerchantRecipePlaceholder[] getRecipes() {
        return wanderingTrades.getItems().toArray(new MerchantRecipePlaceholder[0]);
    }

    /**
     * Returns an array of all custom recipe names.
     *
     * @return an array of custom recipe names.
     */
    public static String[] getRecipeNames() {
        List<String> recipes = new ArrayList<>();
        for (MerchantRecipePlaceholder mr : wanderingTrades.getItems()) {
            recipes.add("\"" + mr.costItemName + "->" + mr.resultItemName + "\"");
        }
        return recipes.toArray(new String[0]);
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
        SQLite.saveMerchantTrades(wanderingTrades.getItems(), wanderingTrades.getTrueWeights());
        SQLite.removeMerchantTrade(costItem, resultItem);
        wanderingTrades = SQLite.getMerchantTrades(); // TODO: Replace with removal from the list instead. This is terrible
    }

    /**
     * Adds a new <code>MerchantRecipePlaceholder</code> to the random distribution.
     *
     * @param recipe the recipe to add.
     * @param weight the weight of the recipe.
     */
    public static void addMerchantRecipe(MerchantRecipePlaceholder recipe, int weight) {
        wanderingTrades.addItem(recipe, weight);
    }

    /**
     * Gets a randomly chosen <code>MerchantRecipe</code> from the list, taking the weights into account.
     * @return a weighted random <code>MerchantRecipe</code>.
     */
    public static MerchantRecipe getRecipe() {
        MerchantRecipePlaceholder mrp = wanderingTrades.chooseOne();

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
        wanderingTrades = SQLite.getMerchantTrades();
    } // TODO: Do this after all plugins have loaded

    @Override
    public void disable() {
        SQLite.saveMerchantTrades(wanderingTrades.getItems(), wanderingTrades.getTrueWeights());
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
