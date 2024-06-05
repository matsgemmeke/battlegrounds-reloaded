package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import nl.matsgemmeke.battlegrounds.game.training.TrainingMode;
import nl.matsgemmeke.battlegrounds.locale.Translator;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrainingModePresenceConditionTest {

    private BukkitCommandIssuer issuer;
    private ConditionContext<BukkitCommandIssuer> conditionContext;
    private Player player;
    private TrainingMode trainingMode;
    private Translator translator;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        this.issuer = mock(BukkitCommandIssuer.class);
        this.conditionContext = (ConditionContext<BukkitCommandIssuer>) mock(ConditionContext.class);
        this.player = mock(Player.class);
        this.trainingMode = mock(TrainingMode.class);
        this.translator = mock(Translator.class);

        when(issuer.getPlayer()).thenReturn(player);
        when(conditionContext.getIssuer()).thenReturn(issuer);
    }

    @Test
    public void shouldPassWhenPlayerIsInTrainingMode() {
        when(trainingMode.hasPlayer(player)).thenReturn(true);

        TrainingModePresenceCondition condition = new TrainingModePresenceCondition(trainingMode, translator);
        condition.validateCondition(conditionContext);
    }

    @Test(expected = ConditionFailedException.class)
    public void shouldNotPassWhenPlayerIsNotInTrainingMode() {
        when(trainingMode.hasPlayer(player)).thenReturn(false);

        TrainingModePresenceCondition condition = new TrainingModePresenceCondition(trainingMode, translator);
        condition.validateCondition(conditionContext);
    }
}
