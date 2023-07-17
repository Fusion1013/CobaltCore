package se.fusion1013.plugin.cobaltcore.action;

import se.fusion1013.plugin.cobaltcore.action.system.IActionType;

public enum CoreActionType implements IActionType {

    // -- Action Actions
    VelocityAction,
    AudioAction,
    ItemConsumeAction,
    Damage,
    Particle,
    Summon,
    Encounter,

    // -- Conditional Actions
    IsSneakingAction
}
