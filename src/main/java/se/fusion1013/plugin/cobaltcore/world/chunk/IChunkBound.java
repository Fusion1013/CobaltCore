package se.fusion1013.plugin.cobaltcore.world.chunk;

import java.util.UUID;

public interface IChunkBound<T> {

    T getObject();

    UUID getUUID();

    default void onChunkLoad() {}

    default void onChunkUnload() {}

}
