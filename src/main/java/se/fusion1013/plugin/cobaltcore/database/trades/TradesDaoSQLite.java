package se.fusion1013.plugin.cobaltcore.database.trades;

import org.bukkit.Bukkit;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.system.Dao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.trades.CustomTradesManager;
import se.fusion1013.plugin.cobaltcore.util.PreGeneratedWeightsRandom;
import se.fusion1013.plugin.cobaltcore.util.RandomCollection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TradesDaoSQLite extends Dao implements ITradesDao {

    public static String SQLiteCreateMerchantTradesTable = "CREATE TABLE IF NOT EXISTS merchant_trades (" +
            "`cost_item` varchar(32)," +
            "`cost_count` INTEGER NOT NULL," +
            "`result_item` varchar(32)," +
            "`result_count` INTEGER NOT NULL," +
            "`max_uses` INTEGER NOT NULL," +
            "`weight` DOUBLE NOT NULL," +
            "PRIMARY KEY (`cost_item`, `result_item`)" +
            ");";

    @Override
    public DataManager.StorageType getStorageType() {
        return DataManager.StorageType.SQLITE;
    }

    @Override
    public void init() {
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreateMerchantTradesTable);
    }

    @Override
    public void removeMerchantTrade(String costItem, String resultItem) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> {
            try (
                    Connection conn = getDataManager().getSqliteDb().getSQLConnection();
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM merchant_trades WHERE cost_item = ? AND result_item = ?")
            ) {
                ps.setString(1, costItem);
                ps.setString(2, resultItem);
                ps.execute();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public RandomCollection<CustomTradesManager.MerchantRecipePlaceholder> getMerchantTrades() {
        RandomCollection<CustomTradesManager.MerchantRecipePlaceholder> list = new RandomCollection<>();

        try (
                Connection conn = getDataManager().getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM merchant_trades");
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                String costItemName = rs.getString("cost_item");
                int costCount = rs.getInt("cost_count");

                String resultItemName = rs.getString("result_item");
                int resultCount = rs.getInt("result_count");

                int maxUses = rs.getInt("max_uses");

                CustomTradesManager.MerchantRecipePlaceholder mr = new CustomTradesManager.MerchantRecipePlaceholder(costItemName, costCount, resultItemName, resultCount, maxUses);

                list.addItem(rs.getDouble("weight"), mr);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }

    @Override
    public void saveMerchantTrades(List<CustomTradesManager.MerchantRecipePlaceholder> trades, Double[] weights) {
        try (
                Connection conn = getDataManager().getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO merchant_trades(cost_item, cost_count, result_item, result_count, max_uses, weight) VALUES(?, ?, ?, ?, ?, ?)")
        ) {
            conn.setAutoCommit(false);
            for (int i = 0; i < trades.size(); i++) {
                CustomTradesManager.MerchantRecipePlaceholder mr = trades.get(i);
                ps.setString(1, mr.getCostItemName());
                ps.setInt(2, mr.getCostAmount());
                ps.setString(3, mr.getResultItemName());
                ps.setInt(4, mr.getResultAmount());
                ps.setInt(5, mr.getMaxUses());
                ps.setDouble(6, weights[i]);
                ps.execute();
            }
            conn.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
