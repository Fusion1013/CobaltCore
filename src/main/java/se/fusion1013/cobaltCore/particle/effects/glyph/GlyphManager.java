package se.fusion1013.cobaltCore.particle.effects.glyph;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.util.FileUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlyphManager extends Manager<CobaltCore> {

    private static final Map<String, GlyphData> GLYPHS = new HashMap<>();
    private static final Map<String, GlyphAlphabet> ALPHABETS = new HashMap<>();

    private static void loadAlphabets() {
        String[] alphabets = FileUtil.getResources(CobaltCore.class, "alphabet");
        for (String s : alphabets) {
            GlyphAlphabet alphabet = getAlphabet(s);
            if (alphabet == null) continue;
            ALPHABETS.put(alphabet.identifier(), alphabet);
        }
    }

    private static GlyphAlphabet getAlphabet(String path) {
        if (!path.endsWith(".json")) return null;

        File alphabetFile = FileUtil.getOrCreateFileFromResource(CobaltCore.getInstance(), "alphabet/" + path);
        try {
            return loadGlyphAlphabet(alphabetFile);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Gson GSON = new Gson();

    public static GlyphAlphabet loadGlyphAlphabet(File file)
            throws IOException, ParseException {

        JSONParser parser = new JSONParser();

        // Parse with json-simple
        JSONObject json = (JSONObject) parser.parse(new FileReader(file));

        // Extract "characters" section
        JSONArray charactersArray = (JSONArray) json.get("characters");

        // Convert using Gson
        Type listType = new TypeToken<List<GlyphAlphabetCharacter>>(){}.getType();
        List<GlyphAlphabetCharacter> characters = GSON.fromJson(charactersArray.toJSONString(), listType);

        // Use filename (without extension) as identifier, or change as needed
        String identifier = file.getName().replaceFirst("[.][^.]+$", "");

        return new GlyphAlphabet(identifier, characters);
    }

    private static void loadGlyphs() {
        GLYPHS.clear();
        String[] glyphs = FileUtil.getResources(CobaltCore.class, "glyphs");
        for (String s : glyphs) {
            GlyphData glyphData = getGlyph(s);
            if (glyphData == null) continue;
            GLYPHS.put(glyphData.category() + "." + glyphData.name(), glyphData);
        }
    }

    private static GlyphData getGlyph(String path) {
        if (!path.endsWith(".png")) return null;

        File glyphFile = FileUtil.getOrCreateFileFromResource(CobaltCore.getInstance(), "glyphs/" + path);

        String[] splitPath = path.split("/");
        String category = splitPath[0];
        String name = splitPath[1].replace(".png", "");

        List<Vector> positions = new ArrayList<>();

        int width = 0;

        try {
            BufferedImage img = ImageIO.read(glyphFile);

            // Image dimensions
            width = img.getWidth();
            int height = img.getHeight();

            double xOffset = width / 2.0f;
            double yOffset = height / 2.0f;

            // Scaling factor – how far apart particles are

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {

                    int argb = img.getRGB(x, y);
                    int alpha = (argb >> 24) & 0xff;

                    // Skip transparent pixels
                    if (alpha < 10) continue;

                    // Compute world position
                    double px = + (x - xOffset);
                    double py = + -(y - yOffset);

                    Vector position = new Vector(px + 0.5f, py - 0.5f, 0);
                    positions.add(position);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new GlyphData(category, name, positions, width);
    }

    public static GlyphData getGlyphFromName(String namespace) {
        return GLYPHS.get(namespace);
    }

    public static List<String> getGlyphCategories() {
        return GLYPHS.values().stream().map(gd -> gd.category()).toList();
    }

    public static List<String> getGlyphNames(String category) {
        return GLYPHS.values().stream().filter(gd -> gd.category().equalsIgnoreCase(category)).map(gd -> gd.name()).toList();
    }

    public static List<String> getGlyphNamespaces() {
        return GLYPHS.values().stream().map(gd -> gd.category() + "." + gd.name()).sorted().toList();
    }

    public static List<String> getAlphabetNames() {
        return ALPHABETS.values().stream().map(GlyphAlphabet::identifier).toList();
    }

    public static GlyphData getGlyph(String alphabetName, char character) {
        GlyphAlphabet alphabet = ALPHABETS.get(alphabetName);
        return alphabet.getGlyph(character);
    }

    public GlyphManager(CobaltCore plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        loadGlyphs();
        loadAlphabets();
    }

    @Override
    public void disable() {

    }

    public static GlyphManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = CobaltCore.getInstance().getManager(CobaltCore.getInstance(), GlyphManager.class);
        }
        return INSTANCE;
    }
    private static GlyphManager INSTANCE;
}
