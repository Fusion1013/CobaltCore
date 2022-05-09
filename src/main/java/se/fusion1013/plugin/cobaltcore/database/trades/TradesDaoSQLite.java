package se.fusion1013.plugin.cobaltcore.database.trades;

import org.bukkit.Bukkit;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.system.Dao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.trades.CustomTradesManager;
import se.fusion1013.plugin.cobaltcore.util.PreCalculateWeightsRandom;

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
            "`weight` INTEGER NOT NULL," +
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
            try {
                Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM merchant_trades WHERE cost_item = ? AND result_item = ?");
                ps.setString(1, costItem);
                ps.setString(2, resultItem);
                ps.executeUpdate();
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public PreCalculateWeightsRandom<CustomTradesManager.MerchantRecipePlaceholder> getMerchantTrades() {
        PreCalculateWeightsRandom<CustomTradesManager.MerchantRecipePlaceholder> list = new PreCalculateWeightsRandom<>();

        try {
            Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM merchant_trades");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String costItemName = rs.getString("cost_item");
                int costCount = rs.getInt("cost_count");

                String resultItemName = rs.getString("result_item");
                int resultCount = rs.getInt("result_count");

                int maxUses = rs.getInt("max_uses");

                CustomTradesManager.MerchantRecipePlaceholder mr = new CustomTradesManager.MerchantRecipePlaceholder(costItemName, costCount, resultItemName, resultCount, maxUses);

                list.addItem(mr, rs.getInt("weight"));
            }
            conn.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }

    @Override
    public void saveMerchantTrades(List<CustomTradesManager.MerchantRecipePlaceholder> trades, List<Integer> weights) {
        try {
            Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO merchant_trades(cost_item, cost_count, result_item, result_count, max_uses, weight) VALUES(?, ?, ?, ?, ?, ?)");
            for (int i = 0; i < trades.size(); i++) {
                CustomTradesManager.MerchantRecipePlaceholder mr = trades.get(i);
                ps.setString(1, mr.getCostItemName());
                ps.setInt(2, mr.getCostAmount());
                ps.setString(3, mr.getResultItemName());
                ps.setInt(4, mr.getResultAmount());
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
}
