package nl.matsgemmeke.battlegrounds.item.projectile.effect.stick;

import nl.matsgemmeke.battlegrounds.entity.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.trigger.*;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import nl.matsgemmeke.battlegrounds.sound.api.SoundEmitter;
import nl.matsgemmeke.battlegrounds.sound.api.SoundSequence;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StickEffectTest {

    private static final SoundSequence SOUND_SEQUENCE = new SoundSequence(null, null);

    @Mock
    private DamageSource damageSource;
    @Mock
    private Projectile projectile;
    @Mock
    private SoundEmitter soundEmitter;
    @Mock
    private TriggerExecutor triggerExecutor;
    @Mock
    private TriggerResult triggerResult;
    @Mock
    private TriggerRun triggerRun;

    private StickEffect effect;

    @BeforeEach
    void setUp() {
        when(triggerExecutor.createTriggerRun(any(TriggerContext.class))).thenReturn(triggerRun);

        effect = new StickEffect(soundEmitter, SOUND_SEQUENCE);
    }

    @Test
    @DisplayName("onLaunch starts trigger run that cancels when projectile no longer exists")
    void onLaunch_projectileNoLongerExists() {
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
    @DisplayName("onLaunch starts trigger run with observer that performs effects")
    void onLaunch_successful() {
        Location projectileLocation = new Location(null, 1, 1, 1);

        when(projectile.exists()).thenReturn(true);
        when(projectile.getLocation()).thenReturn(projectileLocation);

        effect.addTriggerExecutor(triggerExecutor);
        effect.onLaunch(damageSource, projectile);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(triggerRun).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate(triggerResult);

        verify(soundEmitter).playSoundSequence(eq(SOUND_SEQUENCE), Mockito.<Supplier<Location>>argThat(supplier -> supplier.get() == projectileLocation));
        verify(projectile).setGravity(false);
        verify(projectile).setVelocity(new Vector(0, 0, 0));
    }
}
