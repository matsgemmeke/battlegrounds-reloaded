package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.configuration.data.DataConfiguration;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SetMainLobbyCommandExecutorTest {

    private static final String MAIN_LOBBY_SET_MESSAGE = "hello";

    @Mock
    private DataConfiguration dataConfiguration;
    @Mock
    private Player player;
    @Mock
    private Translator translator;
    @InjectMocks
    private SetMainLobbyCommandExecutor commandExecutor;

    @Test
    @DisplayName("execute saves main lobby location to data configuration")
    void execute() {
        Block block = mock(Block.class);
        when(block.getLocation()).thenReturn(new Location(null, 1.0, 2.0, 3.0));

        Location playerLocation = mock(Location.class);
        when(playerLocation.getBlock()).thenReturn(block);

        when(player.getLocation()).thenReturn(playerLocation);
        when(translator.translate(TranslationKey.MAIN_LOBBY_SET.getPath())).thenReturn(new TextTemplate(MAIN_LOBBY_SET_MESSAGE));

        commandExecutor.execute(player);

        ArgumentCaptor<Location> savedLocationCaptor = ArgumentCaptor.forClass(Location.class);
        verify(dataConfiguration).setMainLobbyLocation(savedLocationCaptor.capture());

        assertThat(savedLocationCaptor.getValue()).satisfies(location -> {
            assertThat(location.getX()).isEqualTo(1.5);
            assertThat(location.getY()).isEqualTo(2.0);
            assertThat(location.getZ()).isEqualTo(3.5);
        });

        verify(player).sendMessage(MAIN_LOBBY_SET_MESSAGE);
    }
}
