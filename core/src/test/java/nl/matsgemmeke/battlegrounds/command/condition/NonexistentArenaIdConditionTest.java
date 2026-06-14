package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonexistentArenaIdConditionTest {

    private static final int ARENA_ID = 1;
    private static final String ARENA_EXISTS_MESSAGE = "arena exists";

    @Mock
    private BukkitCommandExecutionContext execContext;
    @Mock
    private ConditionContext<BukkitCommandIssuer> conditionContext;
    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private Translator translator;
    @InjectMocks
    private NonexistentArenaIdCondition condition;

    @Test
    @DisplayName("validateCondition passes when arena does not exist")
    void validateCondition_arenaNotExists() {
        when(gameContextProvider.sessionExists(ARENA_ID)).thenReturn(false);

        assertThatCode(() -> condition.validateCondition(conditionContext, execContext, ARENA_ID)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateCondition throws ConditionFailedException when arena exists")
    void validateCondition_arenaExists() {
        when(gameContextProvider.sessionExists(ARENA_ID)).thenReturn(true);
        when(translator.translate(TranslationKey.ARENA_ALREADY_EXISTS.getPath())).thenReturn(new TextTemplate(ARENA_EXISTS_MESSAGE));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext, execContext, ARENA_ID))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(ARENA_EXISTS_MESSAGE);
    }
}
