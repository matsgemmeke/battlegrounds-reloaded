package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExistentSessionIdConditionTest {

    private BukkitCommandExecutionContext execContext;
    private ConditionContext<BukkitCommandIssuer> conditionContext;
    private GameContextProvider contextProvider;
    private Translator translator;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() {
        execContext = mock(BukkitCommandExecutionContext.class);
        conditionContext = (ConditionContext<BukkitCommandIssuer>) mock(ConditionContext.class);
        contextProvider = mock(GameContextProvider.class);
        translator = mock(Translator.class);
    }

    @Test
    public void conditionShouldPassWhenSessionExists() {
        int sessionId = 1;

        when(contextProvider.sessionExists(sessionId)).thenReturn(true);

        ExistentSessionIdCondition condition = new ExistentSessionIdCondition(contextProvider, translator);
        condition.validateCondition(conditionContext, execContext, sessionId);
    }

    @Test
    public void conditionShouldNotPassWhenSessionDoesNotExist() {
        int sessionId = 1;

        when(contextProvider.sessionExists(sessionId)).thenReturn(false);
        when(translator.translate(TranslationKey.SESSION_NOT_EXISTS.getPath())).thenReturn(new TextTemplate("message"));

        ExistentSessionIdCondition condition = new ExistentSessionIdCondition(contextProvider, translator);

        assertThrows(ConditionFailedException.class, () -> condition.validateCondition(conditionContext, execContext, sessionId));
    }
}
