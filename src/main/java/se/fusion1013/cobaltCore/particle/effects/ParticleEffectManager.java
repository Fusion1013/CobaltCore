package se.fusion1013.cobaltCore.particle.effects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Particle;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.particle.effects.glyph.ParticleEffectGlyph;
import se.fusion1013.cobaltCore.particle.effects.glyph.ParticleEffectText;
import se.fusion1013.cobaltCore.particle.effects.primitive.*;
import se.fusion1013.cobaltCore.shape.ShapeUtils;

public class ParticleEffectManager extends Manager<CobaltCore> {

  private static final Map<String, IParticleEffect> PARTICLE_EFFECTS =
      new HashMap<>(); // Particle styles that a player has created through the in-game command, or
  // through file loading
  private static final Map<String, IParticleEffect> DEFAULT_PARTICLE_EFFECTS =
      new HashMap<>(); // Registered, default styles. Used for debugging

  public ParticleEffectManager(CobaltCore plugin) {
    super(plugin);
  }

  @Override
  public void reload() {}

  @Override
  public void disable() {}

  public static boolean createParticleEffect(String effectType, String customEffectName) {
    if (customEffectExists(effectType)) {
      return false;
    }

    IParticleEffect defaultEffect = DEFAULT_PARTICLE_EFFECTS.get(effectType);
    if (defaultEffect == null) {
      return false;
    }

    IParticleEffect newEffect = defaultEffect.copy();
    PARTICLE_EFFECTS.put(customEffectName, newEffect);
    return true;
  }

  public static final IParticleEffect PARTICLE_EFFECT_SPHERE =
      register(new ParticleEffectSphere(Particle.CRIT, 3, 100));
  public static final IParticleEffect PARTICLE_EFFECT_CUBE =
      register(new ParticleEffectCube(Particle.CRIT, 3, 100));
  public static final IParticleEffect PARTICLE_EFFECT_TORUS =
      register(new ParticleEffectTorus(Particle.CRIT, 5, 4, 100));
  public static final IParticleEffect PARTICLE_EFFECT_MORPH =
      register(
          new ParticleEffectMorph(
              ShapeUtils.generateSphere(2, 100),
              ShapeUtils.generateSphere(10, 100),
              Particle.CRIT));
  public static final IParticleEffect PARTICLE_EFFECT_FIBONACCI_SPHERE =
      register(new ParticleEffectFibonacciSphere(Particle.CRIT, 3, 100));
  public static final IParticleEffect PARTICLE_EFFECT_LINE =
      register(new ParticleEffectLine(Particle.CRIT));
  public static final IParticleEffect PARTICLE_EFFECT_SPIRAL =
      register(new ParticleEffectSpiral(Particle.CRIT));
  public static final IParticleEffect PARTICLE_EFFECT_GLYPH =
      register(new ParticleEffectGlyph(Particle.CRIT));
  public static final IParticleEffect PARTICLE_EFFECT_TEXT =
      register(new ParticleEffectText(Particle.CRIT));
  public static final IParticleEffect PARTICLE_EFFECT_CIRCLE =
      register(new ParticleEffectCircle(Particle.CRIT));
  public static final IParticleEffect PARTICLE_EFFECT_TESSERACT =
      register(new ParticleEffectTesseract(Particle.CRIT));
  public static final IParticleEffect PARTICLE_EFFECT_SPIROGRAPH =
      register(new ParticleEffectSpirograph(Particle.CRIT));
  public static final IParticleEffect PARTICLE_EFFECT_3DSPIROGRAPH =
      register(new ParticleEffect3DSpirograph(Particle.CRIT));
  public static final IParticleEffect PARTICLE_EFFECT_SPIRAL_CONNECTOR =
      register(new ParticleEffectSpiralConnector(Particle.CRIT));
  public static final IParticleEffect PARTICLE_EFFECT_BEZIER =
      register(new ParticleEffectBezier(Particle.CRIT));

  private static IParticleEffect register(IParticleEffect effect) {
    DEFAULT_PARTICLE_EFFECTS.put(effect.getName(), effect);
    return effect;
  }

  /**
   * Returns a custom effect with the given identifier.
   *
   * @param name the identifier of the effect.
   * @return a custom effect, or null.
   */
  public static IParticleEffect getCustomEffect(String name) {
    return PARTICLE_EFFECTS.get(name);
  }

  public static String[] getDefaultParticleEffectNames() {
    List<String> effectNames = new ArrayList<>();
    for (IParticleEffect effect : DEFAULT_PARTICLE_EFFECTS.values())
      effectNames.add(effect.getName());
    return effectNames.toArray(new String[0]);
  }

  public static String[] getCustomParticleEffectNames() {
    return PARTICLE_EFFECTS.keySet().toArray(new String[0]);
  }

  public static IParticleEffect getParticleEffect(String effectName) {
    return DEFAULT_PARTICLE_EFFECTS.get(effectName);
  }

  public static boolean customEffectExists(String effectName) {
    return PARTICLE_EFFECTS.containsKey(effectName);
  }
}
