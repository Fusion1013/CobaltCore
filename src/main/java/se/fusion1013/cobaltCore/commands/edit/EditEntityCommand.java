package se.fusion1013.cobaltCore.commands.edit;

import dev.jorel.commandapi.CommandAPICommand;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.commands.entity.EntityPastebinCommand;
import se.fusion1013.cobaltCore.util.CommandUtil;

public class EditEntityCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("entity")
                .withPermission(CommandUtil.getPermissionString(CobaltCore.getInstance(), "edit.entity"))
                .withSubcommand(EntityPastebinCommand.createItemPastebinCommand());
    }

}
