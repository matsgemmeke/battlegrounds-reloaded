package nl.matsgemmeke.battlegrounds.item.effect;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.PotionEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.RangeProfileSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.ActivationPatternSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.ItemEffectSpec;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.item.PotionEffectProperties;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionProperties;
import nl.matsgemmeke.battlegrounds.item.effect.explosion.ExplosionEffect;
import nl.matsgemmeke.battlegrounds.item.effect.explosion.ExplosionProperties;
import nl.matsgemmeke.battlegrounds.item.effect.flash.FlashEffect;
import nl.matsgemmeke.battlegrounds.item.effect.flash.FlashProperties;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationProperties;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenProperties;
import nl.matsgemmeke.battlegrounds.item.effect.sound.SoundNotificationEffect;
import nl.matsgemmeke.battlegrounds.item.effect.spawn.MarkSpawnPointEffect;
import nl.matsgemmeke.battlegrounds.item.mapper.ParticleEffectMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemEffectFactory {

    @NotNull
    private final CombustionEffectFactory combustionEffectFactory;
    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final GunFireSimulationEffectFactory gunFireSimulationEffectFactory;
    @NotNull
    private final ParticleEffectMapper particleEffectMapper;
    @NotNull
    private final SmokeScreenEffectFactory smokeScreenEffectFactory;

    @Inject
    public ItemEffectFactory(
            @NotNull GameContextProvider contextProvider,
            @NotNull CombustionEffectFactory combustionEffectFactory,
            @NotNull GunFireSimulationEffectFactory gunFireSimulationEffectFactory,
            @NotNull ParticleEffectMapper particleEffectMapper,
            @NotNull SmokeScreenEffectFactory smokeScreenEffectFactory
    ) {
        this.contextProvider = contextProvider;
        this.combustionEffectFactory = combustionEffectFactory;
        this.gunFireSimulationEffectFactory = gunFireSimulationEffectFactory;
        this.particleEffectMapper = particleEffectMapper;
        this.smokeScreenEffectFactory = smokeScreenEffectFactory;
    }

    public ItemEffect create(@NotNull ItemEffectSpec spec, @NotNull GameKey gameKey) {
        ItemEffectType itemEffectType = ItemEffectType.valueOf(spec.type());

        switch (itemEffectType) {
            case COMBUSTION -> {
                double maxSize = this.validateSpecVar(spec.maxSize(), "maxSize", itemEffectType);
                long growthInterval = this.validateSpecVar(spec.growthInterval(), "growthInterval", itemEffectType);
                long maxDuration = this.validateSpecVar(spec.maxDuration(), "maxDuration", itemEffectType);
                boolean damageBlocks = this.validateSpecVar(spec.damageBlocks(), "damageBlocks", itemEffectType);
                boolean spreadFire = this.validateSpecVar(spec.spreadFire(), "spreadFire", itemEffectType);
                List<GameSound> activationSounds = DefaultGameSound.parseSounds(spec.activationSounds());
                RangeProfileSpec rangeProfileSpec = this.validateSpecVar(spec.rangeProfile(), "rangeProfile", itemEffectType);

                CombustionProperties properties = new CombustionProperties(activationSounds, maxSize, growthInterval, maxDuration, damageBlocks, spreadFire);
                RangeProfile rangeProfile = new RangeProfile(rangeProfileSpec.longRangeDamage(), rangeProfileSpec.longRangeDistance(), rangeProfileSpec.mediumRangeDamage(), rangeProfileSpec.mediumRangeDistance(), rangeProfileSpec.shortRangeDamage(), rangeProfileSpec.shortRangeDistance());

                AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);
                CollisionDetector collisionDetector = contextProvider.getComponent(gameKey, CollisionDetector.class);
                TargetFinder targetFinder = contextProvider.getComponent(gameKey, TargetFinder.class);

                return combustionEffectFactory.create(properties, rangeProfile, audioEmitter, collisionDetector, targetFinder);
            }
            case EXPLOSION -> {
                RangeProfileSpec rangeProfileSpec = this.validateSpecVar(spec.rangeProfile(), "rangeProfile", itemEffectType);
                float power = this.validateSpecVar(spec.power(), "power", itemEffectType);
                boolean damageBlocks = this.validateSpecVar(spec.damageBlocks(), "damageBlocks", itemEffectType);
                boolean spreadFire = this.validateSpecVar(spec.spreadFire(), "spreadFire", itemEffectType);

                ExplosionProperties properties = new ExplosionProperties(power, damageBlocks, spreadFire);
                DamageProcessor damageProcessor = contextProvider.getComponent(gameKey, DamageProcessor.class);
                RangeProfile rangeProfile = new RangeProfile(rangeProfileSpec.longRangeDamage(), rangeProfileSpec.longRangeDistance(), rangeProfileSpec.mediumRangeDamage(), rangeProfileSpec.mediumRangeDistance(), rangeProfileSpec.shortRangeDamage(), rangeProfileSpec.shortRangeDistance());
                TargetFinder targetFinder = contextProvider.getComponent(gameKey, TargetFinder.class);

                return new ExplosionEffect(properties, damageProcessor, rangeProfile, targetFinder);
            }
            case FLASH -> {
                double maxSize = this.validateSpecVar(spec.maxSize(), "maxSize", itemEffectType);
                float power = this.validateSpecVar(spec.power(), "power", itemEffectType);
                boolean damageBlocks = this.validateSpecVar(spec.damageBlocks(), "damageBlocks", itemEffectType);
                boolean spreadFire = this.validateSpecVar(spec.spreadFire(), "spreadFire", itemEffectType);
                PotionEffectSpec potionEffectSpec = this.validateSpecVar(spec.potionEffect(), "potionEffect", itemEffectType);

                PotionEffectProperties potionEffect = new PotionEffectProperties(potionEffectSpec.duration(), potionEffectSpec.amplifier(), potionEffectSpec.ambient(), potionEffectSpec.particles(), potionEffectSpec.icon());

                FlashProperties properties = new FlashProperties(potionEffect, maxSize, power, damageBlocks, spreadFire);
                TargetFinder targetFinder = contextProvider.getComponent(gameKey, TargetFinder.class);

                return new FlashEffect(properties, targetFinder);
            }
            case GUN_FIRE_SIMULATION -> {
                List<GameSound> activationSounds = DefaultGameSound.parseSounds(spec.activationSounds());
                long maxDuration = this.validateSpecVar(spec.maxDuration(), "maxDuration", itemEffectType);
                long minDuration = this.validateSpecVar(spec.minDuration(), "minDuration", itemEffectType);
                ActivationPatternSpec activationPatternSpec = this.validateSpecVar(spec.activationPattern(), "activationPattern", itemEffectType);

                AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);
                GunInfoProvider gunInfoProvider = contextProvider.getComponent(gameKey, GunInfoProvider.class);
                GunFireSimulationProperties properties = new GunFireSimulationProperties(activationSounds, activationPatternSpec.burstInterval(), activationPatternSpec.maxBurstDuration(), activationPatternSpec.minBurstDuration(), activationPatternSpec.maxDelayDuration(), activationPatternSpec.minDelayDuration(), maxDuration, minDuration);

                return gunFireSimulationEffectFactory.create(audioEmitter, gunInfoProvider, properties);
            }
            case MARK_SPAWN_POINT -> {
                SpawnPointProvider spawnPointProvider = contextProvider.getComponent(gameKey, SpawnPointProvider.class);

                return new MarkSpawnPointEffect(spawnPointProvider);
            }
            case SMOKE_SCREEN -> {
                List<GameSound> activationSounds = DefaultGameSound.parseSounds(spec.activationSounds());
                long maxDuration = this.validateSpecVar(spec.maxDuration(), "maxDuration", itemEffectType);
                long minDuration = this.validateSpecVar(spec.minDuration(), "minDuration", itemEffectType);
                double density = this.validateSpecVar(spec.density(), "density", itemEffectType);
                double maxSize = this.validateSpecVar(spec.maxSize(), "maxSize", itemEffectType);
                double minSize = this.validateSpecVar(spec.minSize(), "minSize", itemEffectType);
                double growth = this.validateSpecVar(spec.growth(), "growth", itemEffectType);
                long growthInterval = this.validateSpecVar(spec.growthInterval(), "growthInterval", itemEffectType);
                ParticleEffectSpec particleEffectSpec = this.validateSpecVar(spec.particleEffect(), "particleEffect", itemEffectType);

                ParticleEffect particleEffect = particleEffectMapper.map(particleEffectSpec);

                SmokeScreenProperties properties = new SmokeScreenProperties(particleEffect, activationSounds, maxDuration, minDuration, density, maxSize, minSize, growth, growthInterval);
                AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);
                CollisionDetector collisionDetector = contextProvider.getComponent(gameKey, CollisionDetector.class);

                return smokeScreenEffectFactory.create(properties, audioEmitter, collisionDetector);
            }
            case SOUND_NOTIFICATION -> {
                Iterable<GameSound> sounds = DefaultGameSound.parseSounds(spec.activationSounds());

                return new SoundNotificationEffect(sounds);
            }
        }

        throw new ItemEffectCreationException("Unknown item effect type \"" + itemEffectType + "\"!");
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
