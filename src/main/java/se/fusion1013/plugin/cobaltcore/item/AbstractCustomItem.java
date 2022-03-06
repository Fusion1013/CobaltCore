package se.fusion1013.plugin.cobaltcore.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
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
    }
}
