package se.fusion1013.plugin.cobaltcore.commands.system;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandHandler {

    MethodReturnType methodReturnType() default MethodReturnType.FEEDBACK;

    String permission() default "";

    String overrideName() default "";

    String[] parameterNames(); // TODO: Replace with reflection

    String[] commandSuggestionMethods() default "";

    String[] subCommands() default {""};

    ParameterType[] overrideTypes() default {};

    enum ParameterType {
        TEXT,
        LOCATION_BLOCK,
        NONE
    }

    enum MethodReturnType {
        FEEDBACK,
        LIST
    }
}
