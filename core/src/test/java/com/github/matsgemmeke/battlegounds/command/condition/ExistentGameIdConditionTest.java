package com.github.matsgemmeke.battlegounds.command.condition;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import com.github.matsgemmeke.battlegrounds.api.GameProvider;
import com.github.matsgemmeke.battlegrounds.api.game.Session;
import com.github.matsgemmeke.battlegrounds.command.condition.ExistentSessionIdCondition;
import com.github.matsgemmeke.battlegrounds.locale.Translator;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ExistentGameIdConditionTest {

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
    public void conditionShouldPassWhenSessionExists() {
        Session session = mock(Session.class);

        int sessionId = 1;

        when(gameProvider.getSession(sessionId)).thenReturn(session);

        ExistentSessionIdCondition condition = new ExistentSessionIdCondition(gameProvider, translator);
        condition.validateCondition(context, execContext, sessionId);
    }

    @Test(expected = ConditionFailedException.class)
    public void conditionShouldNotPassWhenSessionDoesNotExist() {
        int sessionId = 1;

        when(gameProvider.getSession(sessionId)).thenReturn(null);

        ExistentSessionIdCondition condition = new ExistentSessionIdCondition(gameProvider, translator);
        condition.validateCondition(context, execContext, sessionId);
    }
}
