package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.configuration.data.DataConfiguration;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class SetMainLobbyCommandTest {

    private DataConfiguration dataConfiguration;
    private Player player;
    private Translator translator;

    @BeforeEach
    public void setUp() {
        this.dataConfiguration = mock(DataConfiguration.class);
        this.player = mock(Player.class);
        this.translator = mock(Translator.class);

        when(translator.translate(TranslationKey.DESCRIPTION_SETMAINLOBBY.getPath())).thenReturn(new TextTemplate("description"));
    }

    @Test
    public void shouldBeAbleToSetMainLobbyLocation() {
        Block block = mock(Block.class);
        Location location = mock(Location.class);
        String message = "hello";

        when(block.getLocation()).thenReturn(new Location(null, 1.0, 2.0, 3.0));
        when(location.getBlock()).thenReturn(block);
        when(player.getLocation()).thenReturn(location);
        when(translator.translate(TranslationKey.MAIN_LOBBY_SET.getPath())).thenReturn(new TextTemplate(message));

        SetMainLobbyCommand command = new SetMainLobbyCommand(dataConfiguration, translator);
        command.execute(player);

        verify(dataConfiguration).setMainLobbyLocation(any(Location.class));
        verify(player).sendMessage(message);
    }
}
