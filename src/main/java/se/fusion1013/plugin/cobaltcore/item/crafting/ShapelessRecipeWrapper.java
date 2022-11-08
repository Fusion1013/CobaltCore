package se.fusion1013.plugin.cobaltcore.item.crafting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShapelessRecipeWrapper implements IRecipeWrapper {

    // ----- VARIABLES -----

    private final NamespacedKey key;
    private final String output;
    private int count = 1;
    private final List<String> ingredientNames = new ArrayList<>();
    private final List<Material> ingredientMaterials = new ArrayList<>();

    // ----- CONSTRUCTORS -----

    public ShapelessRecipeWrapper(NamespacedKey key, String output) {
        this.key = key;
        this.output = output;
    }

    public ShapelessRecipeWrapper(NamespacedKey key, String output, int count) {
        this.key = key;
        this.output = output;
        this.count = count;
    }

    // ----- GETTERS / SETTERS -----

    public void addIngredient(String item) {
        ingredientNames.add(item);
    }

    public void addIngredient(Material material) {
        ingredientMaterials.add(material);
    }

    @Override
    public String getRecipeType() {
        return "shapeless";
    }

    @Override
    public String getItemName() {
        return output;
    }

    // ----- REGISTER -----

    @Override
    public boolean register() {
        // Check output
        ItemStack itemOutput = CustomItemManager.getCustomItemStack(output);
        if (itemOutput == null) return false;

        itemOutput.setAmount(count);

        // If output exist, attempt to load the recipe
        ShapelessRecipe recipe = new ShapelessRecipe(key, itemOutput);

        // Add string ingredients
        for (String s : ingredientNames) {
            ItemStack itemInput = CustomItemManager.getItemStack(s);
            if (itemInput == null) return false;
            recipe.addIngredient(itemInput);
        }

        // Add material ingredients
        ingredientMaterials.forEach(recipe::addIngredient);

        // Register item
        Bukkit.removeRecipe(key);
        Bukkit.addRecipe(recipe);

        return true;
    }

    // ----- LOADING -----

    @Override
    public List<IRecipeWrapper> loadFromFile(YamlConfiguration yaml, String itemName) {
        List<IRecipeWrapper> wrappers = new ArrayList<>();

        if (yaml.contains("shapeless_crafting")) {
            List<Map<?, ?>> mapList = yaml.getMapList("shapeless_crafting");

            for (Map<?, ?> map : mapList) {
                map.keySet().forEach(k -> {
                    Map<?, ?> values = (Map<?, ?>) map.get(k);
                    NamespacedKey key = new NamespacedKey(CobaltCore.getInstance(), itemName + "_" + k);

                    int amount = 1;
                    if (values.containsKey("amount")) amount = (int) values.get("amount");
                    ShapelessRecipeWrapper wrapper = new ShapelessRecipeWrapper(key, itemName, amount);

                    // Load ingredients as item stacks (With meta requirements)
                    if (values.containsKey("ingredients")) {
                        List<?> ingredients = (List<?>) values.get("ingredients");
                        ingredients.forEach(i -> {
                            wrapper.addIngredient((String) i);
                        });
                    }

                    // Load ingredients as materials (Without meta requirements)
                    if (values.containsKey("material_ingredients")) {
                        List<?> ingredients = (List<?>) values.get("material_ingredients");
                        ingredients.forEach(i -> {
                            Map<?, ?> ingredientMap = (Map<?, ?>) i;
                            ingredientMap.keySet().forEach(ingredientMapKey -> {
                                wrapper.addIngredient((String) ingredientMapKey);
                            });
                        });
                    }

                    wrappers.add(wrapper);
                });
            }
        }

        return wrappers;
    }
}
