package nl.matsgemmeke.battlegrounds.item.projectile.effect.trail;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.BaseProjectileEffect;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class TrailEffect extends BaseProjectileEffect {

    @NotNull
    private final ParticleEffectSpawner particleEffectSpawner;
    @NotNull
    private final TrailProperties properties;

    @Inject
    public TrailEffect(@NotNull ParticleEffectSpawner particleEffectSpawner, @Assisted @NotNull TrailProperties properties) {
        this.particleEffectSpawner = particleEffectSpawner;
        this.properties = properties;
    }

    public void performEffect(@NotNull Projectile projectile) {
        Location location = projectile.getLocation();
        World world = projectile.getWorld();

        particleEffectSpawner.spawnParticleEffect(properties.particleEffect(), world, location);
    }
}
