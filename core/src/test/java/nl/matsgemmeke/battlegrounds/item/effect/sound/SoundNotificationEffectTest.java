package nl.matsgemmeke.battlegrounds.item.effect.sound;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.effect.EffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.EffectSource;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.mockito.Mockito.*;

public class SoundNotificationEffectTest {

    private static final Location INITIATION_LOCATION = new Location(null, 0, 0, 0);
    private static final Sound SOUND = Sound.AMBIENT_CAVE;
    private static final float VOLUME = 1.0f;
    private static final float PITCH = 2.0f;

    private GameSound sound;
    private List<GameSound> sounds;
    private Trigger trigger;

    @BeforeEach
    public void setUp() {
        trigger = mock(Trigger.class);

        sound = mock(GameSound.class);
        when(sound.getSound()).thenReturn(SOUND);
        when(sound.getVolume()).thenReturn(VOLUME);
        when(sound.getPitch()).thenReturn(PITCH);

        sounds = List.of(sound);
    }

    @Test
    public void activatePlaysNoSoundsIfEntityIsNoPlayer() {
        Zombie zombie = mock(Zombie.class);
        EffectSource source = mock(EffectSource.class);
        EffectContext context = new EffectContext(zombie, source, INITIATION_LOCATION);

        SoundNotificationEffect effect = new SoundNotificationEffect(sounds);
        effect.addTrigger(trigger);
        effect.prime(context);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate();

        verifyNoInteractions(sound);
    }

    @Test
    public void activatePlaysSoundsOnlyForPlayerEntity() {
        EffectSource source = mock(EffectSource.class);
        Location playerLocation = new Location(null, 0, 0, 0);

        Player player = mock(Player.class);
        when(player.getLocation()).thenReturn(playerLocation);

        EffectContext context = new EffectContext(player, source, INITIATION_LOCATION);

        SoundNotificationEffect effect = new SoundNotificationEffect(sounds);
        effect.addTrigger(trigger);
        effect.prime(context);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate();

        verify(player).playSound(playerLocation, SOUND, VOLUME, PITCH);
    }
}
