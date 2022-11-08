package se.fusion1013.plugin.cobaltcore.item.crafting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShapedRecipeWrapper implements IRecipeWrapper {

    // ----- VARIABLES -----

    private final NamespacedKey key;
    private final String output;
    private int count = 1;
    private String[] rows;
    private final Map<Character, String> ingredientNames = new HashMap<>();
    private final Map<Character, Material> ingredientMaterials = new HashMap<>();

    // ----- CONSTRUCTORS -----

    public ShapedRecipeWrapper(NamespacedKey key, String output) {
        this.key = key;
        this.output = output;
    }

    public ShapedRecipeWrapper(NamespacedKey key, String output, int count) {
        this.key = key;
        this.output = output;
        this.count = count;
    }

    // ----- GETTERS / SETTERS -----

    public void setRows(String[] rows) {
        this.rows = rows;
    }

    public void setIngredient(char key, String item) {
        ingredientNames.put(key, item);
    }

    public void setIngredient(char key, Material material) {
        ingredientMaterials.put(key, material);
    }

    @Override
    public String getRecipeType() {
        return "shaped_recipe";
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
        ShapedRecipe recipe = new ShapedRecipe(key, itemOutput);

        // Set rows
        recipe.shape(rows);

        // Check input
        for (char c : ingredientNames.keySet()) {
            ItemStack itemInput = CustomItemManager.getItemStack(ingredientNames.get(c));
            if (itemInput == null) return false;
            recipe.setIngredient(c, itemInput);
        }

        // Add material ingredients
        ingredientMaterials.forEach(recipe::setIngredient);

        Bukkit.removeRecipe(key); // Remove the recipe if it already exists
        Bukkit.addRecipe(recipe);

        return true;
    }

    // ----- LOADING -----

    @Override
    public List<IRecipeWrapper> loadFromFile(YamlConfiguration yaml, String itemName) {
        List<IRecipeWrapper> wrappers = new ArrayList<>();

        if (yaml.contains("shaped_crafting")) {
            List<Map<?, ?>> mapList = yaml.getMapList("shaped_crafting");

            for (Map<?, ?> map : mapList) {
                map.keySet().forEach(k -> {
                    Map<?, ?> values = (Map<?, ?>) map.get(k);
                    NamespacedKey key = new NamespacedKey(CobaltCore.getInstance(), itemName + "_" + k);
                    List<String> rows = (List<String>) values.get("rows");

                    int amount = 1;
                    if (values.containsKey("amount")) amount = (int) values.get("amount");
                    ShapedRecipeWrapper wrapper = new ShapedRecipeWrapper(key, itemName, amount);
                    wrapper.setRows(rows.toArray(new String[0]));

                    // Load ingredients as item stacks (With meta requirements)
                    if (values.containsKey("ingredients")) {
                        List<?> ingredients = (List<?>) values.get("ingredients");
                        ingredients.forEach(i -> {
                            Map<?, ?> ingredientMap = (Map<?, ?>) i;
                            ingredientMap.keySet().forEach(ingredientMapKey -> {
                                wrapper.setIngredient(((String) ingredientMap.get(ingredientMapKey)).charAt(0), (String) ingredientMapKey);
                            });
                        });
                    }

                    // Load ingredients as materials (Without meta requirements)
                    if (values.containsKey("material_ingredients")) {
                        List<?> ingredients = (List<?>) values.get("material_ingredients");
                        ingredients.forEach(i -> {
                            Map<?, ?> ingredientMap = (Map<?, ?>) i;
                            ingredientMap.keySet().forEach(ingredientMapKey -> {
                                wrapper.setIngredient(((String) ingredientMap.get(ingredientMapKey)).charAt(0), EnumUtils.findEnumInsensitiveCase(Material.class, (String) ingredientMapKey));
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
