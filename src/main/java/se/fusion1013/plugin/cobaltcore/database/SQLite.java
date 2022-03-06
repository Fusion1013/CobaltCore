package se.fusion1013.plugin.cobaltcore.database;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.settings.PlayerSettingHolder;
import se.fusion1013.plugin.cobaltcore.settings.SettingsManager;
import se.fusion1013.plugin.cobaltcore.util.PreCalculateWeightsRandom;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

/**
 * This class is responsible for creating the database and setting up the tables and values
 */
public class SQLite extends Database {

    // ----- VARIABLES -----

    String dbname;

    public static String SQLiteCreatePlayersTable = "CREATE TABLE IF NOT EXISTS players (" +
            "`uuid` varchar(36) NOT NULL," +
            "`name` varchar(32) NOT NULL," +
            "PRIMARY KEY (`uuid`,`name`)" +
            ");";

    public static String SQLiteCreatePlayerLocationsTable = "CREATE TABLE IF NOT EXISTS locations (" +
            "`uuid` varchar(36) NOT NULL," +
            "`world` varchar(32) NOT NULL," +
            "`x_pos` REAL NOT NULL," +
            "`y_pos` REAL NOT NULL," +
            "`z_pos` REAL NOT NULL," +
            "`yaw` REAL NOT NULL," +
            "`pitch` REAL NOT NULL," +
            "PRIMARY KEY (`uuid`)" +
            ");";

    public static String SQLiteCreatePlayerSettingsTable = "CREATE TABLE IF NOT EXISTS player_settings (" +
            "`player_uuid` TEXT," +
            "`setting` TEXT," +
            "`value` TEXT," +
            "PRIMARY KEY (`player_uuid`,`setting`)" +
            ");";

    public static String SQLiteCreateMerchantTradesTable = "CREATE TABLE IF NOT EXISTS merchant_trades (" +
            "`cost_item` varchar(32)," +
            "`cost_count` INTEGER NOT NULL," +
            "`result_item` varchar(32)," +
            "`result_count` INTEGER NOT NULL," +
            "`max_uses` INTEGER NOT NULL," +
            "`weight` INTEGER NOT NULL," +
            "PRIMARY KEY (`cost_item`, `result_item`)" +
            ");";

    // ----- CONSTRUCTORS -----

    public SQLite(CobaltCore plugin){
        super(plugin);
        dbname = plugin.getConfig().getString("SQLite.Filename", "cobalt");
    }

    // ----- LOGIC -----

