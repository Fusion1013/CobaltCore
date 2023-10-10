package se.fusion1013.plugin.cobaltcore.action;

import se.fusion1013.plugin.cobaltcore.action.system.IAction;
import se.fusion1013.plugin.cobaltcore.action.system.IActionFactory;
import se.fusion1013.plugin.cobaltcore.action.system.IActionType;

import java.util.Map;

public class CoreActionFactory implements IActionFactory {

    @Override
    public IAction createAction(IActionType actionType, Map<?, ?> data) {
        if (actionType == CoreActionType.VelocityAction) return new VelocityAction(data);
        else if (actionType == CoreActionType.AudioAction) return new AudioAction(data);
        else if (actionType == CoreActionType.ItemConsumeAction) return new ItemConsumeAction(data);
        else if (actionType == CoreActionType.IsSneakingAction) return new IsSneakingAction();
        else if (actionType == CoreActionType.Damage) return new DamageAction(data);
        else if (actionType == CoreActionType.Particle) return new ParticleAction(data);
        else if (actionType == CoreActionType.Summon) return new SummonAction(data);
        else if (actionType == CoreActionType.Encounter) return new EncounterAction(data);
        else if (actionType == CoreActionType.Effect) return new EffectAction(data);
        return null;
    }

    @Override
    public IAction createAction(String actionType, Map<?, ?> data) {
        if (actionType.equalsIgnoreCase("velocity_action")) return new VelocityAction(data);
        else if (actionType.equalsIgnoreCase("audio_action")) return new AudioAction(data);
        else if (actionType.equalsIgnoreCase("item_consume_action")) return new ItemConsumeAction(data);
        else if (actionType.equalsIgnoreCase("is_sneaking_action")) return new IsSneakingAction();
        else if (actionType.equalsIgnoreCase("damage_action")) return new DamageAction(data);
        else if (actionType.equalsIgnoreCase("particle_action")) return new ParticleAction(data);
        else if (actionType.equalsIgnoreCase("summon_action")) return new SummonAction(data);
        else if (actionType.equalsIgnoreCase("encounter_action")) return new EncounterAction(data);
        else if (actionType.equalsIgnoreCase("effect_action")) return new EffectAction(data);
        return null;
    }
}
