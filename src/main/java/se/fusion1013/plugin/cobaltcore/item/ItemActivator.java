package se.fusion1013.plugin.cobaltcore.item;

public enum ItemActivator {


    /**
     * Activates when the player starts flying.
     */
    PLAYER_ACTIVATE_FLY,
    /**
     * Activates when the player stops flying.
     */
    PLAYER_DEACTIVATE_FLY,
    /**
     * Activates when the player starts sneaking.
     */
    PLAYER_ACTIVATE_SNEAK,
    /**
     * Activates when the player stops sneaking.
     */
    PLAYER_DEACTIVATE_SNEAK,
    /**
     * Activates when the player starts sprinting.
     */
    PLAYER_ACTIVATE_SPRINT,
    /**
     * Activates when the player stops sprinting.
     */
    PLAYER_DEACTIVATE_SPRINT,
    /**
     * Activates when the player left-clicks or right-clicks.
     */
    PLAYER_ALL_CLICK,
    /**
     * Activates when the player enters a bed.
     */
    PLAYER_BED_ENTER,
    /**
     * Activates when the player leaves a bed.
     */
    PLAYER_BED_LEAVE,
    /**
     * Activates right before a player dies.
     */
    PLAYER_BEFORE_DEATH,
    /**
     * Activates when the player breaks a block.
     */
    PLAYER_BLOCK_BREAK,
    /**
     * Activates when the player places a block.
     */
    PLAYER_BLOCK_PLACE,
    /**
     * Activates when the player changes world.
     */
    PLAYER_CHANGE_WORLD,
    /**
     * Activates when the player clicks at another entity.
     */
    PLAYER_CLICK_AT_ENTITY,
    /**
     * Activates when the player is connecting to the server.
     */
    PLAYER_CONNECTION,
    /**
     * Activates when the player is disconnecting from the server.
     */
    PLAYER_DISCONNECTION,
    /**
     * Activates when the player consumes an item.
     */
    PLAYER_CONSUME,
    /**
     * Activates when a player dies.
     */
    PLAYER_DEATH,
    /**
     * Activates when the player deselects the custom item in the hotbar.
     */
    PLAYER_DESELECT_CUSTOM_ITEM,
    /**
     * Activates when the player selectes the custom item in the hotbar.
     */
    PLAYER_SELECT_CUSTOM_ITEM,
    /**
     * Activates when the player dismounts.
     */
    PLAYER_DISMOUNT,
    /**
     * Activates when the player drops an item.
     */
    PLAYER_DROP_ITEM,
    /**
     * Activates when the player drops a custom item.
     */
    PLAYER_DROP_CUSTOM_ITEM,
    /**
     * Activates when a player edits a book.
     */
    PLAYER_EDIT_BOOK,
    /**
     * Activates when a player equips an item.
     */
    PLAYER_EQUIP_ITEM,
    /**
     * Activates when a player unequips an item.
     */
    PLAYER_UNEQUIP_ITEM,
    /**
     * Activates when a player equip a custom item.
     */
    PLAYER_EQUIP_CUSTOM_ITEM,
    /**
     * Activates when a player unequips a custom item.
     */
    PLAYER_UNEQUIP_CUSTOM_ITEM,
    /**
     * Activates when a player fertilizes a block.
     */
    PLAYER_FERTILIZE_BLOCK,
    /**
     * Activates when a player uses a fishing rod.
     */
    PLAYER_FISH,
    /**
     * Activates when a player breaks an item.
     */
    PLAYER_ITEM_BREAK,
    /**
     * Activates when a player jumps.
     */
    PLAYER_JUMP,
    /**
     * Activates when a player kills an entity.
     */
    PLAYER_KILL_ENTITY,
    /**
     * Activates when a player kills a player.
     */
    PLAYER_KILL_PLAYER,

    PLAYER_LAUNCH_PROJECTILE, // TODO

    /**
     * Activates when a player left-clicks.
     */
    PLAYER_LEFT_CLICK,
    /**
     * Activates when a player left-clicks air.
     */
    PLAYER_LEFT_CLICK_AIR,
    /**
     * Activates when a player left-clicks a block.
     */
    PLAYER_LEFT_CLICK_BLOCK,
    /**
     * Activates when a player moves.
     */
    PLAYER_MOVE,
    /**
     * Activates during a player command preprocess.
     */
    PLAYER_COMMAND_PREPROCESS,
    /**
     * Activates when a player sends a command.
     */
    PLAYER_COMMAND_SEND,
    /**
     * Activates when a player is hit by an entity.
     */
    PLAYER_RECEIVE_HIT_BY_ENTITY,
    /**
     * Activates when a player is hit.
     */
    PLAYER_RECEIVE_HIT_GLOBAL,
    /**
     * Activates when a player is in the process of respawning. Note: Modifications of inventory during this step will get overridden.
     */
    PLAYER_RESPAWN,
    /**
     * Activates after a player has respawned.
     */
    PLAYER_POST_RESPAWN,
    /**
     * Activates when a player right-clicks.
     */
    PLAYER_RIGHT_CLICK,
    /**
     * Activates when a player right-clicks air.
     */
    PLAYER_RIGHT_CLICK_AIR,
    /**
     * Activates when a player right-clicks a block.
     */
    PLAYER_RIGHT_CLICK_BLOCK,
    /**
     * Activates when a player shears an entity.
     */
    PLAYER_SHEAR_ENTITY,
    /**
     * Activates when a player hits an entity.
     */
    PLAYER_HIT_ENTITY,

    PLAYER_BUCKET,

    PLAYER_TRAMPLE_CROP, // TODO

    PROJECTILE_HIT_BLOCK, // TODO
    PROJECTILE_HIT_ENTITY, // TODO
    PROJECTILE_HIT_PLAYER, // TODO
    /**
     * Activates when a player clicks an inventory slot.
     */
    INVENTORY_CLICK,

    /**
     * Activates when a player hangs an item frame / painting.
     */
    HANGING_PLACE,

    LOOP // Activates on repeat as long as the item is in the players inventory // TODO

}
