package se.fusion1013.plugin.cobaltcore.animated;

import se.fusion1013.plugin.cobaltcore.util.animation.EasingUtil;

public class AnimatedDouble {

    // ----- VARIABLES -----

    private final double startValue;
    private final double targetValue;
    private final int duration;

    private int t = 0;
    private boolean continuous = true;

    // ----- CONSTRUCTORS -----

    public AnimatedDouble(double startValue, double targetValue, int duration) {
        this.startValue = startValue;
        this.targetValue = targetValue;
        this.duration = duration;
    }

    public AnimatedDouble(double startValue, double targetValue, int duration, boolean continuous) {
        this.startValue = startValue;
        this.targetValue = targetValue;
        this.duration = duration;
        this.continuous = continuous;
    }

    // ----- GETTERS / SETTERS -----

    public double getValue() {
        if (continuous || t < duration) t++;
        return EasingUtil.easeInOutSine(t, startValue, targetValue-startValue, duration); // TODO: Add support for different methods
    }

    public double getStartValue() {
        return startValue;
    }

}
