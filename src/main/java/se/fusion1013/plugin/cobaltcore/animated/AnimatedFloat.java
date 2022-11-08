package se.fusion1013.plugin.cobaltcore.animated;

import se.fusion1013.plugin.cobaltcore.util.animation.EasingUtil;

public class AnimatedFloat {

    // ----- VARIABLES -----

    float startValue;
    float targetValue;
    int duration;

    int t = 0;

    boolean continous = true;

    // ----- CONSTRUCTORS -----

    public AnimatedFloat(float startValue, float targetValue, int duration) {
        this.startValue = startValue;
        this.targetValue = targetValue;
        this.duration = duration;
    }

    public AnimatedFloat(float startValue, float targetValue, int duration, boolean continous) {
        this.startValue = startValue;
        this.targetValue = targetValue;
        this.duration = duration;
        this.continous = continous;
    }

    // ----- GETTERS / SETTERS -----

    public float getValue() {
        if (continous || t < duration) t++;
        return EasingUtil.easeInOutSine(t, startValue, targetValue-startValue, duration);
    }

}
