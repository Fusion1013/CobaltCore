package se.fusion1013.plugin.cobaltcore.simulation.boids;

import se.fusion1013.plugin.cobaltcore.util.CG.KDTree;
import se.fusion1013.plugin.cobaltcore.util.CG.KeyDuplicateException;
import se.fusion1013.plugin.cobaltcore.util.CG.KeySizeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.Graphics;

/**
 * This implementation of the Boids algorithm uses the following simple rules;
 * - Separation: Steer to avoid crowding local flockmates
 * - Alignment: Steer toward the average heading of local flockmates
 * - Cohesion: Steer to move towards the average position (center of mass) of local flockmates
 */
public class Boids {
    KDTree kd;     //kd-tree structure is used to find bird's neighbours fast
    BoidEntity[] boids;
    int N;         //number of boids to process
    public int xRes;      //maximum x-coordinate of field
    public int yRes;      //maximum y-coordinate of field
    public int zRes;      //maximum z-coordinate of field

    /**
     * Initialize the array of bird-like objects with random coordinates within certain area,
     * determined by width and height, so each bird will be asigned position vector (from 0 to width, from 0 to height)
     * and zero velocity vector.
     *
     * @param  amount                 Number of boids to create.
     *         width                  Maximum value of x-coordinate of position.
     *         height                 Maximum value of y-coordinate of position.
     *         depth                  Maximum value of z-coordinate of position.
     */
    public Boids(int amount, int width, int height, int depth)
    {
        N = amount;
        xRes = width;
        yRes = height;
        zRes = depth;
        kd =  new KDTree(3);
        boids = new BoidEntity[N];
        Random rand = new Random();

        List<Vector> positions = new ArrayList<>();

        for (int i = 0; i < N - 1; i++)
        {
            Vector boidPos;
            do {
                boidPos = new Vector(rand.nextInt(xRes),rand.nextInt(yRes),rand.nextInt(zRes));
            } while (positions.contains(boidPos));
            positions.add(boidPos);
            boids[i] = new BoidEntity(boidPos, new Vector(0,0,0));

            try{
                kd.insert(boids[i].position.data, boids[i]);
            } catch (Exception e) {
                System.out.println("Init Exception caught: " + e);
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates each boid's position and velocity depending on it's neighbours.
     *
     * @param  distance               Number of neighbours, which positions and velocities are used to calculate
     *                                corresponding vectors of cohesion, alignment, separation of a bird.
     *         cohesionCoefficient    Value affects speed at which bird moves towards the perceived centre of mass
     *                                e.g 100 means that in each iteration bird moves 1% to the perceived centre
     *         alignmentCoefficient   Value affects velocity increase of bird with respect to the perceived centre
     *                                of mass
     *         separationCoefficient  If bird is within this distance from other birds, it will move away
     */
    public void move(int distance, double cohesionCoefficient, int alignmentCoefficient, double separationCoefficient)
    {
        try{
            for (int i = 0; i < N - 1; i++)
            {
                double[] coords = boids[i].position.data;
                BoidEntity[] nbrs = new BoidEntity[distance];
                kd.nearest(coords, distance).toArray(nbrs);
                try {
                    kd.delete(coords);
                } catch (Exception e) {
                    // we ignore this exception on purpose
                    System.out.println("KeyMissingException deleting caught: " + e + e.getMessage());
                }
                boids[i].updateVelocity(nbrs, xRes, yRes, zRes, cohesionCoefficient, alignmentCoefficient, separationCoefficient);
                boids[i].updatePosition();
                kd.insert(boids[i].position.data, boids[i]);
            }

            //the implementation of deletion in KdTree does not actually delete nodes,
            //but only marks them, that affects performance, so it's necessary to rebuild the tree
            //after long sequences of insertions and deletions
            kd = new KDTree(3);
            for (int i = 0; i < N - 1; i++)
                kd.insert(boids[i].position.data, boids[i]);
        } catch (KeySizeException | KeyDuplicateException e) {
            System.out.println("KeySizeException/KeyDuplicateException caught: " + e + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unknown exception caught: ");
            e.printStackTrace();
        }
    }

    /**
     * Draws each boid as a point on the graphics object.
     *
     * @param  g                 Graphics object to draw on.
     */
    public void draw(Graphics g)
    {
        for (int i = 0; i < N - 1; i++)
        {
            int x = (int) boids[i].position.data[0];
            int y = (int) boids[i].position.data[1];
            int z = (int) boids[i].position.data[2];
            g.drawLine(x, y, x, y);
        }
    }

    public List<Vector> getPositions() {
        List<Vector> positions = new ArrayList<>();
        for (BoidEntity boid : boids) {
            if (boid != null) positions.add(boid.position);
        }
        return positions;
    }
}