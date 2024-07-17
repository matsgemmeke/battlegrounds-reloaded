package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class DefaultAudioEmitterTest {

    @Test
    public void doesNotPlaySoundIfGivenLocationHasNoWorld() {
        GameSound sound = mock(GameSound.class);

        Location location = new Location(null, 1.0, 1.0, 1.0);

        DefaultAudioEmitter audioEmitter = new DefaultAudioEmitter();
        audioEmitter.playSound(sound, location);

        verifyNoInteractions(sound);
    }

    @Test
    public void shouldPlaySoundToAllPlayersInTheWorldOfTheLocation() {
        GameSound sound = mock(GameSound.class);
        Player player = mock(Player.class);

        World world = mock(World.class);
        when(world.getPlayers()).thenReturn(List.of(player));

        Location location = new Location(world, 1.0, 1.0, 1.0);

        DefaultAudioEmitter audioEmitter = new DefaultAudioEmitter();
        audioEmitter.playSound(sound, location);

        verify(player).playSound(eq(location), (Sound) isNull(), anyFloat(), anyFloat());
    }

    @Test
    public void shouldPlayMultipleSoundsToAllPlayersInTheWorldOfTheLocation() {
        Iterable<GameSound> sounds = List.of(mock(GameSound.class), mock(GameSound.class));
        Player player = mock(Player.class);

        World world = mock(World.class);
        when(world.getPlayers()).thenReturn(List.of(player));

        Location location = new Location(world, 1.0, 1.0, 1.0);

        DefaultAudioEmitter audioEmitter = new DefaultAudioEmitter();
        audioEmitter.playSounds(sounds, location);

        verify(player, times(2)).playSound(eq(location), (Sound) isNull(), anyFloat(), anyFloat());
    }
}
