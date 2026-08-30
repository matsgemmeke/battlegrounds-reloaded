package nl.matsgemmeke.battlegrounds.arena.command.executor.element;

import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfigurationProvider;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.spawn.CreateSpawnPointData;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.arena.map.element.SpawnPoint;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelection;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelector;
import nl.matsgemmeke.battlegrounds.i18n.TextTemplate;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddSpawnPointCommandExecutorTest {

    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final String PLAYER_NAME = "TestPlayer";

    private static final int ARENA_ID = 1;
    private static final String MAP_NAME = "Level 1";
    private static final int ELEMENT_ID = 2;
    private static final int TEAM_ID = 3;
    private static final double LOCATION_X = 1.1;
    private static final double LOCATION_Y = 2.2;
    private static final double LOCATION_Z = 3.3;

    private static final String GENERIC_ERROR_TEXT = "generic error";
    private static final String SPAWN_POINT_ADDED_TEXT = "spawn point added";

    @Mock
    private ArenaMapSelector mapSelector;
    @Mock
    private ArenaSetupConfigurationProvider arenaSetupConfigurationProvider;
    @Mock
    private Logger logger;
    @Mock
    private Player player;
    @Mock
    private Translator translator;
    @InjectMocks
    private AddSpawnPointCommandExecutor commandExecutor;
    @Captor
    private ArgumentCaptor<Map<String, Object>> valuesCaptor;

    @Test
    @DisplayName("execute sends an error message when the given player has no selected map")
    void execute_noSelectedMap() {
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(mapSelector.getSelection(PLAYER_ID)).thenReturn(Optional.empty());
        when(translator.translate(TranslationKey.GENERIC_ERROR.getPath())).thenReturn(new TextTemplate(GENERIC_ERROR_TEXT));
        when(player.getName()).thenReturn(PLAYER_NAME);

        commandExecutor.execute(player, TEAM_ID);

        verify(player).sendMessage(GENERIC_ERROR_TEXT);
        verify(logger).severe("Player TestPlayer has no map selection despite prior validation");
    }

    @Test
    @DisplayName("execute adds a spawn point to the map and the arena setup configuration")
    void execute_successful() {
        World world = mock(World.class);
        Location playerLocation = new Location(world, LOCATION_X, LOCATION_Y, LOCATION_Z);
        ArenaSetupConfiguration setupConfiguration = mock(ArenaSetupConfiguration.class);

        Arena arena = mock(Arena.class);
        when(arena.getId()).thenReturn(ARENA_ID);

        ArenaMap map = mock(ArenaMap.class);
        when(map.getName()).thenReturn(MAP_NAME);
        when(map.generateNextElementId()).thenReturn(ELEMENT_ID);

        TextTemplate spawnPointAddedTextTemplate = mock(TextTemplate.class);
        when(spawnPointAddedTextTemplate.replace(anyMap())).thenReturn(SPAWN_POINT_ADDED_TEXT);

        ArenaMapSelection selection = new ArenaMapSelection(arena, map);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(mapSelector.getSelection(PLAYER_ID)).thenReturn(Optional.of(selection));
        when(player.getLocation()).thenReturn(playerLocation);
        when(arenaSetupConfigurationProvider.get(ARENA_ID)).thenReturn(setupConfiguration);
        when(translator.translate(TranslationKey.SPAWN_POINT_ADDED.getPath())).thenReturn(spawnPointAddedTextTemplate);

        commandExecutor.execute(player, TEAM_ID);

        ArgumentCaptor<SpawnPoint> spawnPointCaptor = ArgumentCaptor.forClass(SpawnPoint.class);
        verify(map).addElement(spawnPointCaptor.capture());

        assertThat(spawnPointCaptor.getValue()).satisfies(spawnPoint -> {
            assertThat(spawnPoint.getId()).isEqualTo(ELEMENT_ID);
            assertThat(spawnPoint.getTeamId()).isEqualTo(TEAM_ID);
            assertThat(spawnPoint.getLocation()).satisfies(location -> {
                assertThat(location.getWorld()).isEqualTo(world);
                assertThat(location.getX()).isEqualTo(1.5);
                assertThat(location.getY()).isEqualTo(LOCATION_Y);
                assertThat(location.getZ()).isEqualTo(3.5);
            });
        });

        ArgumentCaptor<CreateSpawnPointData> dataCaptor = ArgumentCaptor.forClass(CreateSpawnPointData.class);
        verify(setupConfiguration).createSpawnPoint(dataCaptor.capture());

        assertThat(dataCaptor.getValue()).satisfies(data -> {
            assertThat(data.mapName()).isEqualTo(MAP_NAME);
            assertThat(data.elementId()).isEqualTo(ELEMENT_ID);
            assertThat(data.teamId()).isEqualTo(TEAM_ID);
            assertThat(data.location()).satisfies(location -> {
                assertThat(location.getWorld()).isEqualTo(world);
                assertThat(location.getX()).isEqualTo(1.5);
                assertThat(location.getY()).isEqualTo(LOCATION_Y);
                assertThat(location.getZ()).isEqualTo(3.5);
            });
        });

        verify(spawnPointAddedTextTemplate).replace(valuesCaptor.capture());

        assertThat(valuesCaptor.getValue()).contains(
                entry("bg_element_id", ELEMENT_ID),
                entry("bg_map_name", MAP_NAME)
        );

        verify(player).sendMessage(SPAWN_POINT_ADDED_TEXT);
    }
}
