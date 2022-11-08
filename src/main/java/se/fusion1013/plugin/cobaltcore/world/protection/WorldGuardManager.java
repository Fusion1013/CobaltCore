package se.fusion1013.plugin.cobaltcore.world.protection;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.gamemode.GameMode;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;

import java.util.Set;

public class WorldGuardManager extends Manager {

    // ----- CONSTRUCTOR -----

    public WorldGuardManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    // ----- FLAGS -----

    private static Flag<?> initFlag(Flag<?> flag){
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            registry.register(flag);
            return flag;
        } catch (FlagConflictException e){
            return registry.get(flag.getName());
        }
    }

    // ----- FLAG CHECKING ----- // TODO: Create Enums for all default flags

    public boolean isBlockBreakAllowed(Location location) {
        Set<ProtectedRegion> regions = getRegionSet(location).getRegions();
        for (ProtectedRegion region : regions) {
            StateFlag.State blockBreakAllowed = region.getFlag(Flags.BLOCK_BREAK);
            if (blockBreakAllowed == null) continue;
            if (blockBreakAllowed.equals(StateFlag.State.DENY)) return false;

            GameMode gameMode = region.getFlag(Flags.GAME_MODE);
            if (gameMode == null) continue;
            if (gameMode.getName().equalsIgnoreCase("adventure")) return false;
        }
        return true;
    }

    // ----- GETTERS / SETTERS -----

    private static ApplicableRegionSet getRegionSet(Location location) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        return query.getApplicableRegions(BukkitAdapter.adapt(location));
    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static WorldGuardManager INSTANCE = null;

    /**
     * Returns the object representing this <code>WorldGuardManager</code>.
     *
     * @return The object of this class.
     */
    public static WorldGuardManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new WorldGuardManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }

}
