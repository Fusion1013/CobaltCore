package se.fusion1013.plugin.cobaltcore.action;

import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.action.system.AbstractAction;
import se.fusion1013.plugin.cobaltcore.action.system.ActionResult;
import se.fusion1013.plugin.cobaltcore.action.system.IActionResult;
import se.fusion1013.plugin.cobaltcore.action.system.ILocationAction;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntityManager;
import se.fusion1013.plugin.cobaltcore.entity.ICustomEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SummonAction extends AbstractAction implements ILocationAction {

    //region FIELDS

    // -- Required
    private final String entity;

    // -- Optional
    private int count = 1;
    private String identifier;

    //endregion

    //region CONSTRUCTION

    protected SummonAction(Map<?, ?> data) {
        super(data);

        // Required fields
        entity = (String) data.get("entity");

        // Optional
        if (data.containsKey("count")) count = (int) data.get("count");
        if (data.containsKey("amount")) count = (int) data.get("amount");

        if (data.containsKey("identifier")) identifier = (String) data.get("identifier");
        if (data.containsKey("id")) identifier = (String) data.get("id");
        if (data.containsKey("entity_id")) identifier = (String) data.get("entity_id");
    }

    //endregion

    //region GETTERS/SETTERS

    @Override
    public String getInternalName() {
        return "summon_action";
    }

    //endregion

    //region ACTIVATION

    @Override
    public IActionResult activate() {
        if (extraData.containsKey("location")) {
            Location location = (Location) extraData.get("location");
            return activate(location);
        }
        return new ActionResult(false);
    }

    @Override
    public IActionResult activate(Location location) {
        // Spawn the entity
        List<ICustomEntity> entities = new ArrayList<>();
        for (int i = 0; i < count; i++) entities.add(CustomEntityManager.forceSummonEntity(entity, location));

        ActionResult result = new ActionResult(true);

        // Set entity identifier
        if (identifier == null) result.extraData("entity", entities);
        else if (identifier.equalsIgnoreCase("")) result.extraData("entity", entities);
        else result.extraData(identifier, entities);

        return result;
    }

    //endregion
}
