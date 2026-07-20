package nl.matsgemmeke.battlegrounds.arena.command.executor;

import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfigurationProvider;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.MapCreationInfo;
import nl.matsgemmeke.battlegrounds.arena.exception.ArenaNotFoundException;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateMapCommandExecutorTest {

    private static final int ARENA_ID = 1;
    private static final String MAP_NAME = "Level 1";
    private static final Instant INSTANT = Instant.parse("2026-01-01T12:00:00.00Z");
    private static final UUID PLAYER_ID = UUID.randomUUID();

    @Mock
    private ArenaRegistry arenaRegistry;
    @Mock
    private ArenaSetupConfigurationProvider arenaSetupConfigurationProvider;
    @Spy
    private Clock clock = Clock.fixed(INSTANT, ZoneOffset.UTC);
    @Mock
    private Player player;
    @Mock
    private Translator translator;
    @InjectMocks
    private CreateMapCommandExecutor commandExecutor;
    @Captor
    private ArgumentCaptor<Map<String, Object>> textTemplateValuesCaptor;

    @Test
    @DisplayName("execute throws ArenaNotFoundException when no arena instance is found for given arena id")
    void execute_arenaNotFound() {
        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commandExecutor.execute(player, ARENA_ID, MAP_NAME))
                .isInstanceOf(ArenaNotFoundException.class)
                .hasMessage("Received a supposedly validated arena id 1, but the arena instance is not present");
    }

    @Test
    @DisplayName("execute adds new map section to arena setup configuration and sends success message")
    void execute_successful() {
        Arena arena = mock(Arena.class);
        ArenaSetupConfiguration arenaSetupConfiguration = mock(ArenaSetupConfiguration.class);
        TextTemplate mapCreatedTextTemplate = mock(TextTemplate.class);

        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.of(arena));
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(arenaSetupConfigurationProvider.get(ARENA_ID)).thenReturn(arenaSetupConfiguration);
        when(translator.translate(TranslationKey.MAP_CREATED.getPath())).thenReturn(mapCreatedTextTemplate);

        commandExecutor.execute(player, ARENA_ID, MAP_NAME);

        ArgumentCaptor<ArenaMap> mapCaptor = ArgumentCaptor.forClass(ArenaMap.class);
        verify(arena).addMap(mapCaptor.capture());

        assertThat(mapCaptor.getValue().getName()).isEqualTo(MAP_NAME);

        ArgumentCaptor<MapCreationInfo> mapCreationInfoCaptor = ArgumentCaptor.forClass(MapCreationInfo.class);
        verify(arenaSetupConfiguration).createMap(mapCreationInfoCaptor.capture());

        assertThat(mapCreationInfoCaptor.getValue()).satisfies(mapCreationInfo -> {
            assertThat(mapCreationInfo.mapName()).isEqualTo(MAP_NAME);
            assertThat(mapCreationInfo.createdAt()).isEqualTo(INSTANT);
            assertThat(mapCreationInfo.createdBy()).isEqualTo(PLAYER_ID);
        });

        verify(mapCreatedTextTemplate).replace(textTemplateValuesCaptor.capture());

        assertThat(textTemplateValuesCaptor.getValue()).contains(
                entry("bg_arena", ARENA_ID),
                entry("bg_map", MAP_NAME)
        );

        verify(arenaSetupConfiguration).save();
    }
}
