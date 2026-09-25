package nl.matsgemmeke.battlegrounds.arena.loading;

import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.configuration.settings.*;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfigurationProvider;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.ElementData;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.map.ArenaMapData;
import nl.matsgemmeke.battlegrounds.arena.loading.element.CompositeElementFactory;
import nl.matsgemmeke.battlegrounds.arena.map.element.Element;
import nl.matsgemmeke.battlegrounds.arena.mapper.ArenaSettingsMapper;
import nl.matsgemmeke.battlegrounds.configuration.model.LocationData;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.util.world.LocationMapper;
import org.bukkit.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArenaLoaderTest {

    private static final int ARENA_ID = 1;
    private static final int LOBBY_COUNTDOWN_LENGTH = 30;
    private static final int MAX_PLAYERS = 12;
    private static final int MIN_PLAYERS = 2;

    private static final String MAP_NAME = "Level 1";
    private static final Instant MAP_CREATED_AT = Instant.parse("2026-01-01T12:00:00.00Z");
    private static final UUID MAP_CREATED_BY = UUID.randomUUID();

    @Mock
    private ArenaRegistry arenaRegistry;
    @Mock
    private ArenaSettingsConfigurationProvider arenaSettingsConfigurationProvider;
    @Spy
    private ArenaSettingsMapper arenaSettingsMapper;
    @Mock
    private ArenaSetupConfigurationProvider arenaSetupConfigurationProvider;
    @Mock
    private CompositeElementFactory compositeElementFactory;
    @Mock
    private LocationMapper locationMapper;
    @InjectMocks
    private ArenaLoader arenaLoader;

    @Test
    @DisplayName("loadArena throws InvalidArenaSetupException when failing to load arena settings")
    void loadArena_settingsLoadFail() {
        ArenaSettingsConfiguration settingsConfiguration = mock(ArenaSettingsConfiguration.class);
        when(settingsConfiguration.getArenaSettings()).thenThrow(new InvalidArenaSettingsSpecException("error"));

        when(arenaSettingsConfigurationProvider.get(ARENA_ID)).thenReturn(settingsConfiguration);

        assertThatThrownBy(() -> arenaLoader.loadArena(ARENA_ID))
                .isInstanceOf(InvalidArenaSetupException.class)
                .hasMessage("Failed to load setup for arena 1");
    }

    @Test
    @DisplayName("loadArena loads setup with lobby location from configuration files")
    void loadArena_withoutLobby() {
        ArenaSettingsSpec settingsSpec = new ArenaSettingsSpec(LOBBY_COUNTDOWN_LENGTH, MAX_PLAYERS, MIN_PLAYERS);

        ArenaSettingsConfiguration settingsConfiguration = mock(ArenaSettingsConfiguration.class);
        when(settingsConfiguration.getArenaSettings()).thenReturn(settingsSpec);

        ArenaSetupConfiguration setupConfiguration = mock(ArenaSetupConfiguration.class);
        when(setupConfiguration.getLobby()).thenReturn(Optional.empty());
        when(setupConfiguration.getMaps()).thenReturn(List.of());

        when(arenaSettingsConfigurationProvider.get(ARENA_ID)).thenReturn(settingsConfiguration);
        when(arenaSetupConfigurationProvider.get(ARENA_ID)).thenReturn(setupConfiguration);

        arenaLoader.loadArena(ARENA_ID);

        ArgumentCaptor<Arena> arenaCaptor = ArgumentCaptor.forClass(Arena.class);
        verify(arenaRegistry).addArena(eq(GameKey.ofArena(ARENA_ID)), arenaCaptor.capture());

        assertThat(arenaCaptor.getValue()).satisfies(arena -> {
            assertThat(arena.getLobbyLocation()).isEmpty();
        });
    }

    @Test
    @DisplayName("loadArena loads complete setup from configuration files and registers new arena instance to the registry")
    void loadArena_successful() {
        ArenaSettingsSpec settingsSpec = new ArenaSettingsSpec(LOBBY_COUNTDOWN_LENGTH, MAX_PLAYERS, MIN_PLAYERS);
        ElementData elementData = mock(ElementData.class);
        Element element = mock(Element.class);
        ArenaMapData mapData = new ArenaMapData(MAP_NAME, MAP_CREATED_AT, MAP_CREATED_BY, List.of(elementData));
        LocationData lobbyLocationData = new LocationData(null, null, null, null, null, null);
        Location lobbyLocation = new Location(null, 1.1, 2.2, 3.3, 90.0f, 0.0f);

        ArenaSettingsConfiguration settingsConfiguration = mock(ArenaSettingsConfiguration.class);
        when(settingsConfiguration.getArenaSettings()).thenReturn(settingsSpec);

        ArenaSetupConfiguration setupConfiguration = mock(ArenaSetupConfiguration.class);
        when(setupConfiguration.getLobby()).thenReturn(Optional.of(lobbyLocationData));
        when(setupConfiguration.getMaps()).thenReturn(List.of(mapData));

        when(arenaSettingsConfigurationProvider.get(ARENA_ID)).thenReturn(settingsConfiguration);
        when(arenaSetupConfigurationProvider.get(ARENA_ID)).thenReturn(setupConfiguration);
        when(locationMapper.toLocation(lobbyLocationData)).thenReturn(lobbyLocation);
        when(compositeElementFactory.create(elementData)).thenReturn(element);

        arenaLoader.loadArena(ARENA_ID);

        ArgumentCaptor<Arena> arenaCaptor = ArgumentCaptor.forClass(Arena.class);
        verify(arenaRegistry).addArena(eq(GameKey.ofArena(ARENA_ID)), arenaCaptor.capture());

        assertThat(arenaCaptor.getValue()).satisfies(arena -> {
            assertThat(arena.getId()).isEqualTo(ARENA_ID);
            assertThat(arena.getSettings()).satisfies(settings -> {
                assertThat(settings.getLobbyCountdownLength()).isEqualTo(LOBBY_COUNTDOWN_LENGTH);
                assertThat(settings.getMaxPlayers()).isEqualTo(MAX_PLAYERS);
                assertThat(settings.getMinPlayers()).isEqualTo(MIN_PLAYERS);
            });
            assertThat(arena.getLobbyLocation()).hasValue(lobbyLocation);
            assertThat(arena.getMaps()).satisfiesExactly(map -> {
                assertThat(map.getName()).isEqualTo(MAP_NAME);
                assertThat(map.getMetadata().createdAt()).isEqualTo(MAP_CREATED_AT);
                assertThat(map.getMetadata().createdBy()).isEqualTo(MAP_CREATED_BY);
            });
        });
    }
}
