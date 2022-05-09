package se.fusion1013.plugin.cobaltcore.database.particle.group;

import se.fusion1013.plugin.cobaltcore.database.system.IDao;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IParticleGroupDao extends IDao {

    /**
     * Removes a <code>ParticleGroup</code> from the database.
     *
     * @param uuid the <code>UUID</code> of the <code>ParticleGroup</code>.
     */
    void removeParticleGroup(UUID uuid);

    /**
     * Inserts a list of <code>ParticleGroup</code>'s into the database.
     *
     * @param groups the <code>ParticleGroup</code>'s to insert.
     */
    void insertParticleGroups(List<ParticleGroup> groups);

    /**
     * Gets all <code>ParticleGroup</code>'s from the database.
     *
     * @return all <code>ParticleGroup</code>'s in the database.
     */
    Map<String, ParticleGroup> getParticleGroups();

    @Override
    default String getId() { return "particle_group"; }

}
