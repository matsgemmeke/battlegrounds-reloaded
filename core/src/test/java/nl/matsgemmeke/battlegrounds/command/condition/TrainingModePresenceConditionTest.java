package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.EntityRegistry;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrainingModePresenceConditionTest {

    private BukkitCommandIssuer issuer;
    private ConditionContext<BukkitCommandIssuer> conditionContext;
    private EntityRegistry<GamePlayer, Player> playerRegistry;
    private GameContext gameContext;
    private Player player;
    private Translator translator;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        playerRegistry = (EntityRegistry<GamePlayer, Player>) mock(EntityRegistry.class);
        player = mock(Player.class);
        translator = mock(Translator.class);

        issuer = mock(BukkitCommandIssuer.class);
        when(issuer.getPlayer()).thenReturn(player);

        conditionContext = (ConditionContext<BukkitCommandIssuer>) mock(ConditionContext.class);
        when(conditionContext.getIssuer()).thenReturn(issuer);

        gameContext = mock(GameContext.class);
        when(gameContext.getPlayerRegistry()).thenReturn(playerRegistry);
    }

    @Test
    public void shouldPassWhenPlayerIsInTrainingMode() {
        when(playerRegistry.isRegistered(player)).thenReturn(true);

        TrainingModePresenceCondition condition = new TrainingModePresenceCondition(gameContext, translator);
        condition.validateCondition(conditionContext);
    }

    @Test(expected = ConditionFailedException.class)
    public void shouldNotPassWhenPlayerIsNotInTrainingMode() {
        when(playerRegistry.isRegistered(player)).thenReturn(false);
        when(translator.translate(TranslationKey.NOT_IN_TRAINING_MODE.getPath())).thenReturn(new TextTemplate("message"));

        TrainingModePresenceCondition condition = new TrainingModePresenceCondition(gameContext, translator);
        condition.validateCondition(conditionContext);
    }
}
