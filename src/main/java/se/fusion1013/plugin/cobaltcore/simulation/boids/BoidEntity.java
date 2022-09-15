package se.fusion1013.plugin.cobaltcore.simulation.boids;

public class BoidEntity {

    // ----- VARIABLES -----

    Vector position;
    Vector velocity;

    // ----- CONSTRUCTORS -----

    public BoidEntity(Vector position, Vector velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    // ----- METHODS -----

    /**
     * Calculate new velocity vector based on current velocity,
     * cohesion, alignment and separation coefficients and bound position.
     *
     * @param  boids                  List of boids, which positions and velocities are used to calculate
     *                                corresponding vectors of cohesion, alignment, separation
     *         xMax                   Maximum value of x-coordinate of position
     *         yMax                   Maximum value of y-coordinate of position
     *         cohesionCoefficient    Value affects speed at which bird moves towards the perceived centre of mass
     *                                e.g 100 means that in each iteration bird moves 1% to the perceived centre
     *         alignmentCoefficient   Value affects velocity increase of bird with respect to the perceived centre
     *                                of mass
     *         separationCoefficient  If bird is within this distance from other boids, it will move away
     */
    public void updateVelocity(BoidEntity[] boids, int xMax, int yMax, int zMax, double cohesionCoefficient, int alignmentCoefficient, double separationCoefficient) {
        velocity = velocity.plus(cohesion(boids,  cohesionCoefficient))
                .plus(alignment(boids, alignmentCoefficient))
                .plus(separation(boids, separationCoefficient))
                .plus(boundPosition(xMax, yMax, zMax));
        limitVelocity();
    }

    /**
     * Update current position using its velocity.
     */
    public void updatePosition() {
        position = position.plus(velocity);
    }
    //rules that determine flock's behaviour
    //are all to apply on bird's velocity

    //cohesion - steer towards the center of mass of local flockmates
    public Vector cohesion(BoidEntity[] boids, double cohesionCoefficient) {
        Vector pcJ = new Vector(0,0,0);
        int length = boids.length;
        for (BoidEntity boid : boids) if (boid != null) pcJ = pcJ.plus(boid.position);
        pcJ = pcJ.div(length);
        return pcJ.minus(position).div(cohesionCoefficient);
    }

    //alignment - steer towards the average heading of local flockmates
    public Vector alignment(BoidEntity[] boids, int alignmentCoefficient) {
        Vector pvJ = new Vector(0,0,0);
        int length = boids.length;
        for (BoidEntity boid : boids) if (boid != null) pvJ = pvJ.plus(boid.velocity);
        pvJ = pvJ.div(length);
        return pvJ.minus(velocity).div(alignmentCoefficient);
    }

    //separation - steer to avoid crowding local flockmates
    public Vector separation(BoidEntity[] boids, double separationCoefficient) {
        Vector c = new Vector(0,0,0);
        for (BoidEntity boid : boids)
            if (boid != null)
                if ((boid.position.minus(position).magnitude()) < separationCoefficient)
                    c = c.minus(boid.position.minus(position));
        return c;
    }

    //keep birds within a certain area
    public Vector boundPosition(int xMax, int yMax, int zMax) {
        int x = 0;
        int y = 0;
        int z = 0;
        if (this.position.data[0] < 0)                x = 10;
        else if (this.position.data[0]  > xMax)       x = -10;
        if (this.position.data[1]  < 0)               y = 10;
        else if (this.position.data[1]  > yMax)       y = -10;
        if (this.position.data[2] < 0)                z = 10;
        else if (this.position.data[2]  > zMax)       z = -10;
        return new Vector(x,y, z);
    }

    //limit the magnitude of the boids' velocities
    public void limitVelocity() {
        int vlim = 100;
        if (this.velocity.magnitude() > vlim) {
            this.velocity = this.velocity.div(this.velocity.magnitude());
            this.velocity = this.velocity.times(vlim);
        }
    }

    public String toString() {
        return "Position: " + this.position + " Velocity: " + this.velocity;
    }
}
