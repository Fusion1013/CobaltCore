package se.fusion1013.plugin.cobaltcore.particle.styles;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.ParticleContainer;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParticleStyleTesseract extends ParticleStyle {

    // ----- VARIABLES -----

    double rx = 0;
    double ry = 0;
    double rz = 0;
    double rw = 0;

    double dx = 0;
    double dy = 0;
    double dz = 0;
    double dw = 0;

    int density = 6;
    int width = 10;

    public ParticleStyleTesseract(String name, Particle particle, Vector offset, int count, double speed, Object extra) {
        super("tesseract", name, particle, offset, count, speed, extra);
    }

    public ParticleStyleTesseract(String name) {
        super("tesseract", name);
    }

    // ----- INFO -----

    @Override
    public List<String> getInfoStrings() {
        List<String> info = super.getInfoStrings();
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("rx", rx)
                .addPlaceholder("ry", ry)
                .addPlaceholder("rz", rz)
                .addPlaceholder("rw", rw)
                .addPlaceholder("dx", dx)
                .addPlaceholder("dy", dy)
                .addPlaceholder("dz", dz)
                .addPlaceholder("dw", dw)
                .addPlaceholder("density", density)
                .addPlaceholder("width", width)
                .build();
        info.add(LocaleManager.getInstance().getLocaleMessage("particle.style.tesseract.info", placeholders));
        return info;
    }


    // ----- SET EXTRA SETTINGS -----

    @Override
    public void setExtraSetting(String key, Object value) {
        switch (key) {
            case "rx" -> rx = (double) value;
            case "ry" -> ry = (double) value;
            case "rz" -> rz = (double) value;
            case "rw" -> rw = (double) value;
            case "dx" -> dx = (double) value;
            case "dy" -> dy = (double) value;
            case "dz" -> dz = (double) value;
            case "dw" -> dw = (double) value;
            case "density" -> density = (int) value;
            case "width" -> width = (int) value;
        }
    }

    @Override
    public Argument[] getExtraSettingsArguments() {
        List<Argument> arguments = new ArrayList<>();
        arguments.add(new DoubleArgument("rx"));
        arguments.add(new DoubleArgument("ry"));
        arguments.add(new DoubleArgument("rz"));
        arguments.add(new DoubleArgument("rw"));
        arguments.add(new DoubleArgument("dx"));
        arguments.add(new DoubleArgument("dy"));
        arguments.add(new DoubleArgument("dz"));
        arguments.add(new DoubleArgument("dw"));
        arguments.add(new IntegerArgument("density"));
        arguments.add(new IntegerArgument("width"));
        return arguments.toArray(new Argument[0]);
    }

    @Override
    public void setExtraSettings(Object[] args) {
        super.setExtraSettings(args);

        rx = (double) args[0];
        ry = (double) args[1];
        rz = (double) args[2];
        rw = (double) args[3];
        dx = (double) args[4];
        dy = (double) args[5];
        dz = (double) args[6];
        dw = (double) args[7];
        density = (int) args[8];
        width = (int) args[9];
    }

    @Override
    public String getExtraSettings() {
        JsonObject jo = new JsonObject();
        jo.addProperty("rx", rx);
        jo.addProperty("ry", ry);
        jo.addProperty("rz", rz);
        jo.addProperty("rw", rw);
        jo.addProperty("dx", dx);
        jo.addProperty("dy", dy);
        jo.addProperty("dz", dz);
        jo.addProperty("dw", dw);
        jo.addProperty("density", density);
        jo.addProperty("width", width);
        return jo.toString();
    }

    @Override
    public void setExtraSettings(String extra) {
        JsonObject jsonObject = new Gson().fromJson(extra, JsonObject.class);
        rx = jsonObject.get("rx").getAsDouble();
        ry = jsonObject.get("ry").getAsDouble();
        rz = jsonObject.get("rz").getAsDouble();
        rw = jsonObject.get("rw").getAsDouble();
        dx = jsonObject.get("dx").getAsDouble();
        dy = jsonObject.get("dy").getAsDouble();
        dz = jsonObject.get("dz").getAsDouble();
        dw = jsonObject.get("dw").getAsDouble();
        density = jsonObject.get("density").getAsInt();
        width = jsonObject.get("width").getAsInt();
    }

    // ----- PARTICLE GETTERS -----

    @Override
    public ParticleContainer[] getParticleContainers(Location location) {

        // ry = (ry-(0.012 / 10))%(Math.PI*2);
        // rw = (rw-(0.1 / 10))%(Math.PI*2);

        rx = (rx-dx)%(Math.PI);
        ry = (ry-dy)%(Math.PI);
        rz = (rz-dz)%(Math.PI);
        rw = (rw-dw)%(Math.PI);

        double w = width;

        Vertex[] v = new Vertex[16];

        v[0] = new Vertex(-w / 2, w / 2, -w / 2, w / 2);
        v[1] = new Vertex(w / 2, w / 2, -w / 2, w / 2);
        v[2] = new Vertex(w / 2, w / 2, w / 2, w / 2);
        v[3] = new Vertex(-w / 2, w / 2, w / 2, w / 2);
        v[4] = new Vertex(-w / 2, -w / 2, -w / 2, w / 2);
        v[5] = new Vertex(w / 2, -w / 2, -w / 2, w / 2);
        v[6] = new Vertex(w / 2, -w / 2, w / 2, w / 2);
        v[7] = new Vertex(-w / 2, -w / 2, w / 2, w / 2);
        v[8] = new Vertex(-w / 2, w / 2, -w / 2, -w / 2);
        v[9] = new Vertex(w / 2, w / 2, -w / 2, -w / 2);
        v[10] = new Vertex(w / 2, w / 2, w / 2, -w / 2);
        v[11] = new Vertex(-w / 2, w / 2, w / 2, -w / 2);
        v[12] = new Vertex(-w / 2, -w / 2, -w / 2, -w / 2);
        v[13] = new Vertex(w / 2, -w / 2, -w / 2, -w / 2);
        v[14] = new Vertex(w / 2, -w / 2, w / 2, -w / 2);
        v[15] = new Vertex(-w / 2, -w / 2, w / 2, -w / 2);

        for (int i = 0; i < v.length; i++) {
            if (Math.abs(rx)+Math.abs(ry)+Math.abs(rz)+Math.abs(rw) > 0) v[i].rotate(rx, ry, rz, rw);
            v[i].project();
        }

        Face[] faces = new Face[12];
        faces[0] = (new Face(v[0], v[1], v[2], v[3]));
        faces[1] = (new Face(v[4], v[7], v[6], v[5]));
        faces[2] = (new Face(v[0], v[4], v[5], v[1]));
        faces[3] = (new Face(v[2], v[6], v[7], v[3]));
        faces[4] = (new Face(v[8], v[9], v[10], v[11]));
        faces[5] = (new Face(v[12], v[15], v[14], v[13]));
        faces[6] = (new Face(v[8], v[12], v[13], v[9]));
        faces[7] = (new Face(v[10], v[14], v[15], v[11]));
        faces[8] = (new Face(v[0], v[1], v[9], v[8]));
        faces[9] = (new Face(v[2], v[3], v[11], v[10]));
        faces[10] = (new Face(v[4], v[7], v[15], v[12]));
        faces[11] = (new Face(v[6], v[5], v[13], v[14]));

        // Get Vertex locations
        List<ParticleContainer> containers = new ArrayList<>();

        // --- Horizontal planes
        // Upper
        addContainerLine(containers, v, location, 0, 1);
        addContainerLine(containers, v, location, 1, 2);
        addContainerLine(containers, v, location, 2, 3);
        addContainerLine(containers, v, location, 3, 0);

        // Bottom
        addContainerLine(containers, v, location, 4, 5);
        addContainerLine(containers, v, location, 5, 6);
        addContainerLine(containers, v, location, 6, 7);
        addContainerLine(containers, v, location, 7, 4);

        // Upper middle
        addContainerLine(containers, v, location, 8, 9);
        addContainerLine(containers, v, location, 9, 10);
        addContainerLine(containers, v, location, 10, 11);
        addContainerLine(containers, v, location, 11, 8);

        // Bottom middle
        addContainerLine(containers, v, location, 12, 13);
        addContainerLine(containers, v, location, 13, 14);
        addContainerLine(containers, v, location, 14, 15);
        addContainerLine(containers, v, location, 15, 12);

        // --- Connect upper plane
        addContainerLine(containers, v, location, 0, 8);
        addContainerLine(containers, v, location, 1, 9);
        addContainerLine(containers, v, location, 2, 10);
        addContainerLine(containers, v, location, 3, 11);

        addContainerLine(containers, v, location, 0, 4);
        addContainerLine(containers, v, location, 1, 5);
        addContainerLine(containers, v, location, 2, 6);
        addContainerLine(containers, v, location, 3, 7);

        // --- Connect lower plane

        addContainerLine(containers, v, location, 12, 4);
        addContainerLine(containers, v, location, 13, 5);
        addContainerLine(containers, v, location, 14, 6);
        addContainerLine(containers, v, location, 15, 7);

        // --- Connect middle planes
        addContainerLine(containers, v, location, 8, 12);
        addContainerLine(containers, v, location, 9, 13);
        addContainerLine(containers, v, location, 10, 14);
        addContainerLine(containers, v, location, 11, 15);


        // for (Face face : faces) containers.addAll(face.getParticles(location));

        return containers.toArray(new ParticleContainer[0]);
    }

    private void addContainerLine(List<ParticleContainer> containers, Vertex[] v, Location location, int from, int to) {
        ParticleStyleLine line = new ParticleStyleLine("tesseract", Particle.END_ROD, new Vector(0, 0, 0), 1, 0, null);
        line.setDensity(density);
        containers.addAll(Arrays.asList(line.getParticles(location.clone().add(new Vector(v[from].x, v[from].y, v[from].z)), location.clone().add(new Vector(v[to].x, v[to].y, v[to].z)))));
    }

    @Override
    public ParticleContainer[] getParticleContainers(Location location1, Location location2) {
        return new ParticleContainer[0];
    }

    // ----- FACE -----

    private static class Face {

        Vertex[] vertices;

        boolean noCull;

        public Face(Vertex... vertices) {
            this.vertices = vertices;
        }

        public List<ParticleContainer> getParticles(Location relative) {
            List<ParticleContainer> containers = new ArrayList<>();
            for (int i = 0; i < this.vertices.length; i++) {

                Location loc2 = relative.clone().add(new Vector(vertices[i].x, vertices[i].y, vertices[i].z));
                containers.add(new ParticleContainer(loc2, 0, 0, 0, 0, 1));

                //Location loc1 = relative.clone().add(new Vector(vertices[i-1].x, vertices[i-1].y, vertices[i-1].z));
                //Location loc2 = relative.clone().add(new Vector(vertices[i].x, vertices[i].y, vertices[i].z));

                //ParticleStyleLine line = new ParticleStyleLine("tesseract", Particle.END_ROD, new Vector(0, 0, 0), 1, 0, null);
                //containers.addAll(Arrays.asList(line.getParticles(loc1, loc2)));
            }
            return containers;
        }

    }

    // ----- VERTEX -----

    private static class Vertex {

        double x;
        double y;
        double z;
        double w;

        public Vertex(double x, double y, double z, double w) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
        }

        public void rotate(double xr, double yr, double zr, double wr) {
            // 4D rotation on YW axis
            double yy = y;
            y = yy * Math.cos(wr) - w * Math.sin(wr);
            w = yy * Math.sin(wr) + w * Math.cos(wr);
            // Constants
            double x = this.x;
            double y = this.y;
            double z = this.z;
            // Rotation Data
            double sx = Math.sin(xr);
            double sy = Math.sin(yr);
            double sz = Math.sin(zr);
            double cx  = Math.cos(xr);
            double cy = Math.cos(yr);
            double cz = Math.cos(zr);
            // Repeating parts of equation
            double eq1 = sz*y+cz*x;
            double eq2 = cz*y-sz*x;
            double eq3 = cy*z+sy*eq1;
            //Applying Transformations
            this.x = cy*eq1-sy*z;
            this.y = sx*eq3+cx*eq2;
            this.z = cx*eq3-sx*eq2;
        }

        public void project() {
            // Projects 4D to 3D
            double focalLength = 35.0;
            double cw = focalLength*focalLength;
            w -= (cw)/(focalLength);
            x = -x/w*focalLength;
            y = -y/w*focalLength;
            z = -z/w*focalLength;
        }
    }

    // ----- CLONE METHOD & CONSTRUCTOR -----

    public ParticleStyleTesseract(ParticleStyleTesseract target) {
        super(target);

        this.rx = target.rx;
        this.ry = target.ry;
        this.rz = target.rz;
        this.rw = target.rw;
    }

    @Override
    public ParticleStyleTesseract clone() {
        return new ParticleStyleTesseract(this);
    }
}
