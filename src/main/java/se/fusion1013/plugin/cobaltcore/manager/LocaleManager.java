package se.fusion1013.plugin.cobaltcore.manager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.CobaltPlugin;
import se.fusion1013.plugin.cobaltcore.locale.EnglishLocale;
import se.fusion1013.plugin.cobaltcore.locale.Message;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

public class LocaleManager extends Manager {

    private static LocaleManager INSTANCE = null;
    /**
     * Returns the object representing this <code>LocaleManager</code>.
     *
     * @return The object of this class
     */
    public static LocaleManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new LocaleManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }

    EnglishLocale englishLocale;

    public LocaleManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        englishLocale = new EnglishLocale();
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    public String getLocaleMessage(String messageKey){
        return this.getLocaleMessage(messageKey, StringPlaceholders.empty());
    }

    public String getLocaleMessage(String messageKey, StringPlaceholders stringPlaceholders){
        String message = this.englishLocale.getDefaultLocaleString().get(messageKey);
        if (message == null){
            return ChatColor.RED + "Missing message in locale file: " + messageKey;
        }
        return HexUtils.colorify(stringPlaceholders.apply(message));
    }

    public void sendMessage(CommandSender sender, Message message, StringPlaceholders stringPlaceholders) {
        sendMessage((Player)sender, message, stringPlaceholders);
    }

    public void sendMessage(CommandSender sender, Message message) {
        sendMessage((Player)sender, message);
    }

    public void sendMessage(Player player, Message message, StringPlaceholders stringPlaceholders) {
        sendParsedMessage(player, this.getLocaleMessage(message.prefix) + this.getLocaleMessage(message.key, stringPlaceholders));
    }

    public void sendMessage(Player player, Message message) {
        sendMessage(player, message, StringPlaceholders.builder().build());
    }

    /*
    public void sendMessage(CommandSender sender, String messageKey, StringPlaceholders stringPlaceholders){
        String prefix = this.getLocaleMessage("prefix");
        this.sendParsedMessage(sender, prefix + this.getLocaleMessage(messageKey, stringPlaceholders));
    }

    public void sendMessage(CommandSender sender, String messageKey){
        sender.sendMessage(getLocaleMessage("prefix") + getLocaleMessage(messageKey));
    }

    public void sendMessage(Player player, String messageKey, StringPlaceholders stringPlaceholders){
        String prefix = this.getLocaleMessage("prefix");
        this.sendParsedMessage(player, prefix + this.getLocaleMessage(messageKey, stringPlaceholders));
    }

    public void sendMessage(Player player, String messageKey){
        player.sendMessage(getLocaleMessage("prefix") + getLocaleMessage(messageKey));
    }
     */

    private void sendParsedMessage(Player player, String message) {
        HexUtils.sendMessage(player, message);
    }
}
