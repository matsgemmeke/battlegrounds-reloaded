package nl.matsgemmeke.battlegrounds.arena.command.executor.element;

import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfigurationProvider;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.arena.map.element.Element;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelection;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelector;
import nl.matsgemmeke.battlegrounds.fixture.LanguageFixture;
import nl.matsgemmeke.battlegrounds.i18n.TextTemplate;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveElementCommandExecutorTest {

    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final String PLAYER_NAME = "TestPlayer";
    private static final int ARENA_ID = 1;
    private static final String MAP_NAME = "Level 1";
    private static final int ELEMENT_ID = 5;

    private static final String NO_MAP_SELECTED_TEXT = LanguageFixture.getTranslation(TranslationKey.NO_MAP_SELECTED.getPath());
    private static final String ELEMENT_NOT_EXISTS_TEXT = LanguageFixture.getTranslation(TranslationKey.ELEMENT_NOT_EXISTS.getPath());
    private static final String ELEMENT_REMOVE_SUCCESSFUL_TEXT = LanguageFixture.getTranslation(TranslationKey.ELEMENT_REMOVE_SUCCESSFUL.getPath());

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
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(mapSelector.getSelection(PLAYER_ID)).thenReturn(Optional.empty());
        when(translator.translate(TranslationKey.NO_MAP_SELECTED.getPath())).thenReturn(new TextTemplate(NO_MAP_SELECTED_TEXT));
        when(player.getName()).thenReturn(PLAYER_NAME);

        commandExecutor.execute(player, ELEMENT_ID);

        verify(player).sendMessage(NO_MAP_SELECTED_TEXT);
        verify(logger).severe("Player TestPlayer attempted to remove element 5 without map selection despite prior validation");
    }

    @Test
    @DisplayName("execute logs error message when the given element id somehow doesn't exist")
    void execute_invalidElementId() {
        Arena arena = mock(Arena.class);
        when(arena.getId()).thenReturn(ARENA_ID);

        ArenaMap map = mock(ArenaMap.class);
        when(map.getElement(ELEMENT_ID)).thenReturn(Optional.empty());
        when(map.getName()).thenReturn(MAP_NAME);

        ArenaMapSelection mapSelection = new ArenaMapSelection(arena, map);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(mapSelector.getSelection(PLAYER_ID)).thenReturn(Optional.of(mapSelection));
        when(translator.translate(TranslationKey.ELEMENT_NOT_EXISTS.getPath())).thenReturn(new TextTemplate(ELEMENT_NOT_EXISTS_TEXT));
        when(player.getName()).thenReturn(PLAYER_NAME);

        commandExecutor.execute(player, ELEMENT_ID);

        verify(player).sendMessage("&cThe selected map Level 1 does not contain an element with the id 5.");
        verify(logger).severe("Player TestPlayer attempted to remove element 5 in map Level 1 in arena 1, but the element id is invalid despite prior validation");
    }

    @Test
    @DisplayName("execute removes selected element from setup configuration and sends confirmation message")
    void execute_successful() {
        Element element = mock(Element.class);
        ArenaSetupConfiguration arenaSetupConfiguration = mock(ArenaSetupConfiguration.class);

        Arena arena = mock(Arena.class);
        when(arena.getId()).thenReturn(ARENA_ID);

        ArenaMap map = mock(ArenaMap.class);
        when(map.getElement(ELEMENT_ID)).thenReturn(Optional.of(element));
        when(map.getName()).thenReturn(MAP_NAME);

        ArenaMapSelection mapSelection = new ArenaMapSelection(arena, map);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(mapSelector.getSelection(PLAYER_ID)).thenReturn(Optional.of(mapSelection));
        when(arenaSetupConfigurationProvider.get(ARENA_ID)).thenReturn(arenaSetupConfiguration);
        when(translator.translate(TranslationKey.ELEMENT_REMOVE_SUCCESSFUL.getPath())).thenReturn(new TextTemplate(ELEMENT_REMOVE_SUCCESSFUL_TEXT));

        commandExecutor.execute(player, ELEMENT_ID);

        verify(map).removeElement(element);
        verify(arenaSetupConfiguration).removeElement(MAP_NAME, ELEMENT_ID);
        verify(player).sendMessage("&6You have removed element with id &f5 &6from map &fLevel 1&6.");
    }
}
