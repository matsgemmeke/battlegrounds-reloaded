package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrainingModePresenceConditionTest {

    private BukkitCommandIssuer issuer;
    private ConditionContext<BukkitCommandIssuer> conditionContext;
    private GameContextProvider contextProvider;
    private GameKey trainingModeGameKey;
    private Player player;
    private PlayerRegistry playerRegistry;
    private Translator translator;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() {
        playerRegistry = mock(PlayerRegistry.class);
        trainingModeGameKey = GameKey.ofTrainingMode();
        player = mock(Player.class);
        translator = mock(Translator.class);

        issuer = mock(BukkitCommandIssuer.class);
        when(issuer.getPlayer()).thenReturn(player);

        conditionContext = (ConditionContext<BukkitCommandIssuer>) mock(ConditionContext.class);
        when(conditionContext.getIssuer()).thenReturn(issuer);

        contextProvider = mock(GameContextProvider.class);
        when(contextProvider.getComponent(trainingModeGameKey, PlayerRegistry.class)).thenReturn(playerRegistry);
    }

    @Test
    public void shouldPassWhenPlayerIsInTrainingMode() {
        when(playerRegistry.isRegistered(player)).thenReturn(true);

        TrainingModePresenceCondition condition = new TrainingModePresenceCondition(contextProvider, trainingModeGameKey, translator);
        condition.validateCondition(conditionContext);
    }

    @Test
    public void shouldNotPassWhenPlayerIsNotInTrainingMode() {
        when(playerRegistry.isRegistered(player)).thenReturn(false);
        when(translator.translate(TranslationKey.NOT_IN_TRAINING_MODE.getPath())).thenReturn(new TextTemplate("message"));

        TrainingModePresenceCondition condition = new TrainingModePresenceCondition(contextProvider, trainingModeGameKey, translator);

        assertThrows(ConditionFailedException.class, () -> condition.validateCondition(conditionContext));
    }
}
