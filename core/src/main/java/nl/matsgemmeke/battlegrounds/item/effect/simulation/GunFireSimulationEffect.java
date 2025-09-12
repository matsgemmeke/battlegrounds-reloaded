package nl.matsgemmeke.battlegrounds.item.effect.simulation;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunFireSimulationInfo;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

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
    private long elapsedTicks;
    private long remainingTicks;
    @NotNull
    private final Random random;
    @NotNull
    private final TaskRunner taskRunner;

    @Inject
    public GunFireSimulationEffect(
            @NotNull AudioEmitter audioEmitter,
            @NotNull GunInfoProvider gunInfoProvider,
            @NotNull TaskRunner taskRunner,
            @Assisted @NotNull GunFireSimulationProperties properties
    ) {
        this.taskRunner = taskRunner;
        this.audioEmitter = audioEmitter;
        this.gunInfoProvider = gunInfoProvider;
        this.properties = properties;
        this.random = new Random();
    }

    public void perform(@NotNull ItemEffectContext context) {
        UUID entityId = context.getEntity().getUniqueId();
        Optional<GunFireSimulationInfo> gunFireSimulationInfo = gunInfoProvider.getGunFireSimulationInfo(entityId);

        if (gunFireSimulationInfo.isEmpty()) {
            this.simulateGenericGunFire(context);
            return;
        }

        double roundsPerSecond = (double) gunFireSimulationInfo.get().rateOfFire() / 60;
        int interval = (int) (TICKS_PER_SECOND / roundsPerSecond);

        this.simulateGunFire(context, gunFireSimulationInfo.get().shotSounds(), interval);
    }

    private void simulateGenericGunFire(@NotNull ItemEffectContext context) {
        this.simulateGunFire(context, properties.genericSounds(), properties.burstInterval());
    }

    private void simulateGunFire(@NotNull ItemEffectContext context, @NotNull List<GameSound> sounds, long interval) {
        long totalDuration = random.nextLong(properties.minTotalDuration(), properties.maxTotalDuration());

        elapsedTicks = 0;
        remainingTicks = this.getRandomBurstDurationInTicks();
        playingSounds = true;

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

    private long getRandomBurstDurationInTicks() {
        return random.nextLong(properties.minBurstDuration(), properties.maxBurstDuration());
    }

    private long getRandomDelayDurationInTicks() {
        return random.nextLong(properties.minDelayDuration(), properties.maxDelayDuration());
    }
}
