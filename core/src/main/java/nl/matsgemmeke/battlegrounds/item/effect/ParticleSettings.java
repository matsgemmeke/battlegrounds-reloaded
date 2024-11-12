package nl.matsgemmeke.battlegrounds.item.effect;

import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

public record ParticleSettings(
        @NotNull Particle type,
        int count,
        double offsetX,
        double offsetY,
        double offsetZ,
        double extra
) { }
