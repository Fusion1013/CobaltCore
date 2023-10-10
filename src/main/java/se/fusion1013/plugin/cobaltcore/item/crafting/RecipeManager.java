package se.fusion1013.plugin.cobaltcore.item.crafting;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeManager extends Manager {

    private static final Map<String, Map<String, ICobaltRecipe>> CUSTOM_RECIPES = new HashMap<>();

    private static final List<IRecipeWrapper> WRAPPERS_TO_PROCESS = new ArrayList<>();
    private static final List<IRecipeWrapper> REGISTERED_WRAPPERS = new ArrayList<>();

    public RecipeManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    public static void addRecipe(IRecipeWrapper wrapper) {
        WRAPPERS_TO_PROCESS.add(wrapper);
    }

    //region REGISTER

    private static final IRecipeWrapper SHAPED_WRAPPER = registerWrapper(new ShapedRecipeWrapper(null, null));
    private static final IRecipeWrapper SHAPELESS_WRAPPER = registerWrapper(new ShapelessRecipeWrapper(null, null));

    public void registerRecipes() {
        int previousSize;
        do {
            previousSize = WRAPPERS_TO_PROCESS.size();
            for (int i = WRAPPERS_TO_PROCESS.size() - 1; i >= 0; i--) {
                IRecipeWrapper wrapper = WRAPPERS_TO_PROCESS.get(i);
                boolean registered = wrapper.register();
                if (registered) WRAPPERS_TO_PROCESS.remove(i);
            }
        } while (WRAPPERS_TO_PROCESS.size() > 0 && WRAPPERS_TO_PROCESS.size() < previousSize);

        if (WRAPPERS_TO_PROCESS.size() > 0) {
            CobaltCore.getInstance().getLogger().warning("Failed to load " + WRAPPERS_TO_PROCESS.size() + " recipes:");
            for (IRecipeWrapper wrapper : WRAPPERS_TO_PROCESS) CobaltCore.getInstance().getLogger().info(wrapper.getRecipeType() + ": " + wrapper.getItemName());
        }
    }

    public static IRecipeWrapper registerWrapper(IRecipeWrapper wrapper) {
        REGISTERED_WRAPPERS.add(wrapper);
        return wrapper;
    }

    public static ICobaltRecipe registerCobaltRecipe(ICobaltRecipe recipe) {
        return CUSTOM_RECIPES.computeIfAbsent(recipe.getRecipeType(), k -> new HashMap<>()).put(recipe.getInternalName(), recipe);
    }

    //endregion

    //region RECIPE_LOADING

    /**
     * Attempts to load all recipes from a <code>File</code>.
     *
     * @param file the <code>File</code> to load the recipe from.
     * @param itemName the name of the result item.
     */
    public static void loadRecipesFromFile(File file, String itemName) {
        YamlConfiguration yaml = new YamlConfiguration();
        try {
            yaml.load(file);
        } catch (IOException | InvalidConfigurationException ex) {
            ex.printStackTrace();
        }

        loadRecipes(yaml, itemName);
    }

    public static void loadRecipes(YamlConfiguration yaml, String itemName) {
        for (IRecipeWrapper wrapper : REGISTERED_WRAPPERS) {
            WRAPPERS_TO_PROCESS.addAll(wrapper.loadFromFile(yaml, itemName));
        }
    }

    private static void loadShapedRecipe(YamlConfiguration yaml, String itemName) {
        if (yaml.contains("shaped_crafting")) {
            List<Map<?, ?>> mapList = yaml.getMapList("shaped_crafting");

            for (Map<?, ?> map : mapList) {
                map.keySet().forEach(k -> {
                    Map<?, ?> values = (Map<?, ?>) map.get(k);
                    NamespacedKey key = new NamespacedKey(CobaltCore.getInstance(), itemName + "_" + k);
                    List<String> rows = (List<String>) values.get("rows");

                    ShapedRecipeWrapper wrapper = new ShapedRecipeWrapper(key, itemName);
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

                    addRecipe(wrapper);
                });
            }
        }
    }

    private static void loadShapelessRecipe(YamlConfiguration yaml, String itemName) {
        if (yaml.contains("shapeless_crafting")) {
            List<Map<?, ?>> mapList = yaml.getMapList("shapeless_crafting");

            for (Map<?, ?> map : mapList) {
                map.keySet().forEach(k -> {
                    Map<?, ?> values = (Map<?, ?>) map.get(k);
                    NamespacedKey key = new NamespacedKey(CobaltCore.getInstance(), itemName + "_" + k);

                    ShapelessRecipeWrapper wrapper = new ShapelessRecipeWrapper(key, itemName);

                    // Load ingredients as item stacks (With meta requirements)
                    if (values.containsKey("ingredients")) {
                        List<?> ingredients = (List<?>) values.get("ingredients");
                        ingredients.forEach(i -> {
                            Map<?, ?> ingredientMap = (Map<?, ?>) i;
                            ingredientMap.keySet().forEach(ingredientMapKey -> {
                                wrapper.addIngredient((String) ingredientMapKey);
                            });
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

                    addRecipe(wrapper);
                });
            }
        }
    }

    //endregion

    //region GETTERS/SETTERS

    public static Map<String, ICobaltRecipe> getRecipesOfType(String type) {
        return CUSTOM_RECIPES.get(type);
    }

    //endregion

    @Override
    public void reload() {
    }

    @Override
    public void disable() {

    }
}
