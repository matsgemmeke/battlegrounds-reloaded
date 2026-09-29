package nl.matsgemmeke.battlegrounds.arena.command.condition;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelection;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelector;
import nl.matsgemmeke.battlegrounds.i18n.TextTemplate;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExistentElementIdConditionTest {

    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final int ELEMENT_ID = 5;
    private static final String MAP_NAME = "a cool map";
    private static final String PLAYER_ONLY_COMMAND_TEXT = "player only command";
    private static final String NO_MAP_SELECTED_TEXT = "no map selected";
    private static final String ELEMENT_NOT_EXISTS_TEXT = "element not exists";

    @Mock
    private ArenaMapSelector mapSelector;
    @Mock
    private BukkitCommandExecutionContext execContext;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BukkitCommandIssuer issuer;
    @Mock
    private ConditionContext<BukkitCommandIssuer> conditionContext;
    @Mock
    private Translator translator;
    @InjectMocks
    private ExistentElementIdCondition condition;
    @Captor
    private ArgumentCaptor<Map<String, Object>> valuesCaptor;

    @BeforeEach
    void setUp() {
        when(conditionContext.getIssuer()).thenReturn(issuer);
    }

    @Test
    @DisplayName("validateCondition throws ConditionFailedException when the issuer is not a player")
    void validateCondition_issuerIsNoPlayer() {
        when(issuer.getPlayer()).thenReturn(null);
        when(translator.translate(TranslationKey.PLAYER_ONLY_COMMAND.getPath())).thenReturn(new TextTemplate(PLAYER_ONLY_COMMAND_TEXT));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext, execContext, ELEMENT_ID))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(PLAYER_ONLY_COMMAND_TEXT);
    }

    @Test
    @DisplayName("validationCondition throws ConditionFailedException when player has not selected a map")
    void validationCondition_noMapSelected() {
        when(issuer.getPlayer().getUniqueId()).thenReturn(PLAYER_ID);
        when(mapSelector.getSelection(PLAYER_ID)).thenReturn(Optional.empty());
        when(translator.translate(TranslationKey.NO_MAP_SELECTED.getPath())).thenReturn(new TextTemplate(NO_MAP_SELECTED_TEXT));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext, execContext, ELEMENT_ID))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(NO_MAP_SELECTED_TEXT);
    }

    @Test
    @DisplayName("validationCondition throws ConditionFailedException when given element id does not exist in the selected map")
    void validationCondition_elementIdDoesNotExist() {
        TextTemplate elementNotExistsTextTemplate = mock(TextTemplate.class);
        when(elementNotExistsTextTemplate.replace(anyMap())).thenReturn(ELEMENT_NOT_EXISTS_TEXT);

        ArenaMap map = mock(ArenaMap.class);
        when(map.elementExists(ELEMENT_ID)).thenReturn(false);
        when(map.getName()).thenReturn(MAP_NAME);

        ArenaMapSelection mapSelection = new ArenaMapSelection(null, map);

        when(issuer.getPlayer().getUniqueId()).thenReturn(PLAYER_ID);
        when(mapSelector.getSelection(PLAYER_ID)).thenReturn(Optional.of(mapSelection));
        when(translator.translate(TranslationKey.ELEMENT_NOT_EXISTS.getPath())).thenReturn(elementNotExistsTextTemplate);

        assertThatThrownBy(() -> condition.validateCondition(conditionContext, execContext, ELEMENT_ID))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(ELEMENT_NOT_EXISTS_TEXT);

        verify(elementNotExistsTextTemplate).replace(valuesCaptor.capture());

        assertThat(valuesCaptor.getValue()).containsOnly(
                entry("bg_map_name", MAP_NAME),
                entry("bg_element_id", ELEMENT_ID)
        );
    }

    @Test
    @DisplayName("validateCondition does nothing when given element id exists in selected map")
    void validationCondition_successful() {
        ArenaMap map = mock(ArenaMap.class);
        when(map.elementExists(ELEMENT_ID)).thenReturn(true);

        ArenaMapSelection mapSelection = new ArenaMapSelection(null, map);

        when(issuer.getPlayer().getUniqueId()).thenReturn(PLAYER_ID);
        when(mapSelector.getSelection(PLAYER_ID)).thenReturn(Optional.of(mapSelection));

        assertThatNoException().isThrownBy(() -> condition.validateCondition(conditionContext, execContext, ELEMENT_ID));
    }
}
