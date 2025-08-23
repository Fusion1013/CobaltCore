package se.fusion1013.plugin.cobaltcore.util.heatmap;

import org.joml.Vector3i;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.debug.DebugManager;
import se.fusion1013.plugin.cobaltcore.util.Vector3;
import se.fusion1013.plugin.cobaltcore.util.Vector3Int;

import java.util.HashMap;
import java.util.Map;

public class Heatmap3D {

    public float maxValue;

    public final int particlesPerChunk = 16;
    private final int falloff;

    public final Map<String, Float> points = new HashMap<>();

    public Heatmap3D(float particleDistance, int falloff) {
        this.falloff = falloff;
    }

    public void addPoint(Vector3Int point) {
        for (int x = point.x - falloff; x <= point.x + falloff; x++) {
            for (int y = point.y - falloff; y <= point.y + falloff; y++) {
                for (int z = point.z - falloff; z <= point.z + falloff; z++) {


                    var position = new Vector3Int(x, y, z);
                    var distance = distanceSquared(point, position);

                    var id = x + "," + y + "," + z;
                    points.putIfAbsent(id, 0f);

                    var newValue = points.get(id) + 1 - clamp((float)distance / falloff, 0, 1);
                    points.put(id, newValue);
                    if (newValue > maxValue) maxValue = newValue;


                }
            }
        }
    }

    private float easeInOutCube(float x) {
        return x < 0.5 ? 4 * x * x * x : 1 - pow(-2 * x + 2, 3) / 2;
    }

    public static double distanceSquared(Vector3Int a, Vector3Int b) {
        float num1 = b.x - a.x;
        float num2 = b.y - a.y;
        float num3 = b.z - a.z;
        return Math.sqrt(num1 * num1 + num2 * num2 + num3 * num3);
    }

    private float clamp(float value, float low, float high) {
        if (value < low) return low;
        return Math.min(value, high);
    }

    private float pow(float x, float p) {
        float result = 1;
        for (int i = 0; i < p; i++) result *= x;
        return result;
    }

}
