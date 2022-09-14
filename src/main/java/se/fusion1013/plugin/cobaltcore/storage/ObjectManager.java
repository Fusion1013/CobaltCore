package se.fusion1013.plugin.cobaltcore.storage;

import com.google.gson.JsonObject;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.mappings.IMappingsDao;
import se.fusion1013.plugin.cobaltcore.database.storage.IObjectStorageDao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

import java.util.*;

public class ObjectManager extends Manager implements Listener {

    // ----- VARIABLES -----

    // -- Storage
    private static final Map<String, IStorageObject> DEFAULT_STORAGES = new HashMap<>();
    // <ChunkWorldKey, <StorageIdentifier, <StorageUUID, StorageObject>>>
    private static final Map<String, Map<String, Map<UUID, IStorageObject>>> LOADED_STORAGES = new HashMap<>();
    private static final Map<String, Map<UUID, String>> LOADED_MAPPINGS = new HashMap<>();

    // -- Command
    private static final CommandAPICommand rootObjectCommand = new CommandAPICommand("object")
            .withPermission("cobalt.core.command.object");

    // ----- CONSTRUCTORS -----

    public ObjectManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- REGISTER STORAGE OBJECTS -----

    public static IStorageObject TEST_OBJECT = registerDefaultStorage(new StorageTestObject("test_object", 0));

