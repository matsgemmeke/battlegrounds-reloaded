package com.github.matsgemmeke.battlegounds.command.condition;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import com.github.matsgemmeke.battlegrounds.api.BattleContextProvider;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import com.github.matsgemmeke.battlegrounds.locale.Translator;
import com.github.matsgemmeke.battlegrounds.command.condition.NonexistentGameIdCondition;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NonexistentGameIdConditionTest {

    private BattleContextProvider contextProvider;
    private BukkitCommandExecutionContext execContext;
    private ConditionContext<BukkitCommandIssuer> context;
    private Translator translator;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        this.contextProvider = mock(BattleContextProvider.class);
        this.execContext = mock(BukkitCommandExecutionContext.class);
        this.context = (ConditionContext<BukkitCommandIssuer>) mock(ConditionContext.class);
        this.translator = mock(Translator.class);
    }

    @Test
    public void conditionShouldPassWhenGameDoesNotExist() {
        int gameId = 1;

        when(contextProvider.getGameContext(gameId)).thenReturn(null);

        NonexistentGameIdCondition condition = new NonexistentGameIdCondition(contextProvider, translator);
        condition.validateCondition(context, execContext, gameId);
    }

    @Test(expected = ConditionFailedException.class)
    public void conditionShouldNotPassWhenGameDoesNotExist() {
        GameContext gameContext = mock(GameContext.class);

        int gameId = 1;

        when(contextProvider.getGameContext(gameId)).thenReturn(gameContext);

        NonexistentGameIdCondition condition = new NonexistentGameIdCondition(contextProvider, translator);
        condition.validateCondition(context, execContext, gameId);
    }
}
