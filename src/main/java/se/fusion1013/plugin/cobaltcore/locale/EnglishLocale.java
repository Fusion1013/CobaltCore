package se.fusion1013.plugin.cobaltcore.locale;

import java.util.LinkedHashMap;
import java.util.Map;

public class EnglishLocale implements Locale {
    @Override
    public String getLocaleName() {
        return "en_US";
    }

    @Override
    public String getTranslatorName() {
        return "Fusion1013";
    }

    public static void addLocaleString(String messageKey, String message){
        locale.put(messageKey, message);
    }

    @Override
    public Map<String, String> getDefaultLocaleString() {
        return locale;
    }

    private static final Map<String, String> locale = new LinkedHashMap<String, String>(){
        {
            this.put("#0", "Plugin Message Prefix");
            this.put("prefix.core", "&7[<g:#00aaaa:#0066aa>Cobalt&7] ");

            this.put("#1", "General Command Stuff");
            this.put("commands.error.incorrect_syntax", "&7Incorrect Syntax");

            this.put("#3", "Wand");
            this.put("wand.spell.cast.no_mana", "&7Wand is out of mana");
            this.put("wand.spell.cast.cast_delay", "&7Cast delay");
            this.put("wand.spell.cast.recharge_time", "&7Wand is still recharging");

            this.put("#4", "CGive");
            this.put("commands.cgive.spell.error.spell_not_found", "&7Spell &3%spell_name% &7not found");
            this.put("commands.cgive.spell.success", "&7Gave Spell &3%spell_name% &7to &3%player_name%");
            this.put("commands.cgive.spell.all.success", "&7Gave &3%spell_count% &7Spells to &3%player_name%");
            this.put("commands.cgive.wand.success", "&7Gave new Wand to &3%player_name%");
            this.put("commands.cgive.spell.fromid.wand_not_found", "&7Could not find wand with id &3%wand_id%");
            this.put("commands.cgive.spell.fromid.success", "&7Gave wand with id &3%wand_id% &7to &3%player_name%");

            this.put("#6", "killspells");
            this.put("commands.killspells.killall.success", "&7Killed &3%killed_spells% &7spells");

            this.put("#7", "magick");
            this.put("commands.magick.config.edit", "&7Changed value of &3%key% &7to &3%value%");
            this.put("commands.magick.config.get", "&7Key &3%key% &7has value &3%value%");
            this.put("commands.magick.colors.header", "&lColor Codes");
            this.put("commands.magick.colors.color_codes_description", "Usage: <#HEXCODE>, #HEXCODE, &&7FORMAT_CODE, followed by the message");
            this.put("commands.magick.colors.color_codes", "Formatting Codes: &0&&00 &1&&11 &2&&22 &3&&33 &4&&44 &5&&55 &6&&66 &7&&77 &8&&88 &9&&99 &a&&aa &b&&bb &c&&cc &d&&dd &e&&ee &f&&ff");

            this.put("#10", "Misc");
            this.put("command-not-implemented", "&7Command not yet implemented");
            this.put("command-unknown", "&7Unknown command");

            this.put("#11", "List Messages");
            this.put("list-header", "<g:#00aaaa:#0066aa>------ %header% ------");
            this.put("list-item-name", "&7Name: &3%name%");
            this.put("list-item-location", "&7Location: &3%x%&7x &3%y%&7y &3%z%&7z &7World: &3%world%");
            this.put("list-item-id-name-location", "&7[&3%id%&7] &7Name: &3%name% &7Location: &3%x%&7x &3%y%&7y &3%z%&7z");

            this.put("gradient", "<g:#ff1100:#00ff1e>GRADIENT");
            this.put("rainbow", "&7[<r:1:1>ThisIsAReallyLongRainbowText&7]");
        }
    };
}
