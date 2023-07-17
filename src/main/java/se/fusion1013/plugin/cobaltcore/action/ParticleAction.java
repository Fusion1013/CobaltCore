package se.fusion1013.plugin.cobaltcore.action;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.action.system.AbstractAction;
import se.fusion1013.plugin.cobaltcore.action.system.ActionResult;
import se.fusion1013.plugin.cobaltcore.action.system.IActionResult;
import se.fusion1013.plugin.cobaltcore.action.system.ILocationAction;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.manager.ParticleStyleManager;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParticleAction extends AbstractAction implements ILocationAction {

    //region FIELDS

    private ParticleGroup group;

    // -- Optional
    private final Vector location2Offset = new Vector();

    //endregion

    //region CONSTRUCTORS

    protected ParticleAction(Map<?, ?> data) {
        super(data);

        if (data.containsKey("particles")) {
            ParticleGroup.ParticleGroupBuilder particleBuilder = new ParticleGroup.ParticleGroupBuilder();
            for (ParticleStyle style : getParticles((List<Map<?,?>>) data.get("particles"))) {
                particleBuilder.addStyle(style);
            }
            group = particleBuilder.build();
        }

        if (data.containsKey("location2_x")) location2Offset.setX((double) data.get("location2_x"));
        if (data.containsKey("location2_y")) location2Offset.setY((double) data.get("location2_y"));
        if (data.containsKey("location2_z")) location2Offset.setZ((double) data.get("location2_z"));
    }

    protected List<ParticleStyle> getParticles(List<Map<?, ?>> particleData) {
        List<ParticleStyle> newStyles = new ArrayList<>();

        for (Map<?, ?> styleData : particleData) {
            for (var key : styleData.keySet()) {
                ParticleStyle newStyle = ParticleStyleManager.createParticleStyleSilent((String) key, (Map<?, ?>) styleData.get(key));
                newStyles.add(newStyle);
            }
        }

        return newStyles;
    }

    //endregion

    //region ACTIVATION

    @Override
    public IActionResult activate(Location location) {
        if (group == null) return new ActionResult(false);

        group.display(location);
        group.display(location, location.clone().add(location2Offset));

        return new ActionResult(true);
    }

    @Override
    public IActionResult activate() {
        if (extraData.containsKey("location")) {
            Location location = (Location) extraData.get("location");
            return activate(location);
        }
        return new ActionResult(false);
    }

    //endregion

    //region GETTERS/SETTERS

    @Override
    public String getInternalName() {
        return "particle_action";
    }

    //endregion
}
