package nl.matsgemmeke.battlegrounds.item.effect.sound;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class SoundNotificationEffectTest {

    private static final Sound SOUND = Sound.AMBIENT_CAVE;
    private static final float VOLUME = 1.0f;
    private static final float PITCH = 2.0f;

    private GameSound sound;
    private List<GameSound> sounds;

    @BeforeEach
    public void setUp() {
        sound = mock(GameSound.class);
        when(sound.getSound()).thenReturn(SOUND);
        when(sound.getVolume()).thenReturn(VOLUME);
        when(sound.getPitch()).thenReturn(PITCH);

        sounds = List.of(sound);
    }

    @Test
    public void activatePlaysNoSoundsIfItemHolderEntityIsNoPlayer() {
        Zombie zombie = mock(Zombie.class);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getEntity()).thenReturn(zombie);

        EffectSource effectSource = mock(EffectSource.class);

        ItemEffectContext context = new ItemEffectContext(holder, effectSource);

        SoundNotificationEffect effect = new SoundNotificationEffect(sounds);
        effect.activate(context);

        verifyNoInteractions(sound);
    }

    @Test
    public void activatePlaysSoundsOnlyForItemHolder() {
        Location playerLocation = new Location(null, 0, 0, 0);

        Player player = mock(Player.class);
        when(player.getLocation()).thenReturn(playerLocation);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getEntity()).thenReturn(player);

        EffectSource effectSource = mock(EffectSource.class);

        ItemEffectContext context = new ItemEffectContext(holder, effectSource);

        SoundNotificationEffect effect = new SoundNotificationEffect(sounds);
        effect.activate(context);

        verify(player).playSound(playerLocation, SOUND, VOLUME, PITCH);
    }
}
