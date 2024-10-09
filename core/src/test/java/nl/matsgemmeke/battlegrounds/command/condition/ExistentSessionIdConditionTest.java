package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import nl.matsgemmeke.battlegrounds.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExistentSessionIdConditionTest {

    private BukkitCommandExecutionContext execContext;
    private ConditionContext<BukkitCommandIssuer> conditionContext;
    private GameContextProvider contextProvider;
    private Translator translator;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        execContext = mock(BukkitCommandExecutionContext.class);
        conditionContext = (ConditionContext<BukkitCommandIssuer>) mock(ConditionContext.class);
        contextProvider = mock(GameContextProvider.class);
        translator = mock(Translator.class);
    }

    @Test
    public void conditionShouldPassWhenSessionExists() {
        GameContext sessionContext = mock(GameContext.class);

        int sessionId = 1;

        when(contextProvider.getSessionContext(sessionId)).thenReturn(sessionContext);

        ExistentSessionIdCondition condition = new ExistentSessionIdCondition(contextProvider, translator);
        condition.validateCondition(conditionContext, execContext, sessionId);
    }

    @Test(expected = ConditionFailedException.class)
    public void conditionShouldNotPassWhenSessionDoesNotExist() {
        int sessionId = 1;

        when(contextProvider.getSessionContext(sessionId)).thenReturn(null);

        ExistentSessionIdCondition condition = new ExistentSessionIdCondition(contextProvider, translator);
        condition.validateCondition(conditionContext, execContext, sessionId);
    }
}
