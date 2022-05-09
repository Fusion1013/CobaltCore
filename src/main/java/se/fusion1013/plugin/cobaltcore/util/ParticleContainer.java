package se.fusion1013.plugin.cobaltcore.util;

import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Contains the information to display one particle
 */
public class ParticleContainer implements Cloneable {

    // ----- VARIABLES -----

    private Location location;
    private double speed;
    private double xOff, yOff, zOff;
    private boolean directional;
    private Object overrideData;
    private float size;
    private int count;

    // ----- CONSTRUCTORS -----

    public ParticleContainer(Location location, double xOff, double yOff, double zOff, double speed, int count, boolean directional, Object overrideData){
        this.location = location;
        this.xOff = xOff;
        this.yOff = yOff;
        this.zOff = zOff;
        this.speed = speed;
        this.directional = directional;
        this.overrideData = overrideData;
        this.count = count;
    }

    public ParticleContainer(Location location, double xOff, double yOff, double zOff, double speed, int count, boolean directional){
        this(location, xOff, yOff, zOff, speed, count, directional, null);
    }

    public ParticleContainer(Location location, double xOff, double yOff, double zOff, double speed, int count){
        this(location, xOff, yOff, zOff, speed, count,false, null);
    }

    public ParticleContainer(Location location){
        this(location, 0.0F, 0.0F, 0.0F, 0.0F, 1, false, null);
    }

    // ----- GETTERS / SETTERS -----

    public Location getLocation(){
        return this.location;
    }

    public double getSpeed(){
        return this.speed;
    }

    public boolean isDirectional(){
        return this.directional;
    }

    public double getxOff(){
        return this.xOff;
    }

    public double getSize(){
        return this.size;
    }

    public double getyOff(){
        return this.yOff;
    }

    public double getzOff(){
        return this.zOff;
    }

    public int getCount() { return this.count; }

    public Object getOverrideData(){
        return this.overrideData;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setLocationPosition(Vector position) {
        this.location.set(position.getX(), position.getY(), position.getZ());
    }

    public ParticleContainer(ParticleContainer target) {
        this.location = target.location.clone();
        this.speed = target.speed;
        this.xOff = target.getxOff();
        this.yOff = target.getyOff();
        this.zOff = target.getzOff();
        this.directional = target.directional;
        this.overrideData = target.overrideData;
        this.size = target.size;
        this.count = target.getCount();
    }

    public ParticleContainer clone() {
        return new ParticleContainer(this);
    }
}
