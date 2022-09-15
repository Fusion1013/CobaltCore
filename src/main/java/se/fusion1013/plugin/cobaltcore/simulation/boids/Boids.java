package se.fusion1013.plugin.cobaltcore.simulation.boids;

import se.fusion1013.plugin.cobaltcore.util.kdtree.KDTree;

/**
 * This implementation of the Boids algorithm uses the following simple rules;
 * - Separation: Steer to avoid crowding local flockmates
 * - Alignment: Steer toward the average heading of local flockmates
 * - Cohesion: Steer to move towards the average position (center of mass) of local flockmates
 */
public class Boids {

    KDTree kd; // KDTree is used to find nearby flockmates quickly
    BoidEntity[] boidEntities;
    int xRes;
    int yRes;
    // TODO: int zRes;

}
