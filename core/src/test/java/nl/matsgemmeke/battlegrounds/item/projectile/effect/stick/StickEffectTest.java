package nl.matsgemmeke.battlegrounds.item.projectile.effect.stick;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.mockito.Mockito.*;

public class StickEffectTest {

    private AudioEmitter audioEmitter;
    private Entity deployerEntity;
    private List<GameSound> stickSounds;
    private Projectile projectile;
    private Trigger trigger;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        deployerEntity = mock(Entity.class);
        stickSounds = List.of(mock(GameSound.class));
        projectile = mock(Projectile.class);
        trigger = mock(Trigger.class);
    }

    @Test
    public void onLaunchStartsTriggerWithObserverThatSetsProjectileVelocityToZero() {
        Location projectileLocation = new Location(null, 1, 1, 1);
        when(projectile.getLocation()).thenReturn(projectileLocation);

        StickEffect effect = new StickEffect(audioEmitter, stickSounds);
        effect.addTrigger(trigger);
        effect.onLaunch(deployerEntity, projectile);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate();

        verify(audioEmitter).playSounds(stickSounds, projectileLocation);
        verify(projectile).setGravity(false);
        verify(projectile).setVelocity(new Vector(0, 0, 0));
    }
}
