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

public class NonexistentSessionIdConditionTest {

    private BukkitCommandExecutionContext execContext;
    private ConditionContext<BukkitCommandIssuer> conditionContext;
    private GameContextProvider contextProvider;
    private Translator translator;

    @BeforeEach
    public void setUp() {
        this.execContext = mock(BukkitCommandExecutionContext.class);
        this.conditionContext = mock();
        this.contextProvider = mock(GameContextProvider.class);
        this.translator = mock(Translator.class);
    }

    @Test
    public void conditionShouldPassWhenSessionDoesNotExist() {
        int sessionId = 1;

        when(contextProvider.sessionExists(sessionId)).thenReturn(false);

        NonexistentSessionIdCondition condition = new NonexistentSessionIdCondition(contextProvider, translator);
        condition.validateCondition(conditionContext, execContext, sessionId);
    }

    @Test
    public void conditionShouldNotPassWhenSessionExists() {
        int sessionId = 1;

        when(contextProvider.sessionExists(sessionId)).thenReturn(true);
        when(translator.translate(TranslationKey.SESSION_ALREADY_EXISTS.getPath())).thenReturn(new TextTemplate("message"));

        NonexistentSessionIdCondition condition = new NonexistentSessionIdCondition(contextProvider, translator);

        assertThrows(ConditionFailedException.class, () -> condition.validateCondition(conditionContext, execContext, sessionId));
    }
}
