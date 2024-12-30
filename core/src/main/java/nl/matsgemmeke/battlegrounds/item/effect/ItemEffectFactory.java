package nl.matsgemmeke.battlegrounds.item.effect;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import nl.matsgemmeke.battlegrounds.item.ParticleEffectProperties;
import nl.matsgemmeke.battlegrounds.item.PotionEffectProperties;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionEffect;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionProperties;
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
import nl.matsgemmeke.battlegrounds.util.MetadataValueEditor;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemEffectFactory {

    @NotNull
    private MetadataValueEditor metadataValueEditor;
    @NotNull
    private TaskRunner taskRunner;

    public ItemEffectFactory(@NotNull MetadataValueEditor metadataValueEditor, @NotNull TaskRunner taskRunner) {
        this.metadataValueEditor = metadataValueEditor;
        this.taskRunner = taskRunner;
    }

    public ItemEffect make(@NotNull Section section, @NotNull GameContext context, @NotNull ItemEffectActivation effectActivation) {
        String type = section.getString("type");

        if (type == null) {
            throw new InvalidItemConfigurationException("Item effect type must be defined!");
        }

        ItemEffectType itemEffectType;

        try {
            itemEffectType = ItemEffectType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new InvalidItemConfigurationException("Item effect type \"" + type + "\" is invalid!");
        }

        switch (itemEffectType) {
            case COMBUSTION -> {
                int radius = section.getInt("radius");
                long ticksBetweenSpread = section.getLong("ticks-between-spread");
                long maxDuration = section.getLong("max-duration");
                boolean burnBlocks = section.getBoolean("burn-blocks");
                boolean spreadFire = section.getBoolean("spread-fire");

                double longRangeDamage = section.getDouble("range.long-range.damage");
                double longRangeDistance = section.getDouble("range.long-range.distance");
                double mediumRangeDamage = section.getDouble("range.medium-range.damage");
                double mediumRangeDistance = section.getDouble("range.medium-range.distance");
                double shortRangeDamage = section.getDouble("range.short-range.damage");
                double shortRangeDistance = section.getDouble("range.short-range.distance");

                List<GameSound> sounds = DefaultGameSound.parseSounds(section.getString("combustion-sound"));

                CombustionProperties properties = new CombustionProperties(sounds, radius, ticksBetweenSpread, maxDuration, burnBlocks, spreadFire);
                RangeProfile rangeProfile = new RangeProfile(longRangeDamage, longRangeDistance, mediumRangeDamage, mediumRangeDistance, shortRangeDamage, shortRangeDistance);
                AudioEmitter audioEmitter = context.getAudioEmitter();
                CollisionDetector collisionDetector = context.getCollisionDetector();
                TargetFinder targetFinder = context.getTargetFinder();

                return new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueEditor, targetFinder, taskRunner);
            }
            case EXPLOSION -> {
                float power = section.getFloat("power");
                boolean setFire = section.getBoolean("set-fire");
                boolean breakBlocks = section.getBoolean("break-blocks");

                double longRangeDamage = section.getDouble("range.long-range.damage");
                double longRangeDistance = section.getDouble("range.long-range.distance");
                double mediumRangeDamage = section.getDouble("range.medium-range.damage");
                double mediumRangeDistance = section.getDouble("range.medium-range.distance");
                double shortRangeDamage = section.getDouble("range.short-range.damage");
                double shortRangeDistance = section.getDouble("range.short-range.distance");

                ExplosionProperties properties = new ExplosionProperties(power, breakBlocks, setFire);
                RangeProfile rangeProfile = new RangeProfile(longRangeDamage, longRangeDistance, mediumRangeDamage, mediumRangeDistance, shortRangeDamage, shortRangeDistance);
                TargetFinder targetFinder = context.getTargetFinder();

                 return new ExplosionEffect(effectActivation, properties, rangeProfile, targetFinder);
            }
            case FLASH -> {
                double range = section.getDouble("range");
                float explosionPower = section.getFloat("explosion.power");
                boolean explosionBreakBlocks = section.getBoolean("explosion.break-blocks");
                boolean explosionSetFire = section.getBoolean("explosion.set-fire");

                int duration = section.getInt("potion-effect.duration");
                int amplifier = section.getInt("potion-effect.amplifier");
                boolean ambient = section.getBoolean("potion-effect.ambient");
                boolean particles = section.getBoolean("potion-effect.particles");
                boolean icon = section.getBoolean("potion-effect.icon");
                PotionEffectProperties potionEffect = new PotionEffectProperties(duration, amplifier, ambient, particles, icon);

                FlashProperties properties = new FlashProperties(potionEffect, range, explosionPower, explosionBreakBlocks, explosionSetFire);
                TargetFinder targetFinder = context.getTargetFinder();

                return new FlashEffect(effectActivation, properties, targetFinder);
            }
            case GUN_FIRE_SIMULATION -> {
                List<GameSound> genericSounds = DefaultGameSound.parseSounds(section.getString("generic-sound"));
                int genericRateOfFire = section.getInt("generic-rate-of-fire");
                int maxBurstDuration = section.getInt("max-burst-duration");
                int minBurstDuration = section.getInt("min-burst-duration");
                int maxDelayBetweenBursts = section.getInt("max-delay-between-bursts");
                int minDelayBetweenBursts = section.getInt("min-delay-between-bursts");
                int maxTotalDuration = section.getInt("max-total-duration");
                int minTotalDuration = section.getInt("min-total-duration");

                AudioEmitter audioEmitter = context.getAudioEmitter();
                GunInfoProvider gunInfoProvider = context.getGunInfoProvider();
                GunFireSimulationProperties properties = new GunFireSimulationProperties(genericSounds, genericRateOfFire, maxBurstDuration, minBurstDuration, maxDelayBetweenBursts, minDelayBetweenBursts, maxTotalDuration, minTotalDuration);

                return new GunFireSimulationEffect(effectActivation, audioEmitter, gunInfoProvider, taskRunner, properties);
            }
            case MARK_SPAWN_POINT -> {
                SpawnPointProvider spawnPointProvider = context.getSpawnPointProvider();

                return new MarkSpawnPointEffect(effectActivation, spawnPointProvider);
            }
            case SMOKE_SCREEN -> {
                Particle particle;
                String particleValue = section.getString("particle.type");

                if (particleValue == null) {
                    throw new InvalidItemConfigurationException("Particle type must be defined!");
                }

                try {
                    particle = Particle.valueOf(particleValue);
                } catch (IllegalArgumentException e) {
                    throw new InvalidItemConfigurationException("Particle type \"" + particleValue + "\" is invalid!");
                }

                int count = section.getInt("particle.count");
                double offsetX = section.getDouble("particle.offset-x");
                double offsetY = section.getDouble("particle.offset-y");
                double offsetZ = section.getDouble("particle.offset-z");
                double extra = section.getDouble("particle.extra");
                ParticleEffectProperties particleEffect = new ParticleEffectProperties(particle, count, offsetX, offsetY, offsetZ, extra);

                List<GameSound> ignitionSounds = DefaultGameSound.parseSounds(section.getString("ignition-sound"));
                int duration = section.getInt("duration");
                double density = section.getDouble("density");
                double radiusMaxSize = section.getDouble("radius.max-size");
                double radiusStartingSize = section.getDouble("radius.starting-size");
                double growthIncrease = section.getDouble("growth-increase");
                long growthPeriod = section.getLong("growth-period");

                SmokeScreenProperties properties = new SmokeScreenProperties(particleEffect, ignitionSounds, duration, density, radiusMaxSize, radiusStartingSize, growthIncrease, growthPeriod);
                AudioEmitter audioEmitter = context.getAudioEmitter();
                CollisionDetector collisionDetector = context.getCollisionDetector();

                return new SmokeScreenEffect(effectActivation, properties, audioEmitter, collisionDetector, taskRunner);
            }
            case SOUND_NOTIFICATION -> {
                Iterable<GameSound> sounds = DefaultGameSound.parseSounds(section.getString("sound"));

                return new SoundNotificationEffect(effectActivation, sounds);
            }
        }

        throw new InvalidItemConfigurationException("Unknown item effect type \"" + type + "\"!");
    }
}
