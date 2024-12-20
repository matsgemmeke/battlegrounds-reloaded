package nl.matsgemmeke.battlegrounds.item.effect.simulation;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunFireSimulationInfo;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class GunFireSimulationEffect implements ItemEffect {

    private static final int TICKS_PER_SECOND = 20;
    private static final long TIMER_DELAY = 0L;
    private static final long TIMER_PERIOD = 1L;

    @NotNull
    private AudioEmitter audioEmitter;
    private boolean playingSounds;
    private BukkitTask task;
    @NotNull
    private GunFireSimulationProperties properties;
    @NotNull
    private GunInfoProvider gunInfoProvider;
    private int elapsedTicks;
    private int remainingTicks;
    @NotNull
    private Random random;
    @NotNull
    private TaskRunner taskRunner;

    public GunFireSimulationEffect(
            @NotNull AudioEmitter audioEmitter,
            @NotNull GunInfoProvider gunInfoProvider,
            @NotNull TaskRunner taskRunner,
            @NotNull GunFireSimulationProperties properties
    ) {
        this.audioEmitter = audioEmitter;
        this.gunInfoProvider = gunInfoProvider;
        this.taskRunner = taskRunner;
        this.properties = properties;
        this.random = new Random();
    }

    public void activate(@NotNull ItemEffectContext context) {
        ItemHolder itemHolder = context.getHolder();
        EffectSource source = context.getSource();

        if (!(itemHolder instanceof GunHolder holder)) {
            this.simulateGenericGunFire(source);
            return;
        }

        GunFireSimulationInfo gunFireSimulationInfo = gunInfoProvider.getGunFireSimulationInfo(holder);

        if (gunFireSimulationInfo == null) {
            this.simulateGenericGunFire(source);
            return;
        }

        this.simulateGunFire(source, gunFireSimulationInfo.shotSounds(), gunFireSimulationInfo.rateOfFire());
    }

    private void simulateGenericGunFire(@NotNull EffectSource source) {
        this.simulateGunFire(source, properties.genericSounds(), properties.genericRateOfFire());
    }

    private void simulateGunFire(@NotNull EffectSource source, @NotNull List<GameSound> sounds, int rateOfFire) {
        int totalDuration = random.nextInt(properties.minTotalDuration(), properties.maxTotalDuration());

        elapsedTicks = 0;
        remainingTicks = this.getRandomBurstDurationInTicks();
        playingSounds = true;

        double roundsPerSecond = (double) rateOfFire / 60;
        int interval = (int) (TICKS_PER_SECOND / roundsPerSecond);

        task = taskRunner.runTaskTimer(() -> {
            // Stop simulation when source no longer exists
            if (!source.exists()) {
                task.cancel();
                return;
            }

            elapsedTicks++;
            remainingTicks--;

            // Swap phase if the time has run out
            if (remainingTicks < 0) {
                playingSounds = !playingSounds;

                if (playingSounds) {
                    remainingTicks = this.getRandomBurstDurationInTicks();
                } else {
                    remainingTicks = this.getRandomDelayDurationInTicks();
                }
            }

            if (playingSounds && remainingTicks % interval == 0) {
                audioEmitter.playSounds(sounds, source.getLocation());
            }

            if (elapsedTicks > totalDuration) {
                source.remove();
                task.cancel();
            }
        }, TIMER_DELAY, TIMER_PERIOD);
    }

    private int getRandomBurstDurationInTicks() {
        return random.nextInt(properties.minBurstDuration(), properties.maxBurstDuration());
    }

    private int getRandomDelayDurationInTicks() {
        return random.nextInt(properties.minDelayBetweenBursts(), properties.maxDelayBetweenBursts());
    }
}
