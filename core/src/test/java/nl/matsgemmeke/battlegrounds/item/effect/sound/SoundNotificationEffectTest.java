package nl.matsgemmeke.battlegrounds.item.effect.sound;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.util.Procedure;
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
    private ItemEffectActivation effectActivation;
    private List<GameSound> sounds;

    @BeforeEach
    public void setUp() {
        effectActivation = mock(ItemEffectActivation.class);

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

        SoundNotificationEffect effect = new SoundNotificationEffect(effectActivation, sounds);
        effect.prime(context);

        ArgumentCaptor<Procedure> procedureCaptor = ArgumentCaptor.forClass(Procedure.class);
        verify(effectActivation).prime(eq(context), procedureCaptor.capture());

        procedureCaptor.getValue().apply();

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

        SoundNotificationEffect effect = new SoundNotificationEffect(effectActivation, sounds);
        effect.prime(context);

        ArgumentCaptor<Procedure> procedureCaptor = ArgumentCaptor.forClass(Procedure.class);
        verify(effectActivation).prime(eq(context), procedureCaptor.capture());

        procedureCaptor.getValue().apply();

        verify(player).playSound(playerLocation, SOUND, VOLUME, PITCH);
    }
}
