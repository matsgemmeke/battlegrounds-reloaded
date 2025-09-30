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
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionProperties;
import nl.matsgemmeke.battlegrounds.item.effect.damage.DamageEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.damage.DamageProperties;
import nl.matsgemmeke.battlegrounds.item.effect.explosion.ExplosionEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.explosion.ExplosionProperties;
import nl.matsgemmeke.battlegrounds.item.effect.flash.FlashEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.flash.FlashProperties;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationProperties;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenEffectFactory;
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

    @NotNull
    private final CombustionEffectFactory combustionEffectFactory;
    @NotNull
    private final DamageEffectFactory damageEffectFactory;
    @NotNull
    private final ExplosionEffectFactory explosionEffectFactory;
    @NotNull
    private final FlashEffectFactory flashEffectFactory;
    @NotNull
    private final GunFireSimulationEffectFactory gunFireSimulationEffectFactory;
    @NotNull
    private final ParticleEffectMapper particleEffectMapper;
    @NotNull
    private final Provider<MarkSpawnPointEffect> markSpawnPointEffectProvider;
    @NotNull
    private final RangeProfileMapper rangeProfileMapper;
    @NotNull
    private final SmokeScreenEffectFactory smokeScreenEffectFactory;
    @NotNull
    private final TriggerExecutorFactory triggerExecutorFactory;

    @Inject
    public ItemEffectFactory(
            @NotNull CombustionEffectFactory combustionEffectFactory,
            @NotNull DamageEffectFactory damageEffectFactory,
            @NotNull ExplosionEffectFactory explosionEffectFactory,
            @NotNull FlashEffectFactory flashEffectFactory,
            @NotNull GunFireSimulationEffectFactory gunFireSimulationEffectFactory,
            @NotNull ParticleEffectMapper particleEffectMapper,
            @NotNull Provider<MarkSpawnPointEffect> markSpawnPointEffectProvider,
            @NotNull RangeProfileMapper rangeProfileMapper,
            @NotNull SmokeScreenEffectFactory smokeScreenEffectFactory,
            @NotNull TriggerExecutorFactory triggerExecutorFactory
    ) {
        this.combustionEffectFactory = combustionEffectFactory;
        this.damageEffectFactory = damageEffectFactory;
        this.explosionEffectFactory = explosionEffectFactory;
        this.flashEffectFactory = flashEffectFactory;
        this.gunFireSimulationEffectFactory = gunFireSimulationEffectFactory;
        this.particleEffectMapper = particleEffectMapper;
        this.markSpawnPointEffectProvider = markSpawnPointEffectProvider;
        this.rangeProfileMapper = rangeProfileMapper;
        this.smokeScreenEffectFactory = smokeScreenEffectFactory;
        this.triggerExecutorFactory = triggerExecutorFactory;
    }

    public ItemEffect create(ItemEffectSpec spec) {
        ItemEffect itemEffect;
        ItemEffectType itemEffectType = ItemEffectType.valueOf(spec.type);

        switch (itemEffectType) {
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

                itemEffect = combustionEffectFactory.create(properties, rangeProfile);
            }
            case DAMAGE -> {
                String damageTypeValue = this.validateSpecVar(spec.damageType, "damageType", itemEffectType);
                RangeProfileSpec rangeProfileSpec = this.validateSpecVar(spec.range, "rangeProfile", itemEffectType);

                RangeProfile rangeProfile = rangeProfileMapper.map(rangeProfileSpec);
                DamageType damageType = DamageType.valueOf(damageTypeValue);

                DamageProperties properties = new DamageProperties(rangeProfile, damageType);

                itemEffect = damageEffectFactory.create(properties);
            }
            case EXPLOSION -> {
                RangeProfileSpec rangeProfileSpec = this.validateSpecVar(spec.range, "rangeProfile", itemEffectType);
                float power = this.validateSpecVar(spec.power, "power", itemEffectType);
                boolean damageBlocks = this.validateSpecVar(spec.damageBlocks, "damageBlocks", itemEffectType);
                boolean spreadFire = this.validateSpecVar(spec.spreadFire, "spreadFire", itemEffectType);

                ExplosionProperties properties = new ExplosionProperties(power, damageBlocks, spreadFire);
                RangeProfile rangeProfile = new RangeProfile(rangeProfileSpec.longRange.damage, rangeProfileSpec.longRange.distance, rangeProfileSpec.mediumRange.damage, rangeProfileSpec.mediumRange.distance, rangeProfileSpec.shortRange.damage, rangeProfileSpec.shortRange.distance);

                itemEffect = explosionEffectFactory.create(properties, rangeProfile);
            }
            case FLASH -> {
                double maxSize = this.validateSpecVar(spec.maxSize, "maxSize", itemEffectType);
                float power = this.validateSpecVar(spec.power, "power", itemEffectType);
                boolean damageBlocks = this.validateSpecVar(spec.damageBlocks, "damageBlocks", itemEffectType);
                boolean spreadFire = this.validateSpecVar(spec.spreadFire, "spreadFire", itemEffectType);
                PotionEffectSpec potionEffectSpec = this.validateSpecVar(spec.potionEffect, "potionEffect", itemEffectType);

                PotionEffectProperties potionEffect = new PotionEffectProperties(potionEffectSpec.duration, potionEffectSpec.amplifier, potionEffectSpec.ambient, potionEffectSpec.particles, potionEffectSpec.icon);
                FlashProperties properties = new FlashProperties(potionEffect, maxSize, power, damageBlocks, spreadFire);

                itemEffect = flashEffectFactory.create(properties);
            }
            case GUN_FIRE_SIMULATION -> {
                List<GameSound> activationSounds = DefaultGameSound.parseSounds(spec.activationSounds);
                long maxDuration = this.validateSpecVar(spec.maxDuration, "maxDuration", itemEffectType);
                long minDuration = this.validateSpecVar(spec.minDuration, "minDuration", itemEffectType);
                ActivationPatternSpec activationPatternSpec = this.validateSpecVar(spec.activationPattern, "activationPattern", itemEffectType);

                GunFireSimulationProperties properties = new GunFireSimulationProperties(activationSounds, activationPatternSpec.burstInterval, activationPatternSpec.maxBurstDuration, activationPatternSpec.minBurstDuration, activationPatternSpec.maxDelayDuration, activationPatternSpec.minDelayDuration, maxDuration, minDuration);

                itemEffect = gunFireSimulationEffectFactory.create(properties);
            }
            case MARK_SPAWN_POINT -> itemEffect = markSpawnPointEffectProvider.get();
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

                itemEffect = smokeScreenEffectFactory.create(properties);
            }
            case SOUND_NOTIFICATION -> {
                Iterable<GameSound> sounds = DefaultGameSound.parseSounds(spec.activationSounds);

                itemEffect = new SoundNotificationEffect(sounds);
            }
            default -> throw new ItemEffectCreationException("Unknown item effect type '%s'".formatted(itemEffectType));
        }

        for (TriggerSpec triggerSpec : spec.triggers.values()) {
            TriggerExecutor triggerExecutor = triggerExecutorFactory.create(triggerSpec);

//            itemEffect.addTriggerExecutor(triggerExecutor);
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
