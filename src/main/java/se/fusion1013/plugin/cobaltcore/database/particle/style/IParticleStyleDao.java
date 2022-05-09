package se.fusion1013.plugin.cobaltcore.database.particle.style;

import se.fusion1013.plugin.cobaltcore.database.system.IDao;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyle;

import java.util.List;
import java.util.Map;

public interface IParticleStyleDao extends IDao {

    /**
     * Removes a <code>ParticleStyle</code> from the database.
     *
     * @param styleName the name of the <code>ParticleStyle</code>.
     */
    void removeParticleStyle(String styleName);

    /**
     * Inserts a list of <code>ParticleStyle</code>'s into the database.
     *
     * @param styles a list of <code>ParticleStyle</code>'s to insert.
     */
    void insertParticleStyles(List<ParticleStyle> styles);

    /**
     * Gets a map of <code>ParticleStyle</code>'s from the database.
     *
     * @return a map of <code>ParticleStyle</code>'s.
     */
    Map<String, ParticleStyle> getParticleStyles();

    @Override
    default String getId() { return "particle_style"; }

}
