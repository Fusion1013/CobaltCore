package se.fusion1013.cobaltCore.shape.tesseract;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.util.Vector;

public record Face(Vertex... vertices) {

  public List<Vector> getParticles(Vector relative) {
    List<Vector> containers = new ArrayList<>();
    for (Vertex vertex : this.vertices) {
      Vector loc2 = relative.clone().add(new Vector(vertex.getX(), vertex.getY(), vertex.getZ()));
      containers.add(loc2);
    }
    return containers;
  }
}
