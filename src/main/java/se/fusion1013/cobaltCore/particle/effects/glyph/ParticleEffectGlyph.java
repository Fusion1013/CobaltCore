package se.fusion1013.cobaltCore.particle.effects.glyph;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.commands.system.ICommandValue;
import se.fusion1013.cobaltCore.particle.effects.AbstractParticleEffect;
import se.fusion1013.cobaltCore.particle.effects.IParticleEffect;
import se.fusion1013.cobaltCore.particle.transformation.TransformationPipeline;
import se.fusion1013.cobaltCore.variable.DoubleVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;

import java.util.List;

public class ParticleEffectGlyph extends AbstractParticleEffect implements IParticleEffect {

    private final DoubleVariable scale = new DoubleVariable("scale");
    private final StringVariable glyph = new StringVariable("glyph")
            .onValueChange(k -> updateGlyphData())
            .suggestions(() -> GlyphManager.getGlyphNamespaces().toArray(new String[0]));

    private GlyphData glyphData;


    public ParticleEffectGlyph(Particle particle) {
        super("glyph", particle, new TransformationPipeline());
        updateGlyphData();
    }

    private void updateGlyphData() {
        this.glyphData = GlyphManager.getGlyphFromName(glyph.getValue());
    }

    @Override
    public void display(Location center, Player player, TransformationPipeline extraPipeline) {
        display(center, player, particle.getParticle(), getPoints().stream().map(p -> new Vector(p.getX() * scale.getValue(), p.getY() * scale.getValue(), p.getZ() * scale.getValue())).toList(), extraPipeline);
    }

    @Override
    public List<Vector> getPoints() {
        if (glyphData == null) return List.of();
        return glyphData.positions();
    }

    @Override
    public ICommandValue[] getValues() {
        return new ICommandValue[]{particle, scale, glyph};
    }
}