    /**
     * Registers a new <code>IStorageObject</code> as a default storage.
     *
     * @param object the <code>IStorageObject</code> to register.
     * @return the <code>IStorageObject</code>.
     */
    public static IStorageObject registerDefaultStorage(IStorageObject object) {
        DEFAULT_STORAGES.put(object.getObjectIdentifier(), object);
        updateCommand(); // Update the command every time a new storage solution is registered
        return object;
    }

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltCore.getInstance());
    }

    @Override
    public void disable() {
    }

    // ----- EVENTS -----

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(core, () -> {
            Chunk chunk = event.getChunk();
            String chunkWorldKey = getChunkWorldKey(chunk);

            // Load all objects in the chunk from the database
            Map<String, JsonObject> jsonObjectMap = core.getManager(core, DataManager.class).getDao(IObjectStorageDao.class).getJsonStorageInChunk(chunkWorldKey);

            // Loop through all objects in the chunk and add them to the loaded objects map
            for (String storageIdentifier : jsonObjectMap.keySet()) {
                IStorageObject storageObject = createStorageObject(storageIdentifier, jsonObjectMap.get(storageIdentifier));
                if (storageObject == null) continue;
                insertStorageObject(storageObject, chunkWorldKey);
            }
        });
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(core, () -> {
            Chunk chunk = event.getChunk();
            String chunkWorldKey = getChunkWorldKey(chunk);
            Map<String, Map<UUID, IStorageObject>> unloadedObjects = LOADED_STORAGES.remove(chunkWorldKey);

            if (unloadedObjects == null) return;

            // Call unload method for all removed objects
            for (Map<UUID, IStorageObject> uuidObjectMap : unloadedObjects.values()) {
                for (IStorageObject object : uuidObjectMap.values()) {
                    object.onUnload();

                    // Unload object mapping
                    LOADED_MAPPINGS.remove(getDefaultMapping(chunkWorldKey, object.getObjectIdentifier()));
                }
            }
        });
    }

    // ----- REMOVE STORAGE OBJECTS -----

    public static void removeStorageObject(UUID uuid, String type, Chunk chunk) {
        // Remove from database
        CobaltCore.getInstance().getManager(CobaltCore.getInstance(), DataManager.class).getDao(IObjectStorageDao.class).removeJsonStorageAsync(uuid);
        String chunkWorldKey = getChunkWorldKey(chunk);

        Map<String, Map<UUID, IStorageObject>> typeMap = LOADED_STORAGES.get(chunkWorldKey);
        if (typeMap == null) return;

        Map<UUID, IStorageObject> objectMap = typeMap.get(type);
        if (objectMap == null) return;

        IStorageObject removedObject = objectMap.remove(uuid);
        removedObject.onUnload(); // TODO: Might cause problems, perhaps replace with new onRemove() method?

        // Remove associated mappings
        LOADED_MAPPINGS.get(getDefaultMapping(chunkWorldKey, type)).remove(uuid);
        CobaltCore.getInstance().getManager(CobaltCore.getInstance(), DataManager.class).getDao(IMappingsDao.class).removeMappingAsync(uuid);
    }

    // ----- CREATE STORAGE OBJECTS -----

    /**
     * Inserts a <code>IStorageObject</code> into the database & the Loaded Storages map if the chunk is loaded.
     *
     * @param object the <code>IStorageObject</code> to insert.
     * @param chunk the <code>Chunk</code> the <code>IStorageObject</code> is in.
     */
    public static void insertStorageObject(IStorageObject object, Chunk chunk) {
        // Insert into database
        String chunkWorldKey = getChunkWorldKey(chunk);
        CobaltCore.getInstance().getManager(CobaltCore.getInstance(), DataManager.class).getDao(IObjectStorageDao.class).insertJsonStorageAsync(object.getUniqueIdentifier(), chunkWorldKey, object);

        // Create mapping
        String mappingType = getDefaultMapping(chunkWorldKey, object.getObjectIdentifier());
        CobaltCore.getInstance().getManager(CobaltCore.getInstance(), DataManager.class).getDao(IMappingsDao.class).insertMappingAsync(mappingType, object.getUniqueIdentifier(), mappingType);

        // Insert into storage if chunk is loaded
        if (chunk.isLoaded()) {
            insertStorageObject(object, chunkWorldKey);
        }
    }

    private static String getDefaultMapping(String chunkWorldKey, String objectIdentifier) {
        return chunkWorldKey + ":" + objectIdentifier;
    }

    /**
     * Inserts a <code>IStorageObject</code> into the Loaded Storages map.
     *
     * @param object the <code>IStorageObject</code> to insert.
     * @param chunkWorldKey the chunk world key.
     */
    private static void insertStorageObject(IStorageObject object, String chunkWorldKey) {
        LOADED_STORAGES.computeIfAbsent(chunkWorldKey, k -> new HashMap<>())
                .computeIfAbsent(object.getObjectIdentifier(), k -> new HashMap<>())
                .put(object.getUniqueIdentifier(), object);

        // Call load method for loaded object
        object.onLoad();

        // Load object mapping
        Map<UUID, String> newMappings = CobaltCore.getInstance().getManager(CobaltCore.getInstance(), DataManager.class).getDao(IMappingsDao.class).getMappingsOfType(getDefaultMapping(chunkWorldKey, object.getObjectIdentifier()));
        LOADED_MAPPINGS.computeIfAbsent(getDefaultMapping(chunkWorldKey, object.getObjectIdentifier()), k -> new HashMap<>())
                .putAll(newMappings);
    }

    /**
     * Creates a new <code>IStorageObject</code> given a default name & object data.
     *
     * @param defaultName the default name of the <code>IStorageObject</code>.
     * @param objectData the data to insert into the <code>IStorageObject</code>.
     * @return the new <code>IStorageObject</code>.
     */
    private static IStorageObject createStorageObject(String defaultName, JsonObject objectData) {
        IStorageObject defaultStorage = getDefaultStorage(defaultName);
        if (defaultStorage == null) return null;

        IStorageObject newStorage = defaultStorage.clone();
        newStorage.fromJson(objectData);
        return newStorage;
    }

    /**
     * Creates a new <code>IStorageObject</code> given a default name & <code>Chunk</code>.
     *
     * @param defaultName the default name of the <code>IStorageObject</code>.
     * @param location the <code>Location</code> the object is at.
     * @return the new <code>IStorageObject</code>.
     */
    private static IStorageObject createStorageObject(String defaultName, Location location) {
        IStorageObject defaultStorage = getDefaultStorage(defaultName);
        if (defaultStorage == null) return null;

        UUID newUUID = UUID.randomUUID();

        IStorageObject newStorage = defaultStorage.clone();
        newStorage.setUniqueIdentifier(newUUID);
        newStorage.setLocation(location);
        insertStorageObject(newStorage, location.getChunk());

        return newStorage;
    }

    // ----- UPDATE STORAGE OBJECT -----

    public static void updateStorageObject(IStorageObject object) {
        String chunkWorldKey = getChunkWorldKey(object.getLocation().getChunk());
        CobaltCore.getInstance().getManager(CobaltCore.getInstance(), DataManager.class).getDao(IObjectStorageDao.class).insertJsonStorageAsync(object.getUniqueIdentifier(), chunkWorldKey, object);
    }

    // ----- SET STORAGE OBJECT VARIABLES -----

    private static boolean setStorageObjectValue(String type, UUID uuid, String key, Object value) {
        IStorageObject storageObject = getLoadedObject(type, uuid);
        if (storageObject == null) return false;

        storageObject.setValue(key, value);
        updateStorageObject(storageObject);
        return true;
    }

    private static boolean addStorageObjectListValue(String type, UUID uuid, String key, Object value) {
        IStorageObject storageObject = getLoadedObject(type, uuid);
        if (storageObject == null) return false;
        storageObject.addItem(key, value);
        updateStorageObject(storageObject);
        return true;
    }

    private static boolean removeStorageObjectListValue(String type, UUID uuid, String key, Object value) {
        IStorageObject storageObject = getLoadedObject(type, uuid);
        if (storageObject == null) return false;
        storageObject.addItem(key, value);
        updateStorageObject(storageObject);
        return true;
    }

    // ----- MAPPINGS INTEGRATION -----

    private static void updateMappings(IStorageObject object, String newMapping) {
        String chunkWorldKey = getChunkWorldKey(object.getLocation().getChunk());
        LOADED_MAPPINGS.get(getDefaultMapping(chunkWorldKey, object.getObjectIdentifier())).put(object.getUniqueIdentifier(), newMapping);

        // Update database
        CobaltCore.getInstance().getManager(CobaltCore.getInstance(), DataManager.class).getDao(IMappingsDao.class).insertMappingAsync(getDefaultMapping(chunkWorldKey, object.getObjectIdentifier()), object.getUniqueIdentifier(), newMapping);
    }

    // ----- COMMAND INTEGRATION -----

    private static void updateCommand() {
        for (IStorageObject object : DEFAULT_STORAGES.values()) {
            rootObjectCommand.withSubcommand(createSubCommand(object));
        }
        rootObjectCommand.register();
    }

    private static CommandAPICommand createSubCommand(IStorageObject object) {
        CommandAPICommand objectCommand = new CommandAPICommand(object.getObjectIdentifier());

        objectCommand.withPermission("cobalt.core.command.object." + object.getObjectIdentifier());

        // (NOTE: Limitation of the commands will have to be that only currently loaded objects will show up as valid results, as unloaded objects will not be loaded into memory at that point)

        // Create command
        objectCommand.withSubcommand(createCreateCommand(object));
        // Remove command
        objectCommand.withSubcommand(createRemoveCommand(object));
        // Set command
        objectCommand.withSubcommand(createSetCommand(object));
        // Info command
        objectCommand.withSubcommand(createInfoCommand(object));
        // List command
        objectCommand.withSubcommand(createListCommand(object));
        // Mapping command
        objectCommand.withSubcommand(createMappingCommand(object));

        // List item interaction commands
        objectCommand.withSubcommand(createListAddItemCommand(object));
        // TODO: Remove item

        // -- Activatable
        if (object instanceof IActivatableStorageObject activatableStorageObject) {
            objectCommand.withSubcommand(createToggleCommand(activatableStorageObject));
        }

        // -- Activator
        if (object instanceof IActivatorStorageObject activatorStorageObject) {
            objectCommand.withSubcommand(createAddActivatableCommand(activatorStorageObject));
            objectCommand.withSubcommand(createRemoveActivatableCommand(activatorStorageObject));
        }

        return objectCommand;
    }

    // TODO: Replace suggestions with mappings
    private static CommandAPICommand createMappingCommand(IStorageObject object) {
        return new CommandAPICommand("set_mapping")
                .withArguments(new StringArgument("identifier").replaceSuggestions(ArgumentSuggestions.strings(info -> getLoadedObjectsOfTypeStringIds(object.getObjectIdentifier()))))
                .withArguments(new StringArgument("new_mapping"))
                .executes((sender, args) -> {
                    UUID uuid = UUID.fromString((String) args[0]);
                    IStorageObject loadedObject = getLoadedObject(object.getObjectIdentifier(), uuid);
                    if (loadedObject == null) return;

                    String newMapping = (String) args[1];
                    updateMappings(loadedObject, newMapping);

                    // TODO: Command feedback
                });
    }

    private static CommandAPICommand createAddActivatableCommand(IActivatorStorageObject object) {
        return new CommandAPICommand("add_activatable")
                .withArguments(new StringArgument("uuid").replaceSuggestions(ArgumentSuggestions.strings(info -> getLoadedObjectsOfTypeStringIds(object.getObjectIdentifier()))))
                .withArguments(new StringArgument("activatable_uuid").replaceSuggestions(ArgumentSuggestions.strings(info -> getActivatableStorageObjectUUIDs())))
                .executes(((sender, args) -> {
                    UUID uuid = UUID.fromString((String) args[0]);
                    IStorageObject loadedObject = getLoadedObject(object.getObjectIdentifier(), uuid);
                    if (loadedObject == null) return; // TODO: Feedback

                    UUID activatableUUID = UUID.fromString((String) args[1]);
                    if (loadedObject instanceof IActivatorStorageObject activatorStorageObject) {
                        activatorStorageObject.addActivatable(activatableUUID);
                        updateStorageObject(activatorStorageObject);
                    }
                }));
    }

    private static CommandAPICommand createRemoveActivatableCommand(IActivatorStorageObject object) {
        return new CommandAPICommand("remove_activatable")
                .withArguments(new StringArgument("uuid").replaceSuggestions(ArgumentSuggestions.strings(info -> getLoadedObjectsOfTypeStringIds(object.getObjectIdentifier()))))
                .withArguments(new StringArgument("activatable_uuid").replaceSuggestions(ArgumentSuggestions.strings(info -> getActivatableStorageObjectUUIDs())))
                .executes(((sender, args) -> {
                    UUID uuid = UUID.fromString((String) args[0]);
                    IStorageObject loadedObject = getLoadedObject(object.getObjectIdentifier(), uuid);
                    if (loadedObject == null) return; // TODO: Feedback

                    UUID activatableUUID = UUID.fromString((String) args[1]);
                    if (loadedObject instanceof IActivatorStorageObject activatorStorageObject) {
                        activatorStorageObject.removeActivatable(activatableUUID);
                        updateStorageObject(activatorStorageObject);
                    }
                }));
    }

    private static CommandAPICommand createToggleCommand(IActivatableStorageObject object) {
        return new CommandAPICommand("toggle")
                .withArguments(new StringArgument("uuid").replaceSuggestions(ArgumentSuggestions.strings(info -> getLoadedObjectsOfTypeStringIds(object.getObjectIdentifier()))))
                .executes((sender, args) -> {
                    UUID uuid = UUID.fromString((String) args[0]);
                    IStorageObject loadedObject = getLoadedObject(object.getObjectIdentifier(), uuid);
                    if (loadedObject == null) return; // TODO: Feedback

                    if (loadedObject instanceof IActivatableStorageObject activatableStorageObject) {
                        if (activatableStorageObject.isActive()) activatableStorageObject.deactivate();
                        else activatableStorageObject.activate();

                        updateStorageObject(activatableStorageObject);
                    }
                });
    }

    private static CommandAPICommand createInfoCommand(IStorageObject object) {
        return new CommandAPICommand("info")
                .withArguments(new StringArgument("uuid").replaceSuggestions(ArgumentSuggestions.strings(info -> getLoadedObjectsOfTypeStringIds(object.getObjectIdentifier()))))
                .executesPlayer(((sender, args) -> {
                    UUID uuid = UUID.fromString((String) args[0]);
                    IStorageObject loadedObject = getLoadedObject(object.getObjectIdentifier(), uuid);
                    if (loadedObject == null) return; // TODO: Feedback

                    // Send info message
                    LocaleManager localeManager = CobaltCore.getInstance().getManager(CobaltCore.getInstance(), LocaleManager.class);
                    if (localeManager == null) return;
                    localeManager.sendMessage(
                            CobaltCore.getInstance(), sender,
                            "commands.object.header.info",
                            StringPlaceholders.builder()
                                    .addPlaceholder("type", object.getObjectIdentifier())
                                    .build()
                    );

                    // Formatting for these should be done in the method itself for now
                    List<String> infoStrings = loadedObject.getInfoStrings();
                    for (String s : infoStrings) sender.sendMessage(s);
                }));
    }

    private static CommandAPICommand createListCommand(IStorageObject object) {
        return new CommandAPICommand("list")
                .executesPlayer(((sender, args) -> {
                    IStorageObject[] objects = getLoadedObjectsOfType(object.getObjectIdentifier());

                    // Send info message for all objects
                    LocaleManager localeManager = CobaltCore.getInstance().getManager(CobaltCore.getInstance(), LocaleManager.class);
                    if (localeManager == null) return;
                    localeManager.sendMessage(
                            CobaltCore.getInstance(), sender,
                            "commands.object.header.list",
                            StringPlaceholders.builder()
                                    .addPlaceholder("type", object.getObjectIdentifier())
                                    .build()
                    );

                    // List all objects
                    for (IStorageObject storageObject : objects) {
                        localeManager.sendMessage(
                                "", sender,
                                "commands.object.list_item",
                                StringPlaceholders.builder()
                                        .addPlaceholder("uuid", storageObject.getUniqueIdentifier().toString())
                                        .addPlaceholder("location", storageObject.getLocation().toVector())
                                        .build()
                        );
                    }
                }));
    }

    private static CommandAPICommand createCreateCommand(IStorageObject object) {
        return new CommandAPICommand("create")
                .withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION)) // TODO: Possibly create two commands, one that takes block position, one that takes absolute position
                .executes(((sender, args) -> {
                    // Get variables
                    Location location = (Location) args[0];
                    IStorageObject newObject = createStorageObject(object.getObjectIdentifier(), location);
                    if (newObject == null) return;

                    // Send message
                    if (sender instanceof Player player) {
                        LocaleManager localeManager = CobaltCore.getInstance().getManager(CobaltCore.getInstance(), LocaleManager.class);
                        if (localeManager == null) return;
                        localeManager.sendMessage(
                                CobaltCore.getInstance(), player,
                                "commands.object.create",
                                StringPlaceholders.builder()
                                        .addPlaceholder("type", newObject.getObjectIdentifier())
                                        .addPlaceholder("uuid", newObject.getUniqueIdentifier().toString())
                                        .build()
                        );
                    }
                }));
    }

    private static CommandAPICommand createRemoveCommand(IStorageObject object) {
        return new CommandAPICommand("remove")
                .withArguments(new StringArgument("uuid").replaceSuggestions(ArgumentSuggestions.strings(info -> getLoadedObjectsOfTypeStringIds(object.getObjectIdentifier()))))
                .executes(((sender, args) -> {
                    UUID uuid = UUID.fromString((String) args[0]);
                    IStorageObject loadedObject = getLoadedObject(object.getObjectIdentifier(), uuid);
                    removeStorageObject(uuid, object.getObjectIdentifier(), loadedObject.getLocation().getChunk());

                    // Send message
                    if (sender instanceof Player player) {
                        LocaleManager localeManager = CobaltCore.getInstance().getManager(CobaltCore.getInstance(), LocaleManager.class);
                        if (localeManager == null) return;
                        localeManager.sendMessage(
                                CobaltCore.getInstance(), player,
                                "commands.object.remove",
                                StringPlaceholders.builder()
                                        .addPlaceholder("type", loadedObject.getObjectIdentifier())
                                        .addPlaceholder("uuid", uuid.toString())
                                        .build()
                        );
                    }
                }));
    }

    private static CommandAPICommand createListAddItemCommand(IStorageObject object) {
        CommandAPICommand addListItemCommand = new CommandAPICommand("add_list_item");

        for (Argument<?> argument : object.getListCommandArguments()) {
            CommandAPICommand argumentCommand = new CommandAPICommand(argument.getNodeName());
            argumentCommand.withArguments(new StringArgument("uuid").replaceSuggestions(ArgumentSuggestions.strings(info -> getLoadedObjectsOfTypeStringIds(object.getObjectIdentifier()))));
            argumentCommand.withArguments(argument);
            argumentCommand.executes(((sender, args) -> {
                UUID uuid = UUID.fromString((String) args[0]);
                String key = argument.getNodeName();
                Object value = args[1];

                // Add the value
                addStorageObjectListValue(object.getObjectIdentifier(), uuid, key, value);

                // TODO: Send message

            }));
            addListItemCommand.withSubcommand(argumentCommand);
        }

        return addListItemCommand;
    }

    private static CommandAPICommand createListRemoveItemCommand(IStorageObject object) { // TODO
        CommandAPICommand removeListItemCommand = new CommandAPICommand("remove_list_item");

        for (Argument<?> argument : object.getListCommandArguments()) {
            CommandAPICommand argumentCommand = new CommandAPICommand(argument.getNodeName());
            argumentCommand.withArguments(new StringArgument("uuid").replaceSuggestions(ArgumentSuggestions.strings(info -> getLoadedObjectsOfTypeStringIds(object.getObjectIdentifier()))));
            argumentCommand.withArguments(argument);
            argumentCommand.executes(((sender, args) -> {
                UUID uuid = UUID.fromString((String) args[0]);
                String key = argument.getNodeName();
                Object value = args[1];

                // Add the value
                removeStorageObjectListValue(object.getObjectIdentifier(), uuid, key, value);

                // TODO: Send message

            }));
            removeListItemCommand.withSubcommand(argumentCommand);
        }

        return removeListItemCommand;
    }

    private static CommandAPICommand createSetCommand(IStorageObject object) {
        CommandAPICommand setCommand = new CommandAPICommand("set");

        for (Argument<?> argument : object.getCommandArguments()) {
            CommandAPICommand argumentCommand = new CommandAPICommand(argument.getNodeName());
            argumentCommand.withArguments(new StringArgument("uuid").replaceSuggestions(ArgumentSuggestions.strings(info -> getLoadedObjectsOfTypeStringIds(object.getObjectIdentifier()))));
            argumentCommand.withArguments(argument);
            argumentCommand.executes(((sender, args) -> {

                // Get arguments
                UUID objectUUID = UUID.fromString((String) args[0]);
                String key = argument.getNodeName();
                Object value = args[1];

                // Set the value
                setStorageObjectValue(object.getObjectIdentifier(), objectUUID, key, value);

                // Send message
                if (sender instanceof Player player) {
                    LocaleManager localeManager = CobaltCore.getInstance().getManager(CobaltCore.getInstance(), LocaleManager.class);
                    if (localeManager == null) return;
                    localeManager.sendMessage(
                            CobaltCore.getInstance(), player,
                            "commands.object.set",
                            StringPlaceholders.builder()
                                    .addPlaceholder("key", key)
                                    .addPlaceholder("type", object.getObjectIdentifier())
                                    .addPlaceholder("value", value)
                                    .build()
                    );
                }

            }));
            setCommand.withSubcommand(argumentCommand);
        }

        return setCommand;
    }

    // ----- GETTERS / SETTERS -----

    // TODO: Implement methods
    /*
    public static UUID getUUIDFromString(String value) {
        if (NAME_UUID_MAPPINGS.containsKey(value)) return NAME_UUID_MAPPINGS.get(value);
        else return UUID.fromString(value);
    }

    public static String[] getNameFromUUIDs(String[] uuids) {
        List<String> names = new ArrayList<>();
        for (String id : uuids) {
            String name = UUID_NAME_MAPPINGS.get(UUID.fromString(id));
            if (name == null) continue;
            names.add(name);
        }
        return names.toArray(new String[0]);
    }
     */

    public static String[] getActivatableStorageObjectUUIDs() {
        List<String> uuids = new ArrayList<>();
        for (Map<String, Map<UUID, IStorageObject>> typeMap : LOADED_STORAGES.values()) {
            for (Map<UUID, IStorageObject> uuidMap : typeMap.values()) {
                for (IStorageObject obj : uuidMap.values()) {
                    if (obj instanceof IActivatableStorageObject aObj) uuids.add(aObj.getUniqueIdentifier().toString());
                }
            }
        }
        return uuids.toArray(new String[0]);
    }

    public static IActivatableStorageObject getLoadedActivatableObject(UUID uuid) {
        for (Map<String, Map<UUID, IStorageObject>> typeMap : LOADED_STORAGES.values()) {
            for (Map<UUID, IStorageObject> uuidMap : typeMap.values()) {
                IStorageObject obj = uuidMap.get(uuid);
                if (obj == null) continue;
                if (obj instanceof IActivatableStorageObject aObj) return aObj;
            }
        }
        return null;
    }

    public static IActivatorStorageObject getLoadedActivatorObject(UUID uuid) {
        for (Map<String, Map<UUID, IStorageObject>> typeMap : LOADED_STORAGES.values()) {
            for (Map<UUID, IStorageObject> uuidMap : typeMap.values()) {
                IStorageObject obj = uuidMap.get(uuid);
                if (obj == null) continue;
                if (obj instanceof IActivatorStorageObject aObj) return aObj;
            }
        }
        return null;
    }

    /**
     * Gets all loaded objects of the given type.
     *
     * @param type the type of the objects to find.
     * @return an array of <code>IStorageObject</code>'s.
     */
    public static IStorageObject[] getLoadedObjectsOfType(String type) {
        List<IStorageObject> objects = new ArrayList<>();
        for (Map<String, Map<UUID, IStorageObject>> typeMap : LOADED_STORAGES.values()) {
            Map<UUID, IStorageObject> storageObjectMap = typeMap.get(type);
            if (storageObjectMap != null) objects.addAll(storageObjectMap.values());
        }
        return objects.toArray(new IStorageObject[0]);
    }

    /**
     * Gets all loaded objects.
     *
     * @return an array of <code>IStorageObject</code>'s.
     */
    public static IStorageObject[] getLoadedObjects() {
        List<IStorageObject> objects = new ArrayList<>();
        for (Map<String, Map<UUID, IStorageObject>> typeMap : LOADED_STORAGES.values()) {
            for (Map<UUID, IStorageObject> uuidMap : typeMap.values()) {
                objects.addAll(uuidMap.values());
            }
        }
        return objects.toArray(new IStorageObject[0]);
    }

    /**
     * Gets loaded <code>IStorageObject</code> of type and with a <code>UUID</code>, or null if object was not found.
     *
     * @param type the type of the <code>IStorageObject</code>.
     * @param uuid the <code>UUID</code> of the <code>IStorageObject</code>.
     * @return the <code>IStorageObject</code>, or null if not found.
     */
    public static IStorageObject getLoadedObject(String type, UUID uuid) {
        for (Map<String, Map<UUID, IStorageObject>> typeMap : LOADED_STORAGES.values()) {
            Map<UUID, IStorageObject> storageObjectMap = typeMap.get(type);
            if (storageObjectMap == null) continue;
            IStorageObject object = storageObjectMap.get(uuid);
            if (object != null) return object;
        }
        return null;
    }

    public static String[] getLoadedObjectsOfTypeStringIds(String type) {
        IStorageObject[] objects = getLoadedObjectsOfType(type);
        String[] objectUUIDs = new String[objects.length];
        for (int i = 0; i < objectUUIDs.length; i++) objectUUIDs[i] = objects[i].getUniqueIdentifier().toString();
        return objectUUIDs;
    }

    public static IStorageObject getDefaultStorage(String storageName) {
        return DEFAULT_STORAGES.get(storageName);
    }

    /**
     * Combines a <code>Chunk</code> & the <code>World</code> the <code>Chunk</code> is in into a single key.
     *
     * @param chunk the <code>Chunk</code> to get the kye from.
     * @return the key.
     */
    public static String getChunkWorldKey(Chunk chunk) {
        return chunk.getChunkKey() + ":" + chunk.getWorld().getUID();
    }

}
