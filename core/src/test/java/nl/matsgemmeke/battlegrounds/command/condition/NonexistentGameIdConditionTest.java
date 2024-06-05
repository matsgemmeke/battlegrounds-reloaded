package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import nl.matsgemmeke.battlegrounds.GameProvider;
import nl.matsgemmeke.battlegrounds.game.session.Session;
import nl.matsgemmeke.battlegrounds.locale.Translator;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NonexistentGameIdConditionTest {

    private BukkitCommandExecutionContext execContext;
    private ConditionContext<BukkitCommandIssuer> context;
    private GameProvider gameProvider;
    private Translator translator;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        this.execContext = mock(BukkitCommandExecutionContext.class);
        this.context = (ConditionContext<BukkitCommandIssuer>) mock(ConditionContext.class);
        this.gameProvider = mock(GameProvider.class);
        this.translator = mock(Translator.class);
    }

    @Test
    public void conditionShouldPassWhenSessionDoesNotExist() {
        int sessionId = 1;

        when(gameProvider.getSession(sessionId)).thenReturn(null);

        NonexistentSessionIdCondition condition = new NonexistentSessionIdCondition(gameProvider, translator);
        condition.validateCondition(context, execContext, sessionId);
    }

    @Test(expected = ConditionFailedException.class)
    public void conditionShouldNotPassWhenSessionExists() {
        Session session = mock(Session.class);

        int sessionId = 1;

        when(gameProvider.getSession(sessionId)).thenReturn(session);

        NonexistentSessionIdCondition condition = new NonexistentSessionIdCondition(gameProvider, translator);
        condition.validateCondition(context, execContext, sessionId);
    }
}
