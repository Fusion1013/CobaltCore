package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.Argument;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.variable.provider.VectorValueProvider;

public class VectorVariable extends AbstractVariable<Vector, VectorValueProvider, Argument<Vector>, VectorVariable> {

    public VectorVariable(String name) {
        super(name, new VectorValueProvider(name));
    }

    public VectorVariable(String name, Vector defaultValue) {
        super(name, new VectorValueProvider(name, defaultValue));
    }

    @Override
    public VectorVariable getSelf() {
        return this;
    }

    @Override
    public Argument<Vector> getArgument() {
        return null;
    }
}
