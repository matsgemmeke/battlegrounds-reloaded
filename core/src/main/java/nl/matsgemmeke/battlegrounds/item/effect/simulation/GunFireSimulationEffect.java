package nl.matsgemmeke.battlegrounds.item.effect.simulation;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunFireSimulationInfo;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class GunFireSimulationEffect extends BaseItemEffect {

    private static final int TICKS_PER_SECOND = 20;
    private static final long TIMER_DELAY = 0L;
    private static final long TIMER_PERIOD = 1L;

    @NotNull
    private final AudioEmitter audioEmitter;
    private boolean playingSounds;
    private BukkitTask task;
    @NotNull
    private final GunFireSimulationProperties properties;
    @NotNull
    private final GunInfoProvider gunInfoProvider;
    private int elapsedTicks;
    private int remainingTicks;
    @NotNull
    private final Random random;
    @NotNull
    private final TaskRunner taskRunner;

    @Inject
    public GunFireSimulationEffect(
            @NotNull TaskRunner taskRunner,
            @Assisted @NotNull AudioEmitter audioEmitter,
            @Assisted @NotNull GunInfoProvider gunInfoProvider,
            @Assisted @NotNull GunFireSimulationProperties properties
    ) {
        this.taskRunner = taskRunner;
        this.audioEmitter = audioEmitter;
        this.gunInfoProvider = gunInfoProvider;
        this.properties = properties;
        this.random = new Random();
    }

    public void perform(@NotNull ItemEffectContext context) {
        Deployer deployer = context.getDeployer();

        if (!(deployer instanceof GunHolder holder)) {
            this.simulateGenericGunFire(context);
            return;
        }

        GunFireSimulationInfo gunFireSimulationInfo = gunInfoProvider.getGunFireSimulationInfo(holder);

        if (gunFireSimulationInfo == null) {
            this.simulateGenericGunFire(context);
            return;
        }

        this.simulateGunFire(context, gunFireSimulationInfo.shotSounds(), gunFireSimulationInfo.rateOfFire());
    }

    private void simulateGenericGunFire(@NotNull ItemEffectContext context) {
        this.simulateGunFire(context, properties.genericSounds(), properties.genericRateOfFire());
    }

    private void simulateGunFire(@NotNull ItemEffectContext context, @NotNull List<GameSound> sounds, int rateOfFire) {
        int totalDuration = random.nextInt(properties.minTotalDuration(), properties.maxTotalDuration());

        elapsedTicks = 0;
        remainingTicks = this.getRandomBurstDurationInTicks();
        playingSounds = true;

        double roundsPerSecond = (double) rateOfFire / 60;
        int interval = (int) (TICKS_PER_SECOND / roundsPerSecond);

        task = taskRunner.runTaskTimer(() -> {
            ItemEffectSource source = context.getSource();

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
