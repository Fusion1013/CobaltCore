package se.fusion1013.plugin.cobaltcore.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.UUID;

public class JsonUtil {

    // ----- MATERIALS -----

    /**
     * Converts a <code>Material</code> array to a <code>JsonArray</code>.
     *
     * @param materials the <code>Material</code> array to convert.
     * @return a <code>JsonArray</code>.
     */
    public static JsonArray toJson(Material[] materials) {
        JsonArray jo = new JsonArray();
        for (Material mat : materials) jo.add(mat.toString());
        return jo;
    }

    /**
     * Converts a <code>JsonArray</code> to a <code>Material</code> array.
     *
     * @param jsonArray The <code>JsonArray</code> to convert.
     * @return a <code>Material</code> array.
     */
    public static Material[] toMaterialArray(JsonArray jsonArray) {
        Material[] materials = new Material[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) materials[i] = Material.valueOf(jsonArray.get(i).getAsString());
        return materials;
    }

    // ----- VECTORS -----

    /**
     * Converts a <code>Vector</code> to a <code>JsonObject</code>.
     *
     * @param vector the <code>Vector</code> to convert.
     * @return a <code>JsonObject</code>.
     */
    public static JsonObject toJson(Vector vector) {
        JsonObject jo = new JsonObject();
        jo.addProperty("x", vector.getX());
        jo.addProperty("y", vector.getY());
        jo.addProperty("z", vector.getZ());
        return jo;
    }

    /**
     * Converts a <code>JsonObject</code> to a <code>Vector</code>.
     *
     * @param jsonObject The <code>JsonObject</code> to convert.
     * @return a <code>Vector</code>.
     */
    public static Vector toVector(JsonObject jsonObject) {
        double x = jsonObject.get("x").getAsDouble();
        double y = jsonObject.get("y").getAsDouble();
        double z = jsonObject.get("z").getAsDouble();
        return new Vector(x, y, z);
    }

    // ----- LOCATION -----

    /**
     * Converts a <code>Location</code> to a <code>JsonObject</code>.
     *
     * @param location the <code>Location</code> to convert.
     * @return a <code>JsonObject</code>.
     */
    public static JsonObject toJson(Location location) {
        JsonObject jo = new JsonObject();
        jo.addProperty("world_uuid", location.getWorld().getUID().toString());
        jo.addProperty("x", location.getX());
        jo.addProperty("y", location.getY());
        jo.addProperty("z", location.getZ());
        jo.addProperty("yaw", location.getYaw());
        jo.addProperty("pitch", location.getPitch());
        return jo;
    }

    /**
     * Converts a <code>JsonObject</code> to a <code>Location</code>.
     *
     * @param jsonObject The <code>JsonObject</code> to convert.
     * @return a <code>Location</code>.
     */
    public static Location toLocation(JsonObject jsonObject) {
        World world = Bukkit.getWorld(UUID.fromString(jsonObject.get("world_uuid").getAsString()));
        double x = jsonObject.get("x").getAsDouble();
        double y = jsonObject.get("y").getAsDouble();
        double z = jsonObject.get("z").getAsDouble();
        float yaw = jsonObject.get("yaw").getAsFloat();
        float pitch = jsonObject.get("pitch").getAsFloat();
        return new Location(world, x, y, z, yaw, pitch);
    }
}
