package se.fusion1013.plugin.cobaltcore.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobaltcore.CobaltCore;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCustomItem implements ICustomItem {

    // ----- VARIABLES -----

    // Internals
    String internalName;
    NamespacedKey namespacedKey;
    String[] tags;

    // Creation
    Material material;
    int count;

    // Visuals
    String customName;
    List<String> lore;
    int customModel;

    // Crafting Recipe
    List<Recipe> recipes = new ArrayList<>();

    // ----- CONSTRUCTORS -----

    public AbstractCustomItem(String internalName){
        this.internalName = internalName;
        this.namespacedKey = new NamespacedKey(CobaltCore.getInstance(), internalName);
    }

    // ----- LOGIC -----

    public boolean compareTo(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(namespacedKey, PersistentDataType.INTEGER);
    }

    public boolean hasTag(String tag) {
        for (String t : tags) if (t.equalsIgnoreCase(tag)) return true;
        return false;
    }

    // ----- GETTERS / SETTERS -----

    public void addShapedRecipe(String row1, String row2, String row3, ShapedIngredient... ingredients) {
        StringBuilder keyString = new StringBuilder(namespacedKey.getKey() + ".shapeless.");
        for (ShapedIngredient ingredient : ingredients) keyString.append(ingredient.item.getType().name());

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(CobaltCore.getInstance(), keyString.toString()), getItemStack());
        recipe.shape(row1, row2, row3);
        for (ShapedIngredient ingredient : ingredients) recipe.setIngredient(ingredient.key, ingredient.item);
        recipes.add(recipe);
        CobaltCore.getInstance().getServer().addRecipe(recipe);
    }

    public void addShapelessRecipe(ShapelessIngredient... ingredients) {
        StringBuilder keyString = new StringBuilder(namespacedKey.getKey() + ".shapeless.");
        for (ShapelessIngredient ingredient : ingredients) keyString.append(ingredient.item.getType().name());

        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(CobaltCore.getInstance(), keyString.toString()), getItemStack());
        for (ShapelessIngredient ingredient : ingredients) recipe.addIngredient(ingredient.count, ingredient.item);
        recipes.add(recipe);
        CobaltCore.getInstance().getServer().addRecipe(recipe);
    }

    public static class ShapelessIngredient {
        int count;
        ItemStack item;

        public ShapelessIngredient(int count, ItemStack item) {
            this.count = count;
            this.item = item;
        }

        public ShapelessIngredient(int count, Material material) {
            this.count = count;
            this.item = new ItemStack(material);
        }
    }

    public static class ShapedIngredient {

        char key;
        ItemStack item;

        public ShapedIngredient(char key, ItemStack item) {
            this.key = key;
            this.item = item;
        }

        public ShapedIngredient(char key, Material material) {
            this.key = key;
            this.item = new ItemStack(material);
        }
    }

    @Override
    public String getInternalName() {
        return internalName;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack is = new ItemStack(material, count);
        ItemMeta meta = is.getItemMeta();

        if (meta != null) {

            // Metadata
            meta.setDisplayName(customName);
            meta.setLore(lore);
            meta.setCustomModelData(customModel);

            // Persistent Data
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(namespacedKey, PersistentDataType.INTEGER, 1);

            is.setItemMeta(meta);
        }

        return is;
    }

    @Override
    public String[] getTags() {
        return tags;
    }

    // ----- BUILDER -----

    protected static abstract class AbstractCustomItemBuilder<T extends AbstractCustomItem, B extends AbstractCustomItemBuilder>{

        T obj;

        // Internals
        String internalName;
        List<String> tags;

        // Creation
        Material material;
        int count;

        // Visuals
        String customName;
        List<String> lore;
        int customModel;

        // Crafting Recipes
        List<CobaltRecipe> recipes = new ArrayList<>();

        public AbstractCustomItemBuilder(String internalName, Material material, int count){
            this.internalName = internalName;
            this.tags = new ArrayList<>();
            this.material = material;
            this.count = count;

            this.lore = new ArrayList<>();

            obj = createObj();
        }

        public T build(){
            obj.material = material;
            obj.count = count;

            obj.customName = customName;
            obj.lore = lore;
            obj.customModel = customModel;

            for (CobaltRecipe recipe : recipes) {
                if (recipe.shaped) obj.addShapedRecipe(recipe.row1, recipe.row2, recipe.row3, recipe.shapedIngredients);
                else obj.addShapelessRecipe(recipe.shapelessIngredients);
            }

            return obj;
        }

        protected abstract T createObj();
        protected abstract B getThis();

        // Internals

        public B addTag(String tag) {
            this.tags.add(tag);
            return getThis();
        }

        // Visuals

        public B setCustomModel(int customModel){
            this.customModel = customModel;
            return getThis();
        }

        public B addLoreLine(String loreLine){
            lore.add(loreLine);
            return getThis();
        }

        public B setLore(List<String> lore){
            this.lore = lore;
            return getThis();
        }

        public B setCustomName(String customName){
            this.customName = customName;
            return getThis();
        }

        // Crafting Recipes

        public B addShapedRecipe(String row1, String row2, String row3, ShapedIngredient... ingredients) {
            this.recipes.add(new CobaltRecipe(row1, row2, row3, ingredients));
            return getThis();
        }

        public B addShapelessRecipe(ShapelessIngredient... ingredients) {
            this.recipes.add(new CobaltRecipe(ingredients));
            return getThis();
        }

        private static class CobaltRecipe {
            String row1;
            String row2;
            String row3;
            ShapedIngredient[] shapedIngredients;

            ShapelessIngredient[] shapelessIngredients;

            boolean shaped;

            public CobaltRecipe(String row1, String row2, String row3, ShapedIngredient[] ingredients) {
                this.row1 = row1;
                this.row2 = row2;
                this.row3 = row3;
                this.shapedIngredients = ingredients;
                this.shaped = true;
            }

            public CobaltRecipe(ShapelessIngredient[] ingredients) {
                this.shapelessIngredients = ingredients;
                this.shaped = false;
            }
        }
    }
}
