package nl.matsgemmeke.battlegrounds.item.effect;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.*;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.PotionEffectProperties;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionEffectNew;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionProperties;
import nl.matsgemmeke.battlegrounds.item.effect.damage.DamageEffectNew;
import nl.matsgemmeke.battlegrounds.item.effect.damage.DamageProperties;
import nl.matsgemmeke.battlegrounds.item.effect.explosion.ExplosionEffectNew;
import nl.matsgemmeke.battlegrounds.item.effect.explosion.ExplosionProperties;
import nl.matsgemmeke.battlegrounds.item.effect.flash.FlashEffectNew;
import nl.matsgemmeke.battlegrounds.item.effect.flash.FlashProperties;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationEffectNew;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationProperties;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenEffectNew;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenProperties;
import nl.matsgemmeke.battlegrounds.item.effect.sound.SoundNotificationEffectNew;
import nl.matsgemmeke.battlegrounds.item.effect.spawn.MarkSpawnPointEffectNew;
import nl.matsgemmeke.battlegrounds.item.mapper.RangeProfileMapper;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutorFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemEffectFactory {

    private final ParticleEffectMapper particleEffectMapper;
    private final Provider<CombustionEffectNew> combustionEffectProvider;
    private final Provider<DamageEffectNew> damageEffectProvider;
    private final Provider<ExplosionEffectNew> explosionEffectProvider;
    private final Provider<FlashEffectNew> flashEffectProvider;
    private final Provider<GunFireSimulationEffectNew> gunFireSimulationEffectProvider;
    private final Provider<MarkSpawnPointEffectNew> markSpawnPointEffectProvider;
    private final Provider<SmokeScreenEffectNew> smokeScreenEffectProvider;
    private final RangeProfileMapper rangeProfileMapper;
    private final TriggerExecutorFactory triggerExecutorFactory;

    @Inject
    public ItemEffectFactory(
            ParticleEffectMapper particleEffectMapper,
            Provider<CombustionEffectNew> combustionEffectProvider,
            Provider<DamageEffectNew> damageEffectProvider,
            Provider<ExplosionEffectNew> explosionEffectProvider,
            Provider<FlashEffectNew> flashEffectProvider,
            Provider<GunFireSimulationEffectNew> gunFireSimulationEffectProvider,
            Provider<MarkSpawnPointEffectNew> markSpawnPointEffectProvider,
            Provider<SmokeScreenEffectNew> smokeScreenEffectProvider,
            RangeProfileMapper rangeProfileMapper,
            TriggerExecutorFactory triggerExecutorFactory
    ) {
        this.particleEffectMapper = particleEffectMapper;
        this.combustionEffectProvider = combustionEffectProvider;
        this.damageEffectProvider = damageEffectProvider;
        this.explosionEffectProvider = explosionEffectProvider;
        this.flashEffectProvider = flashEffectProvider;
        this.gunFireSimulationEffectProvider = gunFireSimulationEffectProvider;
        this.markSpawnPointEffectProvider = markSpawnPointEffectProvider;
        this.smokeScreenEffectProvider = smokeScreenEffectProvider;
        this.rangeProfileMapper = rangeProfileMapper;
        this.triggerExecutorFactory = triggerExecutorFactory;
    }

    public ItemEffectNew create(ItemEffectSpec spec) {
        ItemEffectType itemEffectType = ItemEffectType.valueOf(spec.type);

        ItemEffectNew itemEffect = switch (itemEffectType) {
            case COMBUSTION -> {
                double minSize = this.validateSpecVar(spec.minSize, "minSize", itemEffectType);
                double maxSize = this.validateSpecVar(spec.maxSize, "maxSize", itemEffectType);
                double growth = this.validateSpecVar(spec.growth, "growth", itemEffectType);
                long growthInterval = this.validateSpecVar(spec.growthInterval, "growthInterval", itemEffectType);
                long minDuration = this.validateSpecVar(spec.minDuration, "minDuration", itemEffectType);
                long maxDuration = this.validateSpecVar(spec.maxDuration, "maxDuration", itemEffectType);
                boolean damageBlocks = this.validateSpecVar(spec.damageBlocks, "damageBlocks", itemEffectType);
                boolean spreadFire = this.validateSpecVar(spec.spreadFire, "spreadFire", itemEffectType);
                List<GameSound> activationSounds = DefaultGameSound.parseSounds(spec.activationSounds);

                RangeProfileSpec rangeProfileSpec = this.validateSpecVar(spec.range, "rangeProfile", itemEffectType);
                RangeProfile rangeProfile = rangeProfileMapper.map(rangeProfileSpec);

                CombustionProperties properties = new CombustionProperties(activationSounds, rangeProfile, minSize, maxSize, growth, growthInterval, minDuration, maxDuration, damageBlocks, spreadFire);

                CombustionEffectNew combustionEffect = combustionEffectProvider.get();
                combustionEffect.setProperties(properties);
                yield combustionEffect;
            }
            case DAMAGE -> {
                String damageTypeValue = this.validateSpecVar(spec.damageType, "damageType", itemEffectType);
                RangeProfileSpec rangeProfileSpec = this.validateSpecVar(spec.range, "rangeProfile", itemEffectType);

                RangeProfile rangeProfile = rangeProfileMapper.map(rangeProfileSpec);
                DamageType damageType = DamageType.valueOf(damageTypeValue);

                DamageProperties properties = new DamageProperties(rangeProfile, damageType);

                DamageEffectNew damageEffect = damageEffectProvider.get();
                damageEffect.setProperties(properties);
                yield damageEffect;
            }
            case EXPLOSION -> {
                RangeProfileSpec rangeProfileSpec = this.validateSpecVar(spec.range, "rangeProfile", itemEffectType);
                float power = this.validateSpecVar(spec.power, "power", itemEffectType);
                boolean damageBlocks = this.validateSpecVar(spec.damageBlocks, "damageBlocks", itemEffectType);
                boolean spreadFire = this.validateSpecVar(spec.spreadFire, "spreadFire", itemEffectType);

                RangeProfile rangeProfile = new RangeProfile(rangeProfileSpec.longRange.damage, rangeProfileSpec.longRange.distance, rangeProfileSpec.mediumRange.damage, rangeProfileSpec.mediumRange.distance, rangeProfileSpec.shortRange.damage, rangeProfileSpec.shortRange.distance);
                ExplosionProperties properties = new ExplosionProperties(rangeProfile, power, damageBlocks, spreadFire);

                ExplosionEffectNew explosionEffect = explosionEffectProvider.get();
                explosionEffect.setProperties(properties);
                yield explosionEffect;
            }
            case FLASH -> {
                double maxSize = this.validateSpecVar(spec.maxSize, "maxSize", itemEffectType);
                float power = this.validateSpecVar(spec.power, "power", itemEffectType);
                boolean damageBlocks = this.validateSpecVar(spec.damageBlocks, "damageBlocks", itemEffectType);
                boolean spreadFire = this.validateSpecVar(spec.spreadFire, "spreadFire", itemEffectType);
                PotionEffectSpec potionEffectSpec = this.validateSpecVar(spec.potionEffect, "potionEffect", itemEffectType);

                PotionEffectProperties potionEffect = new PotionEffectProperties(potionEffectSpec.duration, potionEffectSpec.amplifier, potionEffectSpec.ambient, potionEffectSpec.particles, potionEffectSpec.icon);
                FlashProperties properties = new FlashProperties(potionEffect, maxSize, power, damageBlocks, spreadFire);

                FlashEffectNew flashEffect = flashEffectProvider.get();
                flashEffect.setProperties(properties);
                yield flashEffect;
            }
            case GUN_FIRE_SIMULATION -> {
                List<GameSound> activationSounds = DefaultGameSound.parseSounds(spec.activationSounds);
                long maxDuration = this.validateSpecVar(spec.maxDuration, "maxDuration", itemEffectType);
                long minDuration = this.validateSpecVar(spec.minDuration, "minDuration", itemEffectType);
                ActivationPatternSpec activationPatternSpec = this.validateSpecVar(spec.activationPattern, "activationPattern", itemEffectType);

                GunFireSimulationProperties properties = new GunFireSimulationProperties(activationSounds, activationPatternSpec.burstInterval, activationPatternSpec.maxBurstDuration, activationPatternSpec.minBurstDuration, activationPatternSpec.maxDelayDuration, activationPatternSpec.minDelayDuration, maxDuration, minDuration);

                GunFireSimulationEffectNew gunFireSimulationEffect = gunFireSimulationEffectProvider.get();
                gunFireSimulationEffect.setProperties(properties);
                yield gunFireSimulationEffect;
            }
            case MARK_SPAWN_POINT -> markSpawnPointEffectProvider.get();
            case SMOKE_SCREEN -> {
                List<GameSound> activationSounds = DefaultGameSound.parseSounds(spec.activationSounds);
                long minDuration = this.validateSpecVar(spec.minDuration, "minDuration", itemEffectType);
                long maxDuration = this.validateSpecVar(spec.maxDuration, "maxDuration", itemEffectType);
                double density = this.validateSpecVar(spec.density, "density", itemEffectType);
                double minSize = this.validateSpecVar(spec.minSize, "minSize", itemEffectType);
                double maxSize = this.validateSpecVar(spec.maxSize, "maxSize", itemEffectType);
                double growth = this.validateSpecVar(spec.growth, "growth", itemEffectType);
                long growthInterval = this.validateSpecVar(spec.growthInterval, "growthInterval", itemEffectType);
                ParticleEffectSpec particleEffectSpec = this.validateSpecVar(spec.particleEffect, "particleEffect", itemEffectType);

                ParticleEffect particleEffect = particleEffectMapper.map(particleEffectSpec);

                SmokeScreenProperties properties = new SmokeScreenProperties(particleEffect, activationSounds, minDuration, maxDuration, density, minSize, maxSize, growth, growthInterval);

                SmokeScreenEffectNew smokeScreenEffect = smokeScreenEffectProvider.get();
                smokeScreenEffect.setProperties(properties);
                yield smokeScreenEffect;
            }
            case SOUND_NOTIFICATION -> {
                List<GameSound> notificationSounds = DefaultGameSound.parseSounds(spec.activationSounds);

                SoundNotificationEffectNew soundNotificationEffect = new SoundNotificationEffectNew();
                soundNotificationEffect.setNotificationSounds(notificationSounds);
                yield soundNotificationEffect;
            }
        };

        for (TriggerSpec triggerSpec : spec.triggers.values()) {
            TriggerExecutor triggerExecutor = triggerExecutorFactory.create(triggerSpec);

            itemEffect.addTriggerExecutor(triggerExecutor);
        }

        return itemEffect;
    }

    /**
     * Acts as a double check to validate the presence of nullable specification variables.
     *
     * @param value the specification value
     * @param valueName the name of the value, to create error messages
     * @param effectType the name of the effect type, to create error messages
     * @return the given value
     * @throws ItemEffectCreationException if the value is null
     * @param <T> the value type
     */
    private <T> T validateSpecVar(@Nullable T value, @NotNull String valueName, @NotNull Object effectType) {
        if (value == null) {
            throw new ItemEffectCreationException("Cannot create %s because of invalid spec: Required '%s' value is missing".formatted(effectType, valueName));
        }

        return value;
    }
}
