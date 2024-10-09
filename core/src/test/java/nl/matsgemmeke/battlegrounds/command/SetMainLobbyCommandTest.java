package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.configuration.GeneralDataConfiguration;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class SetMainLobbyCommandTest {

    private GeneralDataConfiguration generalData;
    private Player player;
    private Translator translator;

    @Before
    public void setUp() {
        this.generalData = mock(GeneralDataConfiguration.class);
        this.player = mock(Player.class);
        this.translator = mock(Translator.class);

        when(translator.translate(TranslationKey.DESCRIPTION_SETMAINLOBBY.getPath())).thenReturn("description");
    }

    @Test
    public void shouldBeAbleToSetMainLobbyLocation() {
        Block block = mock(Block.class);
        Location location = mock(Location.class);
        String message = "hello";

        when(block.getLocation()).thenReturn(new Location(null, 1.0, 2.0, 3.0));
        when(location.getBlock()).thenReturn(block);
        when(player.getLocation()).thenReturn(location);
        when(translator.translate(TranslationKey.MAIN_LOBBY_SET.getPath())).thenReturn(message);

        SetMainLobbyCommand command = new SetMainLobbyCommand(generalData, translator);
        command.execute(player);

        verify(generalData).setMainLobbyLocation(any(Location.class));
        verify(player).sendMessage(message);
    }
}
