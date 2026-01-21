package nl.matsgemmeke.battlegrounds.item.projectile.effect.sound;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.trigger.*;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoundEffectTest {

    private static final List<GameSound> SOUNDS = List.of(mock(GameSound.class));

    @Mock
    private AudioEmitter audioEmitter;
    @Mock
    private DamageSource damageSource;
    @Mock
    private Projectile projectile;
    @Mock
    private TriggerExecutor triggerExecutor;
    @Mock
    private TriggerResult triggerResult;
    @Mock
    private TriggerRun triggerRun;

    private SoundEffect effect;

    @BeforeEach
    void setUp() {
        when(triggerExecutor.createTriggerRun(any(TriggerContext.class))).thenReturn(triggerRun);

        effect = new SoundEffect(audioEmitter, SOUNDS);
    }

    @Test
    void onLaunchStartsTriggerRunThatCancelWhenProjectileNoLongerExists() {
        when(projectile.exists()).thenReturn(false);

        effect.addTriggerExecutor(triggerExecutor);
        effect.onLaunch(damageSource, projectile);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(triggerRun).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate(triggerResult);

        verify(triggerRun).cancel();
        verify(projectile, never()).setGravity(anyBoolean());
        verify(projectile, never()).setVelocity(any(Vector.class));
    }

    @Test
    void onLaunchStartsTriggerWithObserverThatPlaysSounds() {
        Location projectileLocation = new Location(null, 1, 2, 3);

        when(projectile.exists()).thenReturn(true);
        when(projectile.getLocation()).thenReturn(projectileLocation);

        effect.addTriggerExecutor(triggerExecutor);
        effect.onLaunch(damageSource, projectile);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(triggerRun).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate(triggerResult);

        verify(audioEmitter).playSounds(SOUNDS, projectileLocation);
    }
}