    public static void dropTable(String table) {
        try {
            Connection connection = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            PreparedStatement s = connection.prepareStatement("DROP TABLE IF EXISTS " + table + ";");
            s.executeUpdate();
            s.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Performs the string statement on the database.
     *
     * @param string Statement to execute.
     */
    public void executeString(String string){
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(string);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ----- GETTERS / SETTERS -----

    // ----- MERCHANT TRADES -----

    public static void removeMerchantTrade(String costItem, String resultItem) {
        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM merchant_trades WHERE cost_item = ? AND result_item = ?");
            ps.setString(1, costItem);
            ps.setString(2, resultItem);
            ps.executeUpdate();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static PreCalculateWeightsRandom<MerchantRecipe> getMerchantTrades() {

        PreCalculateWeightsRandom<MerchantRecipe> list = new PreCalculateWeightsRandom<>();

        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM merchant_trades");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ItemStack result = CustomItemManager.getItemStack(rs.getString("result_item"));

                if (result != null) {
                    int resultCount = rs.getInt("result_count");
                    result.setAmount(resultCount);

                    MerchantRecipe mr = new MerchantRecipe(result, rs.getInt("max_uses"));

                    ItemStack cost = CustomItemManager.getItemStack(rs.getString("cost_item"));

                    if (cost != null) {
                        int costCount = rs.getInt("cost_count");
                        cost.setAmount(costCount);

                        mr.addIngredient(cost);
                        list.addItem(mr, rs.getInt("weight"));
                    }
                }
            }
            conn.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }

    public static void saveMerchantTrades(List<MerchantRecipe> recipes, List<Integer> weights) {
        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO merchant_trades(cost_item, cost_count, result_item, result_count, max_uses, weight) VALUES(?, ?, ?, ?, ?, ?)");
            for (int i = 0; i < recipes.size(); i++) {
                MerchantRecipe mr = recipes.get(i);
                ps.setString(1, CustomItemManager.getItemName(mr.getIngredients().get(0)));
                ps.setInt(2, mr.getIngredients().get(0).getAmount());
                ps.setString(3, CustomItemManager.getItemName(mr.getResult()));
                ps.setInt(4, mr.getResult().getAmount());
                ps.setInt(5, mr.getMaxUses());
                ps.setInt(6, weights.get(i));
                ps.executeUpdate();
            }
            conn.commit();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // ----- SETTINGS -----

    public static Map<UUID, PlayerSettingHolder> getSettings() {
        Map<UUID, PlayerSettingHolder> settings = new HashMap<>();

        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM player_settings");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("player_uuid"));

                PlayerSettingHolder setting = settings.get(uuid);
                if (setting == null) setting = new PlayerSettingHolder();

                String key = rs.getString("setting");
                String value = rs.getString("value");

                setting.setPlayerSetting(key, value);
                settings.put(uuid, setting);
            }
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return settings;
    }

    public static void saveSettings(Map<UUID, PlayerSettingHolder> settings) {
        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO player_settings(player_uuid, setting, value) VALUES(?, ?, ?)");
            for (UUID uuid : settings.keySet()) {
                Map<String, String> values = settings.get(uuid).getPlayerSettings();
                for (String key : values.keySet()) {
                    ps.setString(1, uuid.toString());
                    ps.setString(2, key);
                    ps.setString(3, values.get(key));
                    ps.executeUpdate();
                }
            }
            conn.commit();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Removes a setting from all players.
     *
     * @param setting the setting to remove.
     */
    public static void removeSetting(String setting) {
        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM player_settings WHERE setting = ?");
            ps.setString(1, setting);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Removes a setting for a certain player from the database.
     *
     * @param player the player to remove the setting for.
     * @param setting the setting to remove.
     */
    public static void removeSetting(Player player, String setting) {
        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM player_settings WHERE player_uuid = ? AND setting = ?");
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, setting);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets a setting from the player settings.
     *
     * @param player the player to get the setting from.
     * @param setting the setting to get.
     * @return the value of the setting. Empty string if not found.
     */
    public static String getSetting(Player player, String setting) {
        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM player_settings WHERE player_uuid = ? AND setting = ?");
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, setting);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                return rs.getString("value");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return "";
    }

    /**
     * Sets a player setting in the database.
     *
     * @param player the player to set the setting for.
     * @param setting the setting to set.
     * @param value the value to set the setting to.
     */
    public static void setSetting(Player player, String setting, String value) {
        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO player_settings(player_uuid, setting, value) VALUES(?, ?, ?)");
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, setting);
            ps.setString(3, value);
            ps.executeUpdate();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // ----- LOCATION -----

    /**
     * Removes a location from the database.
     *
     * @param uuid the unique identifier of the location.
     */
    public static void removeLocation(UUID uuid) {
        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM locations WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Inserts a location into the database.
     *
     * @param uuid the unique identifier of the location.
     * @param location the location to insert.
     * @return the number of rows inserted.
     */
    public static int insertLocation(UUID uuid, Location location) {
        int rowsInserted = 0;

        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO locations(uuid, world, x_pos, y_pos, z_pos, yaw, pitch) VALUES(?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, uuid.toString());
            ps.setString(2, location.getWorld().getName());
            ps.setDouble(3, location.getX());
            ps.setDouble(4, location.getY());
            ps.setDouble(5, location.getZ());
            ps.setDouble(6, location.getYaw());
            ps.setDouble(7, location.getPitch());

            rowsInserted = ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return rowsInserted;
    }

    // ----- PLAYER -----

    /**
     * Gets a player name from the database.
     *
     * @param uuid the uuid of the player.
     * @return the player name, or an empty string if not found.
     */
    public static String getPlayerName(UUID uuid) {
        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM players WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("name");
            }

            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return "";
    }

    /**
     * Inserts a player into the database.
     *
     * @param player the player to insert.
     * @return the number of rows inserted.
     */
    public static int insertPlayer(Player player) {
        int rowsInserted = 0;

        try  {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT OR IGNORE INTO players(uuid, name) VALUES(?, ?)");
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, player.getName());

            rowsInserted = ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return rowsInserted;
    }

    public Connection getSQLConnection(){
        File dataFolder = new File(plugin.getDataFolder(), dbname + ".db");
        if (!dataFolder.exists()){
            try {
                plugin.getDataFolder().mkdir();
                dataFolder.createNewFile();
            } catch (IOException e){
                plugin.getLogger().log(Level.SEVERE, "File write error: " + dbname + ".db", e);
            }
        }
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            System.out.println(dbname);
            plugin.getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex){
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }

        return null;
    }

    // ----- LOADING -----

    public void load(){
        executeString(SQLiteCreatePlayersTable);
        executeString(SQLiteCreatePlayerLocationsTable);
        executeString(SQLiteCreatePlayerSettingsTable);
        executeString(SQLiteCreateMerchantTradesTable);
    }
}
