package se.fusion1013.plugin.cobaltcore.util.animation;

public class EasingUtil {

    // ----- BACK -----

    public static float  easeInBack(float t,float b , float c, float d) {
        float s = 1.70158f;
        return c*(t/=d)*t*((s+1)*t - s) + b;
    }

    public static float  easeInBack(float t,float b , float c, float d, float s) {
        return c*(t/=d)*t*((s+1)*t - s) + b;
    }

    public static float  easeOutBack(float t,float b , float c, float d) {
        float s = 1.70158f;
        return c*((t=t/d-1)*t*((s+1)*t + s) + 1) + b;
    }

    public static float  easeOutBack(float t,float b , float c, float d, float s) {
        return c*((t=t/d-1)*t*((s+1)*t + s) + 1) + b;
    }

    public static float  easeInOutBack(float t,float b , float c, float d) {
        float s = 1.70158f;
        if ((t/=d/2) < 1) return c/2*(t*t*(((s*=(1.525f))+1)*t - s)) + b;
        return c/2*((t-=2)*t*(((s*=(1.525f))+1)*t + s) + 2) + b;
    }

    public static float  easeInOutBack(float t,float b , float c, float d, float s) {
        if ((t/=d/2) < 1) return c/2*(t*t*(((s*=(1.525f))+1)*t - s)) + b;
        return c/2*((t-=2)*t*(((s*=(1.525f))+1)*t + s) + 2) + b;
    }

    // ----- SINE -----

    public static float  easeInSine(float t,float b , float c, float d) {
        return -c * (float)Math.cos(t/d * (Math.PI/2)) + c + b;
    }

    public static float  easeOutSine(float t,float b , float c, float d) {
        return c * (float)Math.sin(t/d * (Math.PI/2)) + b;
    }

    public static float  easeInOutSine(float tick, float start , float difference, float duration) {
        return -difference/2 * ((float)Math.cos(Math.PI*tick/duration) - 1) + start;
    }

    public static double  easeInOutSine(double tick, double start , double difference, double duration) {
        return -difference/2 * (Math.cos(Math.PI*tick/duration) - 1) + start;
    }



}
