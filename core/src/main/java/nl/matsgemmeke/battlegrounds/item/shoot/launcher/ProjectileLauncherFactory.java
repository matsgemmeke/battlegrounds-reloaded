package nl.matsgemmeke.battlegrounds.item.shoot.launcher;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.ProjectileSpec;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.damage.DamageEffectNew;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball.FireballLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball.FireballProperties;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan.HitscanLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan.HitscanProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProjectileLauncherFactory {

    @NotNull
    private final FireballLauncherFactory fireballLauncherFactory;
    @NotNull
    private final HitscanLauncherFactory hitscanLauncherFactory;
    @NotNull
    private final ItemEffectFactory itemEffectFactory;
    @NotNull
    private final ParticleEffectMapper particleEffectMapper;
    @NotNull
    private final Provider<DamageEffectNew> damageEffectProvider;

    @Inject
    public ProjectileLauncherFactory(
            @NotNull FireballLauncherFactory fireballLauncherFactory,
            @NotNull HitscanLauncherFactory hitscanLauncherFactory,
            @NotNull ItemEffectFactory itemEffectFactory,
            @NotNull ParticleEffectMapper particleEffectMapper,
            @NotNull Provider<DamageEffectNew> damageEffectProvider
    ) {
        this.fireballLauncherFactory = fireballLauncherFactory;
        this.hitscanLauncherFactory = hitscanLauncherFactory;
        this.itemEffectFactory = itemEffectFactory;
        this.particleEffectMapper = particleEffectMapper;
        this.damageEffectProvider = damageEffectProvider;
    }

    public ProjectileLauncher create(ProjectileSpec spec) {
        ProjectileLauncherType projectileLauncherType = ProjectileLauncherType.valueOf(spec.type);

        switch (projectileLauncherType) {
            case FIREBALL -> {
                List<GameSound> shotSounds = DefaultGameSound.parseSounds(spec.shotSounds);
                ParticleEffect trajectoryParticleEffect = this.createParticleEffect(spec.trajectoryParticleEffect);
                double velocity = this.validateSpecVar(spec.velocity, "velocity", projectileLauncherType);

                FireballProperties properties = new FireballProperties(shotSounds, trajectoryParticleEffect, velocity);
                ItemEffect itemEffect = itemEffectFactory.create(spec.effect);

                return fireballLauncherFactory.create(properties, itemEffect);
            }
            case HITSCAN -> {
                List<GameSound> shotSounds = DefaultGameSound.parseSounds(spec.shotSounds);
                ParticleEffect trajectoryParticleEffect = null;
                ParticleEffectSpec trajectoryParticleEffectSpec = spec.trajectoryParticleEffect;

                if (trajectoryParticleEffectSpec != null) {
                    trajectoryParticleEffect = particleEffectMapper.map(trajectoryParticleEffectSpec);
                }

                HitscanProperties properties = new HitscanProperties(shotSounds, trajectoryParticleEffect);

                DamageEffectNew itemEffect = damageEffectProvider.get();
                itemEffect.setDamageType(DamageType.BULLET_DAMAGE);
                itemEffect.setRangeProfile(new RangeProfile(50.0, 10.0, 30.0, 30.0, 10.0, 50.0));

                return hitscanLauncherFactory.create(properties, itemEffect);
            }
            default -> throw new ProjectileLauncherCreationException("Invalid projectile launcher type '%s'".formatted(projectileLauncherType));
        }
    }

    @NotNull
    private <T> T validateSpecVar(@Nullable T value, @NotNull String valueName, @NotNull Object projectileLauncherType) {
        if (value == null) {
            throw new ProjectileLauncherCreationException("Cannot create projectile launcher for type %s because of invalid spec: Required '%s' value is missing".formatted(projectileLauncherType, valueName));
        }

        return value;
    }

    @Nullable
    private ParticleEffect createParticleEffect(@Nullable ParticleEffectSpec spec) {
        ParticleEffect particleEffect = null;

        if (spec != null) {
            particleEffect = particleEffectMapper.map(spec);
        }

        return particleEffect;
    }
}
