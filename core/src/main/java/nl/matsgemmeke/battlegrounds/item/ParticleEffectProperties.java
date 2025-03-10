package nl.matsgemmeke.battlegrounds.item;

import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

public record ParticleEffectProperties(
        @NotNull Particle type,
        int count,
        double offsetX,
        double offsetY,
        double offsetZ,
        double extra
) {}
