package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.Argument;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.variable.provider.VectorListValueProvider;

import java.util.List;

public class VectorListVariable extends AbstractVariable<List<Vector>, VectorListValueProvider, Argument<List<Vector>>, VectorListVariable> {

    public VectorListVariable(String name) {
        super(name, new VectorListValueProvider(name));
    }

    public VectorListVariable(String name, VectorListValueProvider defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public VectorListVariable getSelf() {
        return this;
    }

    @Override
    public Argument<List<Vector>> getArgument() {
        return null;
    }
}
