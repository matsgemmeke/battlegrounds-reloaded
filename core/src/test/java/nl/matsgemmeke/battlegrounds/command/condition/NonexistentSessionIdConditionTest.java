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

public class NonexistentSessionIdConditionTest {

    private BukkitCommandExecutionContext execContext;
    private ConditionContext<BukkitCommandIssuer> conditionContext;
    private GameContextProvider contextProvider;
    private Translator translator;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        this.execContext = mock(BukkitCommandExecutionContext.class);
        this.conditionContext = (ConditionContext<BukkitCommandIssuer>) mock(ConditionContext.class);
        this.contextProvider = mock(GameContextProvider.class);
        this.translator = mock(Translator.class);
    }

    @Test
    public void conditionShouldPassWhenSessionDoesNotExist() {
        int sessionId = 1;

        when(contextProvider.getSessionContext(sessionId)).thenReturn(null);

        NonexistentSessionIdCondition condition = new NonexistentSessionIdCondition(contextProvider, translator);
        condition.validateCondition(conditionContext, execContext, sessionId);
    }

    @Test(expected = ConditionFailedException.class)
    public void conditionShouldNotPassWhenSessionExists() {
        GameContext sessionContext = mock(GameContext.class);

        int sessionId = 1;

        when(contextProvider.getSessionContext(sessionId)).thenReturn(sessionContext);

        NonexistentSessionIdCondition condition = new NonexistentSessionIdCondition(contextProvider, translator);
        condition.validateCondition(conditionContext, execContext, sessionId);
    }
}
