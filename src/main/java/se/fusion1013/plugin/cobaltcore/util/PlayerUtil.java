package se.fusion1013.plugin.cobaltcore.util;

import dev.jorel.commandapi.SuggestionInfo;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.kitteh.vanish.VanishPlugin;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.CobaltPlugin;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;

import java.time.Duration;
import java.util.*;

/**
 * Contains various methods for working with players.
 */
public class PlayerUtil {

    // ----- PLAYER NAME MATCHING -----

    public static boolean isMatch(Player player, String str, int minimum) {
        if (str.length() < minimum) return false;
        return player.getName().toLowerCase().contains(str.toLowerCase());
    }

    // ----- COMMAND ARGUMENTS -----

    public static String[] getPlayerArguments(SuggestionInfo info) {
        List<String> names = new ArrayList<>();
        if (info.sender() instanceof Player player) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("cobalt.core.vanish") || !isVanished(p)) names.add(p.getName());
            }
        }
        return names.toArray(new String[0]);
    }

    // ----- JOIN / QUIT MESSAGES -----

    /**
     * Sends the join message to all players. // TODO: That have that option enabled
     *
     * @param plugin the plugin that is sending the message.
     * @param player the player to create a join message for.
     */
    public static void sendJoinMessage(CobaltPlugin plugin, Player player) {
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("player", player.getName())
                .build();

        LocaleManager.getInstance().broadcastMessage(plugin, "connection.join", placeholders);
        Bukkit.getConsoleSender().sendMessage(LocaleManager.getInstance().getLocaleMessage("connection.join", placeholders));
    }

    /**
     * Sends the quit message to all players. // TODO: That have that option enabled
     *
     * @param plugin the plugin that is sending the message.
     * @param player the player to create a quit message for.
     */
    public static void sendQuitMessage(CobaltPlugin plugin, Player player) {
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("player", player.getName())
                .build();

        LocaleManager.getInstance().broadcastMessage(plugin, "connection.quit", placeholders);
        Bukkit.getConsoleSender().sendMessage(LocaleManager.getInstance().getLocaleMessage("connection.quit", placeholders));
    }

    // ----- VANISH -----

    /**
     * Gets the number of players that are currently not vanished.
     *
     * @return the number of players that are currently not vanished.
     */
    public static int getUnvanishedPlayerCount() {
        return Bukkit.getOnlinePlayers().size() - getVanishPlayerCount();
    }

    /**
     * Gets the number of players that are currently vanished.
     *
     * @return the number of vanished players.
     */
    public static int getVanishPlayerCount() {
        int count = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isVanished(p)) count++;
        }
        return count;
    }

    /**
     * Sets the vanished state of a player.
     *
     * @param player the player to set the vanished state of.
     * @param vanish true to vanish the player.
     * @param isSilent true to make it unannounced and without effects.
     */
    public static void setVanished(Player player, boolean vanish, boolean isSilent) {
        if (!isVanishInstalled()) return;

        VanishPlugin vnp = (VanishPlugin) Bukkit.getPluginManager().getPlugin("VanishNoPacket");
        boolean oldVanishedState = isVanished(player);

        if (vanish) {
            vnp.getManager().vanish(player, isSilent, !isSilent);

            // Eject spectating players to keep up the illusion
            ejectSpectatingPlayers(player, isSilent);
        } else {
            vnp.getManager().reveal(player, isSilent, !isSilent);
        }
    }

    /**
     * Checks if the given player is vanished.
     *
     * @param player the player to check.
     * @return true if the player is vanished.
     */
    public static boolean isVanished(Player player) {
        if (!isVanishInstalled()) return false; // If VanishNoPacket is not installed, do not try to use it

        VanishPlugin vnp = (VanishPlugin) Bukkit.getPluginManager().getPlugin("VanishNoPacket");
        return vnp.getManager().isVanished(player);
    }

    /**
     * Checks if VanishNoPacket is installed.
     *
     * @return true if it is installed;
     */
    public static boolean isVanishInstalled() {
        Plugin vanishPlugin = Bukkit.getPluginManager().getPlugin("VanishNoPacket");
        return vanishPlugin != null;
    }

    // ----- SPECTATOR THINGS -----

    public static int ejectSpectatingPlayers(Player spectatedPlayer, boolean isSilent) {
        int counter = 0;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getSpectatorTarget() == null) continue;
            if (!p.getSpectatorTarget().equals(spectatedPlayer)) continue;
            // TODO: if (!canSee(spectatedPlayer, p.getUniqueId())) continue;

            // Don't eject the player if they have override perms
            if (p.hasPermission("cobalt.setting.allow-spectators.override")) continue;

            // Eject the spectating player
            p.setSpectatorTarget(null);

            // Give feedback depending on whether the operation is silent
            if (!isSilent) {
                StringPlaceholders placeholders = StringPlaceholders.builder()
                        .addPlaceholder("player", spectatedPlayer.getName())
                        .build();
                LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), p, "events.spectate.info.ejected", placeholders);
            }

            // Increment the counter
            counter++;
        }

        return counter;
    }

    // ----- PLAYER FINDER METHODS -----

    /**
     * Gets the closest player to the <code>Location</code>.
     *
     * @param location the location to get the closest player to.
     * @return the closest player, or null if no player was found.
     */
    public static Player getClosestPlayer(Location location) {
        Player current = null;
        double currentDistance = Double.MAX_VALUE;
        for (Player p : Bukkit.getOnlinePlayers()) {
            double dist = location.distanceSquared(p.getLocation());
            if (dist < currentDistance) {
                current = p;
                currentDistance = dist;
            }
        }
        return current;
    }

    /**
     * Gets the closest player to the <code>Location</code>.
     *
     * @param location the location to get the closest player to.
     * @param gameMode the <code>GameMode</code> of the player
     * @return the closest player, or null if no player was found.
     */
    public static Player getClosestPlayer(Location location, GameMode gameMode) {
        Player current = null;
        double currentDistance = Double.MAX_VALUE;
        for (Player p : Bukkit.getOnlinePlayers()) {

            if (!p.getGameMode().equals(gameMode)) continue;

            double dist = location.distanceSquared(p.getLocation());
            if (dist < currentDistance) {
                current = p;
                currentDistance = dist;
            }
        }
        return current;
    }

    /**
     * Gets an array of strings containing the names of currently online players.
     *
     * @return an array of names.
     */
    public static String[] getOnlinePlayerNames() {
        List<Player> playerList = new ArrayList<>(Bukkit.getOnlinePlayers());
        String[] playerNames = new String[playerList.size()];

        for (int i = 0; i < playerNames.length; i++) {
            playerNames[i] = playerList.get(i).getName();
        }

        return playerNames;
    }

    /**
     * Gets an array of nearby players.
     *
     * @param location the origin location.
     * @return an array of players.
     */
    public static Player[] getNearbyPlayers(Location location) {
        return getNearbyPlayers(location, 0, -1);
    }

    /**
     * Gets an array of nearby players.
     *
     * @param location the origin location.
     * @param maxDistance the maximum distance from the origin that the player can be to be returned. Set to -1 to ignore maximum distance.
     * @return an array of players.
     */
    public static Player[] getNearbyPlayers(Location location, double maxDistance) {
        return getNearbyPlayers(location, 0, maxDistance);
    }

    /**
     * Gets an array of nearby players.
     *
     * @param location the origin location.
     * @param minDistance the minimum distane from the origin that the player can be to be returned. Set to 0 to ignore minimum distance.
     * @param maxDistance the maximum distance from the origin that the player can be to be returned. Set to -1 to ignore maximum distance.
     * @return an array of players.
     */
    public static Player[] getNearbyPlayers(Location location, double minDistance, double maxDistance) {

        // If distance is equal to -1, return all players
        if (maxDistance == -1) return Bukkit.getOnlinePlayers().toArray(new Player[0]);

        // If distance is not equal to -1, loop through all players and return all that satisfy the criteria
        List<Player> playerList = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            double dSq = p.getLocation().distanceSquared(location);
            if (dSq <= maxDistance*maxDistance && dSq >= minDistance) playerList.add(p);
        }

        return playerList.toArray(new Player[0]);
    }

    // ----- INVENTORY MANAGEMENT -----

    public static boolean reduceItemExact(Player player, String item, int count) {
        PlayerInventory inventory = player.getInventory();
        if (countItems(player, item) < count) return false;

        int left = count;

        for (ItemStack itemStack : inventory) {
            if (itemStack == null) continue;
            if (CustomItemManager.getInternalItemName(itemStack).equalsIgnoreCase(item)) {
                int ca = itemStack.getAmount();
                itemStack.setAmount(ca - left);
                left -= ca;

                if (left <= 0) return true;
            }
        }

        return false; // Should never happen
    }

    public static int countItems(Player player, String item) {
        PlayerInventory inventory = player.getInventory();
        int foundItemCount = 0;

        for (ItemStack itemStack : inventory) {
            if (itemStack == null) continue;
            if (CustomItemManager.getInternalItemName(itemStack).equalsIgnoreCase(item)) {
                foundItemCount += itemStack.getAmount();
            }
        }

        return foundItemCount;
    }

    /**
     * Reduces the amount of items in the <code>Player</code>'s hand by the specified amount.
     *
     * @param player the <code>Player</code>.
     * @param count the amount to reduce the <code>ItemStack</code> by.
     */
    public static void reduceHeldItemStack(Player player, int count) {
        PlayerInventory inventory = player.getInventory();
        ItemStack item = inventory.getItemInMainHand();
        item.setAmount(item.getAmount()-count);
        inventory.setItemInMainHand(item);
    }

    /**
     * Drops a percentage of all <code>ItemStack</code>'s in a <code>Player</code>'s inventory on the ground at their <code>Location</code>.
     *
     * @param player the <code>Player</code> to drop the <code>ItemStack</code>'s of.
     * @param percent the percent chance of an <code>ItemStack</code> being dropped.
     */
    public static void dropPercentageOfInventory(Player player, double percent) {
        Random r = new Random();
        World world = player.getWorld();
        Location location = player.getLocation();

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (r.nextDouble() <= percent) {
                if (item != null) {
                    if (item.getType() != Material.AIR) {
                        world.dropItemNaturally(location, item);
                        player.getInventory().setItem(i, new ItemStack(Material.AIR));
                    }
                }
            }
        }
    }

    // ----- EXPERIENCE ----- // Credit: DOGC_Kyle

    /**
     * Calculates the amount of EXP needed to level up.
     *
     * @param level
     * @return
     */
    public static int getExpToLevelUp(int level){
        if(level <= 15){
            return 2*level+7;
        } else if(level <= 30){
            return 5*level-38;
        } else {
            return 9*level-158;
        }
    }

    /**
     * Calculates the total experience needed up to a level.
     *
     * @param level
     * @return
     */
    public static int getExpAtLevel(int level){
        if(level <= 16){
            return (int) (Math.pow(level,2) + 6*level);
        } else if(level <= 31){
            return (int) (2.5*Math.pow(level,2) - 40.5*level + 360.0);
        } else {
            return (int) (4.5*Math.pow(level,2) - 162.5*level + 2220.0);
        }
    }

    /**
     * Calculates the <code>Player</code>'s current EXP amount.
     *
     * @param player the <code>Player</code> to get the EXP from.
     * @return the EXP amount.
     */
    public static int getPlayerExp(Player player){
        int exp = 0;
        int level = player.getLevel();

        // Get the amount of XP in past levels
        exp += getExpAtLevel(level);

        // Get amount of XP towards next level
        exp += Math.round(getExpToLevelUp(level) * player.getExp());

        return exp;
    }

    /**
     * Increases or decreases the <code>Player</code> EXP by the given amount.
     *
     * @param player the <code>Player</code> to change the EXP of.
     * @param exp the EXP amount to change the <code>Player</code>'s with.
     * @return the new EXP amount.
     */
    public static int changePlayerExp(Player player, int exp){
        // Get player's current exp
        int currentExp = getPlayerExp(player);

        // Reset player's current exp to 0
        player.setExp(0);
        player.setLevel(0);

        // Give the player their exp back, with the difference
        int newExp = currentExp + exp;
        player.giveExp(newExp);

        // Return the player's new exp amount
        return newExp;
    }

    /**
     * Changes the <code>Player</code>'s EXP to the given amount.
     *
     * @param player the <code>Player</code> to change the EXP of.
     * @param exp the EXP value to set the value to.
     */
    public static void setPlayerExp(Player player, int exp) {
        // Reset player's current exp to 0
        player.setExp(0);
        player.setLevel(0);

        // Give the player the new exp
        player.giveExp(exp);
    }

    // ----- STORAGE -----

    /**
     * Stores information about a player.
     */
    public static class PlayerStorage {

        // ----- VARIABLES -----

        public UUID uuid;
        public String name;
        public String[] pastNames;
        // public GameMode gameMode; // TODO
        public World world;
        public Location location;

        public Date lastJoined;
        public Date lastLeft;

        public Duration playtime;

        // ----- CONSTRUCTORS -----

        /**
         * Creates a new <code>PlayerStorage</code>.
         * @param uuid the uuid of the player.
         * @param name the name of the player.
         * @param world the world the player is currently in.
         * @param location the current location of the player.
         * @param lastJoined the last time the player joined the game.
         * @param lastLeft the last time the player left the game.
         * @param playtime the time the player has played on the server.
         */
        public PlayerStorage(UUID uuid, String name, World world, Location location, Date lastJoined, Date lastLeft, Duration playtime) {
            this.uuid = uuid;
            this.name = name;
            this.world = world;
            this.location = location;

            this.lastJoined = lastJoined;
            this.lastLeft = lastLeft;

            this.playtime = playtime;
        }

        /**
         * Creates a new <code>PlayerStorage</code>.
         *
         * @param player the player to get information from.
         */
        public PlayerStorage(Player player) {
            this.name = player.getName();
            this.world = player.getWorld();
            this.location = player.getLocation();
        }
    }
}
