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

public class GunFireSimulationEffect implements ItemEffect {

    private static final int TICKS_PER_SECOND = 20;
    private static final long TIMER_DELAY = 0L;

    @NotNull
    private AudioEmitter audioEmitter;
    private BukkitTask task;
    @NotNull
    private GunFireSimulationProperties properties;
    @NotNull
    private GunInfoProvider gunInfoProvider;
    private int elapsedTicks;
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
        elapsedTicks = 0;

        double roundsPerSecond = (double) rateOfFire / 60;
        long timerPeriod = (long) (TICKS_PER_SECOND / roundsPerSecond);

        task = taskRunner.runTaskTimer(() -> {
            if (!source.exists()) {
                task.cancel();
                return;
            }

            audioEmitter.playSounds(sounds, source.getLocation());
            elapsedTicks += timerPeriod;

            if (elapsedTicks >= properties.duration()) {
                source.remove();
                task.cancel();
            }
        }, TIMER_DELAY, timerPeriod);
    }
}
