package se.fusion1013.cobaltCore.util;

import java.util.function.Function;

public class EasingUtil {

    public static double sineIn(double t) {
        return 1 - Math.cos((t * Math.PI) / 2);
    }

    public static double sineOut(double t) {
        return Math.sin((t * Math.PI) / 2);
    }

    public static double sineInOut(double t) {
        return -(Math.cos(Math.PI * t) - 1) / 2;
    }

    public static double quadIn(double t) {
        return t * t;
    }

    public static double quadOut(double t) {
        return 1 - (1 - t) * (1 - t);
    }

    public static double quadInOut(double t) {
        if (t < 0.5) {
            return 2 * t * t;
        } else {
            return 1 - Math.pow(-2 * t + 2, 2) / 2;
        }
    }

    public static EasingMethod getEasingMethod(String name) {
        return EasingMethod.valueOf(name);
    }

    public enum EasingMethod {
        EaseInSine(EasingUtil::sineIn),
        EaseOutSine(EasingUtil::sineOut),
        EaseInOutSine(EasingUtil::sineInOut),

        EaseInQuad(EasingUtil::quadIn),
        EaseOutQuad(EasingUtil::quadOut),
        EaseInOutQuad(EasingUtil::quadInOut);

        private final Function<Double, Double> easingMethod;

        EasingMethod(Function<Double, Double> easingMethod) {
            this.easingMethod = easingMethod;
        }

        public double ease(double t) {
            return easingMethod.apply(t);
        }
    }

}
