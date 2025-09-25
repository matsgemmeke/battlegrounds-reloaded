package nl.matsgemmeke.battlegrounds.item.projectile.effect.sound;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.trigger.*;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.mockito.Mockito.*;

public class SoundEffectTest {

    private AudioEmitter audioEmitter;
    private Entity deployerEntity;
    private List<GameSound> sounds;
    private Projectile projectile;
    private TriggerExecutor triggerExecutor;
    private TriggerRun triggerRun;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        deployerEntity = mock(Entity.class);
        sounds = List.of(mock(GameSound.class));
        projectile = mock(Projectile.class);
        triggerRun = mock(TriggerRun.class);

        triggerExecutor = mock(TriggerExecutor.class);
        when(triggerExecutor.createTriggerRun(any(TriggerContext.class))).thenReturn(triggerRun);
    }

    @Test
    public void onLaunchStartsTriggerRunThatCancelWhenProjectileNoLongerExists() {
        when(projectile.exists()).thenReturn(false);

        SoundEffect effect = new SoundEffect(audioEmitter, sounds);
        effect.addTriggerExecutor(triggerExecutor);
        effect.onLaunch(deployerEntity, projectile);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(triggerRun).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate();

        verify(triggerRun).cancel();
        verify(projectile, never()).setGravity(anyBoolean());
        verify(projectile, never()).setVelocity(any(Vector.class));
    }

    @Test
    public void onLaunchStartsTriggerWithObserverThatPlaysSounds() {
        Location projectileLocation = new Location(null, 1, 2, 3);

        when(projectile.exists()).thenReturn(true);
        when(projectile.getLocation()).thenReturn(projectileLocation);

        SoundEffect effect = new SoundEffect(audioEmitter, sounds);
        effect.addTriggerExecutor(triggerExecutor);
        effect.onLaunch(deployerEntity, projectile);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(triggerRun).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate();

        verify(audioEmitter).playSounds(sounds, projectileLocation);
    }
}
