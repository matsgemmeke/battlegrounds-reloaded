package nl.matsgemmeke.battlegrounds.arena.command.executor;

import nl.matsgemmeke.battlegrounds.arena.configuration.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.ArenaSetupConfigurationFactory;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateMapCommandExecutorTest {

    private static final int ARENA_ID = 1;
    private static final String MAP_NAME = "Level 1";

    @Mock
    private ArenaSetupConfigurationFactory arenaSetupConfigurationFactory;
    @Mock
    private CommandSender sender;
    @Mock
    private Translator translator;
    @InjectMocks
    private CreateMapCommandExecutor commandExecutor;
    @Captor
    private ArgumentCaptor<Map<String, Object>> textTemplateValuesCaptor;

    @Test
    @DisplayName("execute adds new map section to arena setup configuration and sends success message")
    void execute() {
        ArenaSetupConfiguration arenaSetupConfiguration = mock(ArenaSetupConfiguration.class);
        TextTemplate mapCreatedTextTemplate = mock(TextTemplate.class);

        when(arenaSetupConfigurationFactory.create(ARENA_ID)).thenReturn(arenaSetupConfiguration);
        when(translator.translate(TranslationKey.MAP_CREATED.getPath())).thenReturn(mapCreatedTextTemplate);

        commandExecutor.execute(sender, ARENA_ID, MAP_NAME);

        verify(mapCreatedTextTemplate).replace(textTemplateValuesCaptor.capture());

        assertThat(textTemplateValuesCaptor.getValue()).contains(
                entry("bg_arena", ARENA_ID),
                entry("bg_map", MAP_NAME)
        );

        verify(arenaSetupConfiguration).createMap(MAP_NAME);
        verify(arenaSetupConfiguration).save();
    }
}
