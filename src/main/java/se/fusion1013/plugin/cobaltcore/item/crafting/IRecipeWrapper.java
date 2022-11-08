package se.fusion1013.plugin.cobaltcore.item.crafting;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public interface IRecipeWrapper {

    /**
     * Attempts to register the recipe.
     * @return whether the recipe was registered or not.
     */
    boolean register();

    String getRecipeType();
    String getItemName();

    List<IRecipeWrapper> loadFromFile(YamlConfiguration yaml, String itemName);

}
