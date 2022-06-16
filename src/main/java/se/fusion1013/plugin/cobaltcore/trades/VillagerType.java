package se.fusion1013.plugin.cobaltcore.trades;

import org.bukkit.inventory.MerchantRecipe;

public class VillagerType {

    // ----- VARIABLES -----

    String internalName;
    GenerationEvent generationEvent;

    MerchantRecipe[] trades;

    // ----- CONSTRUCTORS -----

    public VillagerType(String internalName, GenerationEvent generationEvent, MerchantRecipe... trades) {
        this.internalName = internalName;
        this.generationEvent = generationEvent;
        this.trades = trades;
    }

    // ----- GETTERS / SETTERS -----

    public String getInternalName() {
        return internalName;
    }

    public GenerationEvent getGenerationEvent() {
        return generationEvent;
    }

    public MerchantRecipe[] getTrades() {
        return trades;
    }

    // ----- GENERATION EVENT ENUM -----

    public enum GenerationEvent {
        CUSTOM
    }
}
