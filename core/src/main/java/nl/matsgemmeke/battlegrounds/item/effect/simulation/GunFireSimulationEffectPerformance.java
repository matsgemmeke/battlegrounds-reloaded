package nl.matsgemmeke.battlegrounds.item.effect.simulation;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunFireSimulationInfo;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.actor.Removable;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.sound.SoundEmitter;
import nl.matsgemmeke.battlegrounds.sound.SoundSequence;

import java.util.*;

public class GunFireSimulationEffectPerformance extends BaseItemEffectPerformance {

    private static final int TICKS_PER_SECOND = 20;
    private static final long SCHEDULE_DELAY = 0L;
    private static final long SCHEDULE_INTERVAL = 1L;

    private final GunFireSimulationProperties properties;
    private final GunInfoProvider gunInfoProvider;
    private final Random random;
    private final Scheduler scheduler;
    private final SoundEmitter soundEmitter;
    private boolean playingSounds;
    private long elapsedTicks;
    private long remainingTicks;
    private long totalDuration;
    private Schedule repeatingSchedule;

    @Inject
    public GunFireSimulationEffectPerformance(GunInfoProvider gunInfoProvider, Scheduler scheduler, SoundEmitter soundEmitter, @Assisted GunFireSimulationProperties properties) {
        this.gunInfoProvider = gunInfoProvider;
        this.scheduler = scheduler;
        this.soundEmitter = soundEmitter;
        this.properties = properties;
        this.random = new Random();
    }

    @Override
    public boolean isPerforming() {
        return repeatingSchedule != null && repeatingSchedule.isRunning();
    }

    @Override
    public void start() {
        UUID damageSourceId = currentContext.getDamageSource().getUniqueId();
        GunFireSimulationInfo gunFireSimulationInfo = gunInfoProvider.getGunFireSimulationInfo(damageSourceId).orElse(null);

        if (gunFireSimulationInfo == null) {
            this.simulateGenericGunFire();
            return;
        }

        double roundsPerSecond = (double) gunFireSimulationInfo.rateOfFire() / 60;
        int interval = (int) (TICKS_PER_SECOND / roundsPerSecond);

        this.simulateGunFire(gunFireSimulationInfo.shotSoundSequence(), interval);
    }

    private void simulateGenericGunFire() {
        this.simulateGunFire(properties.defaultSoundSequence(), properties.burstInterval());
    }

    private void simulateGunFire(SoundSequence soundSequence, long interval) {
        totalDuration = random.nextLong(properties.minTotalDuration(), properties.maxTotalDuration());
        elapsedTicks = 0;
        remainingTicks = this.getRandomBurstDurationInTicks();
        playingSounds = true;

        repeatingSchedule = scheduler.createRepeatingSchedule(SCHEDULE_DELAY, SCHEDULE_INTERVAL);
        repeatingSchedule.addTask(() -> this.handleScheduleTick(currentContext, soundSequence, interval));
        repeatingSchedule.start();
    }

    private void handleScheduleTick(ItemEffectContext context, SoundSequence soundSequence, long interval) {
        Actor actor = context.getActor();

        // Stop simulation when source no longer exists
        if (!actor.exists()) {
            repeatingSchedule.stop();
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
            soundEmitter.playSoundSequence(soundSequence, actor::getLocation);
        }

        if (elapsedTicks > totalDuration) {
            repeatingSchedule.stop();

            if (actor instanceof Removable removableActor) {
                removableActor.remove();
            }
        }
    }

    private long getRandomBurstDurationInTicks() {
        return random.nextLong(properties.minBurstDuration(), properties.maxBurstDuration());
    }

    private long getRandomDelayDurationInTicks() {
        return random.nextLong(properties.minDelayDuration(), properties.maxDelayDuration());
    }
}
