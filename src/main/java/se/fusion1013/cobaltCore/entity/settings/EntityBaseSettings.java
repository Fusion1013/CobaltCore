package se.fusion1013.cobaltCore.entity.settings;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;
import se.fusion1013.cobaltCore.util.HexUtils;

public class EntityBaseSettings implements IEntitySettings {

    protected String displayName;
    protected boolean noGravity;
    protected boolean silent;
    protected boolean invulnerable;
    protected boolean glowing;

    @Override
    public void load(Entity entity) {
        entity.setCustomName(HexUtils.colorify(displayName));
        entity.setGravity(!noGravity);
        entity.setSilent(silent);
        entity.setInvisible(invulnerable);
        entity.setGlowing(glowing);
    }

    public static class Builder {
        private final EntityBaseSettings obj;

        public Builder() {
            obj = new EntityBaseSettings();
        }

        public EntityBaseSettings build() {
            return obj;
        }

        public Builder addDisplayName(String displayName) {
            obj.displayName = displayName;
            return this;
        }

        public Builder addNoGravity(@Nullable boolean noGravity) {
            obj.noGravity = noGravity;
            return this;
        }

        public Builder addSilent(@Nullable boolean silent) {
            obj.silent = silent;
            return this;
        }

        public Builder addInvulnerable(@Nullable boolean invulnerable) {
            obj.invulnerable = invulnerable;
            return this;
        }

        public Builder addGlowing(@Nullable boolean glowing) {
            obj.glowing = glowing;
            return this;
        }
    }
}
