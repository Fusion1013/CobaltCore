package se.fusion1013.plugin.cobaltcore.world.structure;

import org.bukkit.Location;

import java.util.UUID;

public interface IStructure {

    // Internals
    void setInternalName(String internalName);
    String getInternalName();

    String getStructureType();

    UUID getUuid();

    // Location
    Location getLocation();

    // Data
    String getData();
    IStructure fromData(String data);

    String getFormattedInfoString(); // This gets a string containing information about the structure. It should be generated through the locale.

}
