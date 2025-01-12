package nl.matsgemmeke.battlegrounds.item.data;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ParticleEffect(
        @NotNull Particle particle,
        int count,
        double offsetX,
        double offsetY,
        double offsetZ,
        double extra,
        @Nullable Material blockDataMaterial
) { }
