package nl.matsgemmeke.battlegrounds.game.component.projectile;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

public record ProjectileHitResult(@Nullable Block hitBlock, @Nullable Entity hitEntity) {
}
