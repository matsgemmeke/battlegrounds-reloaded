package nl.matsgemmeke.battlegrounds.arena.command.condition;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelection;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelector;
import nl.matsgemmeke.battlegrounds.i18n.TextTemplate;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapSelectedConditionTest {

    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final String NO_MAP_SELECTED_MESSAGE = "no map selected";

    @Mock
    private ArenaMapSelector mapSelector;
    @Mock
    private BukkitCommandIssuer issuer;
    @Mock
    private ConditionContext<BukkitCommandIssuer> conditionContext;
    @Mock
    private Player player;
    @Mock
    private Translator translator;
    @InjectMocks
    private MapSelectedCondition condition;

    @BeforeEach
    void setUp() {
        when(conditionContext.getIssuer()).thenReturn(issuer);
    }

    @Test
    @DisplayName("validateCondition throws ConditionFailedException when the condition context has no player")
    void validateCondition_conditionContextWithoutPlayer() {
        when(issuer.getPlayer()).thenReturn(null);
        when(translator.translate(TranslationKey.NO_MAP_SELECTED.getPath())).thenReturn(new TextTemplate(NO_MAP_SELECTED_MESSAGE));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(NO_MAP_SELECTED_MESSAGE);
    }

    @Test
    @DisplayName("validateCondition does nothing when player has a selected map")
    void validateCondition_successful() {
        ArenaMapSelection selection = new ArenaMapSelection(null, null);

        when(issuer.getPlayer()).thenReturn(player);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(mapSelector.getSelection(PLAYER_ID)).thenReturn(Optional.of(selection));

        assertThatCode(() -> condition.validateCondition(conditionContext)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateCondition throws ConditionFailedException when player has no map selected")
    void validateCondition_noMapSelected() {
        when(issuer.getPlayer()).thenReturn(player);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(mapSelector.getSelection(PLAYER_ID)).thenReturn(Optional.empty());
        when(translator.translate(TranslationKey.NO_MAP_SELECTED.getPath())).thenReturn(new TextTemplate(NO_MAP_SELECTED_MESSAGE));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(NO_MAP_SELECTED_MESSAGE);
    }
}
