package nl.matsgemmeke.battlegrounds.item.effect.sound;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.TriggerObserver;
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
        Deployer deployer = mock(Deployer.class);
        Zombie zombie = mock(Zombie.class);
        ItemEffectSource source = mock(ItemEffectSource.class);
        ItemEffectContext context = new ItemEffectContext(deployer, zombie, source);

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
        Deployer deployer = mock(Deployer.class);
        ItemEffectSource source = mock(ItemEffectSource.class);
        Location playerLocation = new Location(null, 0, 0, 0);

        Player player = mock(Player.class);
        when(player.getLocation()).thenReturn(playerLocation);

        ItemEffectContext context = new ItemEffectContext(deployer, player, source);

        SoundNotificationEffect effect = new SoundNotificationEffect(sounds);
        effect.addTrigger(trigger);
        effect.prime(context);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate();

        verify(player).playSound(playerLocation, SOUND, VOLUME, PITCH);
    }
}
