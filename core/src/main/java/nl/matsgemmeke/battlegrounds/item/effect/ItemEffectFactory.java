package nl.matsgemmeke.battlegrounds.item.effect;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.*;
import nl.matsgemmeke.battlegrounds.configuration.item.effect.*;
import nl.matsgemmeke.battlegrounds.configuration.item.effect.ItemEffectSpec;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.PotionEffectProperties;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionEffect;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionProperties;
import nl.matsgemmeke.battlegrounds.item.effect.damage.DamageEffect;
import nl.matsgemmeke.battlegrounds.item.effect.damage.DamageProperties;
import nl.matsgemmeke.battlegrounds.item.effect.explosion.ExplosionEffect;
import nl.matsgemmeke.battlegrounds.item.effect.explosion.ExplosionProperties;
import nl.matsgemmeke.battlegrounds.item.effect.flash.FlashEffect;
import nl.matsgemmeke.battlegrounds.item.effect.flash.FlashProperties;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationEffect;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationProperties;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenEffect;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenProperties;
import nl.matsgemmeke.battlegrounds.item.effect.sound.SoundNotificationEffect;
import nl.matsgemmeke.battlegrounds.item.effect.spawn.MarkSpawnPointEffect;
import nl.matsgemmeke.battlegrounds.item.mapper.RangeProfileMapper;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutorFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemEffectFactory {

    private final ParticleEffectMapper particleEffectMapper;
    private final Provider<CombustionEffect> combustionEffectProvider;
    private final Provider<DamageEffect> damageEffectProvider;
    private final Provider<ExplosionEffect> explosionEffectProvider;
    private final Provider<FlashEffect> flashEffectProvider;
    private final Provider<GunFireSimulationEffect> gunFireSimulationEffectProvider;
    private final Provider<MarkSpawnPointEffect> markSpawnPointEffectProvider;
    private final Provider<SmokeScreenEffect> smokeScreenEffectProvider;
    private final RangeProfileMapper rangeProfileMapper;
    private final TriggerExecutorFactory triggerExecutorFactory;

    @Inject
    public ItemEffectFactory(
            ParticleEffectMapper particleEffectMapper,
            Provider<CombustionEffect> combustionEffectProvider,
            Provider<DamageEffect> damageEffectProvider,
            Provider<ExplosionEffect> explosionEffectProvider,
            Provider<FlashEffect> flashEffectProvider,
            Provider<GunFireSimulationEffect> gunFireSimulationEffectProvider,
            Provider<MarkSpawnPointEffect> markSpawnPointEffectProvider,
            Provider<SmokeScreenEffect> smokeScreenEffectProvider,
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

    public ItemEffect create(ItemEffectSpec itemEffectSpec) {
        ItemEffectType itemEffectType = ItemEffectType.valueOf(itemEffectSpec.effectType);

        ItemEffect itemEffect = switch (itemEffectType) {
            case COMBUSTION -> {
                CombustionEffectSpec spec = (CombustionEffectSpec) itemEffectSpec;

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

                CombustionEffect combustionEffect = combustionEffectProvider.get();
                combustionEffect.setProperties(properties);
                yield combustionEffect;
            }
            case DAMAGE -> {
                DamageEffectSpec spec = (DamageEffectSpec) itemEffectSpec;
                RangeProfile rangeProfile = rangeProfileMapper.map(spec.range);
                DamageType damageType = DamageType.valueOf(spec.damageType);

//                DamageProperties properties = new DamageProperties(rangeProfile, damageType);

                DamageEffect damageEffect = damageEffectProvider.get();
//                damageEffect.setProperties(properties);
                yield damageEffect;
            }
            case EXPLOSION -> {
                ExplosionEffectSpec spec = (ExplosionEffectSpec) itemEffectSpec;

                RangeProfileSpec rangeProfileSpec = this.validateSpecVar(spec.range, "rangeProfile", itemEffectType);
                float power = this.validateSpecVar(spec.power, "power", itemEffectType);
                boolean damageBlocks = this.validateSpecVar(spec.damageBlocks, "damageBlocks", itemEffectType);
                boolean spreadFire = this.validateSpecVar(spec.spreadFire, "spreadFire", itemEffectType);

                RangeProfile rangeProfile = rangeProfileMapper.map(rangeProfileSpec);
                ExplosionProperties properties = new ExplosionProperties(rangeProfile, power, damageBlocks, spreadFire);

                ExplosionEffect explosionEffect = explosionEffectProvider.get();
                explosionEffect.setProperties(properties);
                yield explosionEffect;
            }
//            case FLASH -> {
//                double maxSize = this.validateSpecVar(spec.maxSize, "maxSize", itemEffectType);
//                float power = this.validateSpecVar(spec.power, "power", itemEffectType);
//                boolean damageBlocks = this.validateSpecVar(spec.damageBlocks, "damageBlocks", itemEffectType);
//                boolean spreadFire = this.validateSpecVar(spec.spreadFire, "spreadFire", itemEffectType);
//                PotionEffectSpec potionEffectSpec = this.validateSpecVar(spec.potionEffect, "potionEffect", itemEffectType);
//
//                PotionEffectProperties potionEffect = new PotionEffectProperties(potionEffectSpec.duration, potionEffectSpec.amplifier, potionEffectSpec.ambient, potionEffectSpec.particles, potionEffectSpec.icon);
//                FlashProperties properties = new FlashProperties(potionEffect, maxSize, power, damageBlocks, spreadFire);
//
//                FlashEffect flashEffect = flashEffectProvider.get();
//                flashEffect.setProperties(properties);
//                yield flashEffect;
//            }
            case GUN_FIRE_SIMULATION -> {
                GunFireSimulationEffectSpec spec = (GunFireSimulationEffectSpec) itemEffectSpec;

                List<GameSound> activationSounds = DefaultGameSound.parseSounds(spec.activationSounds);
                long minDuration = this.validateSpecVar(spec.minDuration, "minDuration", itemEffectType);
                long maxDuration = this.validateSpecVar(spec.maxDuration, "maxDuration", itemEffectType);
                long burstInterval = this.validateSpecVar(spec.burstInterval, "burstInterval", itemEffectType);
                long minBurstDuration = this.validateSpecVar(spec.minBurstDuration, "minBurstDuration", itemEffectType);
                long maxBurstDuration = this.validateSpecVar(spec.maxBurstDuration, "maxBurstDuration", itemEffectType);
                long minDelayDuration = this.validateSpecVar(spec.minDelayDuration, "minDelayDuration", itemEffectType);
                long maxDelayDuration = this.validateSpecVar(spec.maxDelayDuration, "maxDelayDuration", itemEffectType);

                GunFireSimulationProperties properties = new GunFireSimulationProperties(activationSounds, burstInterval, minBurstDuration, maxBurstDuration, minDelayDuration, maxDelayDuration, minDuration, maxDuration);

                GunFireSimulationEffect gunFireSimulationEffect = gunFireSimulationEffectProvider.get();
                gunFireSimulationEffect.setProperties(properties);
                yield gunFireSimulationEffect;
            }
//            case MARK_SPAWN_POINT -> markSpawnPointEffectProvider.get();
//            case SMOKE_SCREEN -> {
//                List<GameSound> activationSounds = DefaultGameSound.parseSounds(spec.activationSounds);
//                long minDuration = this.validateSpecVar(spec.minDuration, "minDuration", itemEffectType);
//                long maxDuration = this.validateSpecVar(spec.maxDuration, "maxDuration", itemEffectType);
//                double density = this.validateSpecVar(spec.density, "density", itemEffectType);
//                double minSize = this.validateSpecVar(spec.minSize, "minSize", itemEffectType);
//                double maxSize = this.validateSpecVar(spec.maxSize, "maxSize", itemEffectType);
//                double growth = this.validateSpecVar(spec.growth, "growth", itemEffectType);
//                long growthInterval = this.validateSpecVar(spec.growthInterval, "growthInterval", itemEffectType);
//                ParticleEffectSpec particleEffectSpec = this.validateSpecVar(spec.particleEffect, "particleEffect", itemEffectType);
//
//                ParticleEffect particleEffect = particleEffectMapper.map(particleEffectSpec);
//
//                SmokeScreenProperties properties = new SmokeScreenProperties(particleEffect, activationSounds, minDuration, maxDuration, density, minSize, maxSize, growth, growthInterval);
//
//                SmokeScreenEffect smokeScreenEffect = smokeScreenEffectProvider.get();
//                smokeScreenEffect.setProperties(properties);
//                yield smokeScreenEffect;
//            }
//            case SOUND_NOTIFICATION -> {
//                List<GameSound> notificationSounds = DefaultGameSound.parseSounds(spec.activationSounds);
//
//                SoundNotificationEffect soundNotificationEffect = new SoundNotificationEffect();
//                soundNotificationEffect.setNotificationSounds(notificationSounds);
//                yield soundNotificationEffect;
//            }
            default -> throw new IllegalStateException("Unexpected value: " + itemEffectType);
        };

        for (TriggerSpec triggerSpec : itemEffectSpec.triggers.values()) {
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
