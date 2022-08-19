package se.fusion1013.plugin.cobaltcore.database.trades;

import se.fusion1013.plugin.cobaltcore.database.system.IDao;
import se.fusion1013.plugin.cobaltcore.trades.CustomTradesManager;
import se.fusion1013.plugin.cobaltcore.util.PreGeneratedWeightsRandom;
import se.fusion1013.plugin.cobaltcore.util.RandomCollection;

import java.util.List;

public interface ITradesDao extends IDao {

    /**
     * Removes a Merchant Trade from the database.
     *
     * @param costItem the cost item.
     * @param resultItem the result item.
     */
    void removeMerchantTrade(String costItem, String resultItem);

    /**
     * Gets all Merchant Trades from the database.
     *
     * @return all Merchant Trades in the database.
     */
    RandomCollection<CustomTradesManager.MerchantRecipePlaceholder> getMerchantTrades();

    /**
     * Saves all Merchant Trades to the database.
     *
     * @param trades the trades to save.
     * @param weights the weights associated with the trades.
     */
    void saveMerchantTrades(List<CustomTradesManager.MerchantRecipePlaceholder> trades, Double[] weights);

    @Override
    default String getId() { return "trades"; }

}
