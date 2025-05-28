package nl.matsgemmeke.battlegrounds.item.projectile.effect.sound;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
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
    private Trigger trigger;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        deployerEntity = mock(Entity.class);
        sounds = List.of(mock(GameSound.class));
        projectile = mock(Projectile.class);
        trigger = mock(Trigger.class);
    }

    @Test
    public void onLaunchStartsTriggerWithObserverThatPlaysSounds() {
        Location projectileLocation = new Location(null, 1, 2, 3);
        when(projectile.getLocation()).thenReturn(projectileLocation);

        SoundEffect effect = new SoundEffect(audioEmitter, sounds);
        effect.addTrigger(trigger);
        effect.onLaunch(deployerEntity, projectile);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate();

        verify(audioEmitter).playSounds(sounds, projectileLocation);
    }
}
