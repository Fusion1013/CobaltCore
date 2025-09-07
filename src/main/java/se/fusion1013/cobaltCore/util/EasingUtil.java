package se.fusion1013.cobaltCore.util;

public class EasingUtil {

    public static double easeInSine(double x) {
        return 1 - Math.cos((x * Math.PI) / 2);
    }

    public static double easeInOutSine(double x) {
        return -(Math.cos(Math.PI * x) - 1) / 2;
    }

    public static double ease(double x, EasingMethod method) {
        return switch (method) {
            case EaseInSine -> easeInSine(x);
            case EaseInOutSine -> easeInOutSine(x);
            default -> x;
        };
    }

    public enum EasingMethod {
        EaseInSine,
        EaseInOutSine
    }

}
