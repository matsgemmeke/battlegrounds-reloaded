package nl.matsgemmeke.battlegrounds.arena.command.executor.element;

import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfigurationProvider;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.arena.map.element.Element;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelection;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelector;
import nl.matsgemmeke.battlegrounds.i18n.*;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveElementCommandExecutorTest {

    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final String PLAYER_NAME = "TestPlayer";
    private static final int ARENA_ID = 1;
    private static final String MAP_NAME = "Level 1";
    private static final int ELEMENT_ID = 5;

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
    private RemoveElementCommandExecutor commandExecutor;

    @Test
    @DisplayName("execute logs error message when the player somehow has no map selection")
    void execute_noSelectedMap() {
        Message noMapSelectedMessage = mock(Message.class);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(mapSelector.getSelection(PLAYER_ID)).thenReturn(Optional.empty());
        when(translator.translateMessage(TranslationKey.NO_MAP_SELECTED.getPath())).thenReturn(Optional.of(noMapSelectedMessage));
        when(player.getName()).thenReturn(PLAYER_NAME);

        commandExecutor.execute(player, ELEMENT_ID);

        verify(logger).severe("Player TestPlayer attempted to remove element 5 without map selection despite prior validation");
        verify(noMapSelectedMessage).send(player);
    }

    @Test
    @DisplayName("execute logs error message when the given element id somehow doesn't exist")
    void execute_invalidElementId() {
        Message elementNotExistsMessage = mock(Message.class);

        Arena arena = mock(Arena.class);
        when(arena.getId()).thenReturn(ARENA_ID);

        ArenaMap map = mock(ArenaMap.class);
        when(map.getElement(ELEMENT_ID)).thenReturn(Optional.empty());
        when(map.getName()).thenReturn(MAP_NAME);

        ArenaMapSelection mapSelection = new ArenaMapSelection(arena, map);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(mapSelector.getSelection(PLAYER_ID)).thenReturn(Optional.of(mapSelection));
        when(translator.translateMessage(eq(TranslationKey.ELEMENT_NOT_EXISTS.getPath()), any(Replacement[].class))).thenReturn(Optional.of(elementNotExistsMessage));
        when(player.getName()).thenReturn(PLAYER_NAME);

        commandExecutor.execute(player, ELEMENT_ID);

        ArgumentCaptor<Replacement[]> replacementsCaptor = ArgumentCaptor.forClass(Replacement[].class);
        verify(translator).translateMessage(eq(TranslationKey.ELEMENT_NOT_EXISTS.getPath()), replacementsCaptor.capture());

        assertThat(replacementsCaptor.getValue()).satisfiesExactly(replacement -> {
            assertThat(replacement.key()).isEqualTo("map-name");
            assertThat(replacement.value()).isEqualTo(MAP_NAME);
        }, replacement -> {
            assertThat(replacement.key()).isEqualTo("element-id");
            assertThat(replacement.value()).isEqualTo("5");
        });

        verify(logger).severe("Player TestPlayer attempted to remove element 5 in map Level 1 in arena 1, but the element id is invalid despite prior validation");
        verify(elementNotExistsMessage).send(player);
    }

    @Test
    @DisplayName("execute removes selected element from setup configuration and sends confirmation message")
    void execute_successful() {
        Element element = mock(Element.class);
        ArenaSetupConfiguration arenaSetupConfiguration = mock(ArenaSetupConfiguration.class);
        Message elementRemoveSuccessfulMessage = mock(Message.class);

        Arena arena = mock(Arena.class);
        when(arena.getId()).thenReturn(ARENA_ID);

        ArenaMap map = mock(ArenaMap.class);
        when(map.getElement(ELEMENT_ID)).thenReturn(Optional.of(element));
        when(map.getName()).thenReturn(MAP_NAME);

        ArenaMapSelection mapSelection = new ArenaMapSelection(arena, map);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(mapSelector.getSelection(PLAYER_ID)).thenReturn(Optional.of(mapSelection));
        when(arenaSetupConfigurationProvider.get(ARENA_ID)).thenReturn(arenaSetupConfiguration);
        when(translator.translateMessage(eq(TranslationKey.ELEMENT_REMOVE_SUCCESSFUL.getPath()), any(Replacement[].class))).thenReturn(Optional.of(elementRemoveSuccessfulMessage));

        commandExecutor.execute(player, ELEMENT_ID);

        ArgumentCaptor<Replacement[]> replacementsCaptor = ArgumentCaptor.forClass(Replacement[].class);
        verify(translator).translateMessage(eq(TranslationKey.ELEMENT_REMOVE_SUCCESSFUL.getPath()), replacementsCaptor.capture());

        assertThat(replacementsCaptor.getValue()).satisfiesExactly(replacement -> {
            assertThat(replacement.key()).isEqualTo("map-name");
            assertThat(replacement.value()).isEqualTo(MAP_NAME);
        }, replacement -> {
            assertThat(replacement.key()).isEqualTo("element-id");
            assertThat(replacement.value()).isEqualTo("5");
        });

        verify(map).removeElement(element);
        verify(arenaSetupConfiguration).removeElement(MAP_NAME, ELEMENT_ID);
        verify(elementRemoveSuccessfulMessage).send(player);
    }
}
