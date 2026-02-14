package se.fusion1013.cobaltCore.item.toggles;

public enum ItemToggleType {

    Glowing("glowing"),
    Invulnerable("invulnerable"),
    Persistent("persistent"),
    DenyContainer("deny_container"),
    Heavy("heavy"),
    CgiveHide("cgive_hide");

    private final String key;

    ItemToggleType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
