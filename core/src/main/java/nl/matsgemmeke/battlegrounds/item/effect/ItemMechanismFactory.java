package nl.matsgemmeke.battlegrounds.item.effect;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.effect.explosion.ExplosionEffect;
import nl.matsgemmeke.battlegrounds.item.effect.explosion.ExplosionSettings;
import nl.matsgemmeke.battlegrounds.item.effect.flash.FlashEffect;
import nl.matsgemmeke.battlegrounds.item.effect.flash.FlashSettings;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenMechanism;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenSettings;
import nl.matsgemmeke.battlegrounds.util.MetadataValueCreator;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemMechanismFactory {

    @NotNull
    private MetadataValueCreator metadataValueCreator;
    @NotNull
    private TaskRunner taskRunner;

    public ItemMechanismFactory(@NotNull MetadataValueCreator metadataValueCreator, @NotNull TaskRunner taskRunner) {
        this.metadataValueCreator = metadataValueCreator;
        this.taskRunner = taskRunner;
    }

    public ItemMechanism make(@NotNull Section section, @NotNull GameContext context) {
        String type = section.getString("type");

        if (type == null) {
            throw new InvalidItemConfigurationException("Equipment mechanism type must be defined!");
        }

        ItemMechanismType equipmentMechanismType;

        try {
            equipmentMechanismType = ItemMechanismType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new InvalidItemConfigurationException("Equipment mechanism type \"" + type + "\" is invalid!");
        }

        switch (equipmentMechanismType) {
            case COMBUSTION -> {
                int radius = section.getInt("radius");
                long ticksBetweenSpread = section.getLong("ticks-between-spread");
                boolean burnBlocks = section.getBoolean("burn-blocks");
                boolean spreadFire = section.getBoolean("spread-fire");

                double longRangeDamage = section.getDouble("range.long-range.damage");
                double longRangeDistance = section.getDouble("range.long-range.distance");
                double mediumRangeDamage = section.getDouble("range.medium-range.damage");
                double mediumRangeDistance = section.getDouble("range.medium-range.distance");
                double shortRangeDamage = section.getDouble("range.short-range.damage");
                double shortRangeDistance = section.getDouble("range.short-range.distance");

                List<GameSound> sounds = DefaultGameSound.parseSounds(section.getString("combustion-sound"));

                CombustionSettings settings = new CombustionSettings(sounds, radius, ticksBetweenSpread, burnBlocks, spreadFire);
                RangeProfile rangeProfile = new RangeProfile(longRangeDamage, longRangeDistance, mediumRangeDamage, mediumRangeDistance, shortRangeDamage, shortRangeDistance);
                AudioEmitter audioEmitter = context.getAudioEmitter();
                TargetFinder targetFinder = context.getTargetFinder();

                return new CombustionMechanism(settings, rangeProfile, audioEmitter, metadataValueCreator, targetFinder, taskRunner);
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

                ExplosionSettings settings = new ExplosionSettings(power, breakBlocks, setFire);
                RangeProfile rangeProfile = new RangeProfile(longRangeDamage, longRangeDistance, mediumRangeDamage, mediumRangeDistance, shortRangeDamage, shortRangeDistance);
                TargetFinder targetFinder = context.getTargetFinder();

                return new ExplosionEffect(settings, rangeProfile, targetFinder);
            }
            case FLASH -> {
                double range = section.getDouble("range");

                int effectDuration = section.getInt("effect.duration");
                int effectAmplifier = section.getInt("effect.amplifier");
                boolean effectAmbient = section.getBoolean("effect.ambient");
                boolean effectParticles = section.getBoolean("effect.particles");
                boolean effectIcon = section.getBoolean("effect.icon");

                float explosionPower = section.getFloat("explosion.power");
                boolean explosionBreakBlocks = section.getBoolean("explosion.break-blocks");
                boolean explosionSetFire = section.getBoolean("explosion.set-fire");

                FlashSettings settings = new FlashSettings(range, effectDuration, effectAmplifier, effectAmbient, effectParticles, effectIcon, explosionPower, explosionBreakBlocks, explosionSetFire);
                TargetFinder targetFinder = context.getTargetFinder();

                return new FlashEffect(settings, targetFinder);
            }
            case SMOKE_SCREEN -> {
                Particle particle;
                String particleValue = section.getString("particle.type");

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

                Iterable<GameSound> ignitionSounds = DefaultGameSound.parseSounds(section.getString("ignition-sound"));
                int size = section.getInt("size");

                SmokeScreenSettings smokeScreenSettings = new SmokeScreenSettings(ignitionSounds, size);
                ParticleSettings particleSettings = new ParticleSettings(particle, count, offsetX, offsetY, offsetZ, extra);
                AudioEmitter audioEmitter = context.getAudioEmitter();

                return new SmokeScreenMechanism(smokeScreenSettings, particleSettings, audioEmitter, taskRunner);
            }
        }

        throw new InvalidItemConfigurationException("Unknown equipment mechanism type \"" + type + "\"!");
    }
}
