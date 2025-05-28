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

public class OpenModePresenceConditionTest {

    private BukkitCommandIssuer issuer;
    private ConditionContext<BukkitCommandIssuer> conditionContext;
    private GameContextProvider contextProvider;
    private GameKey openModeGameKey;
    private Player player;
    private PlayerRegistry playerRegistry;
    private Translator translator;

    @BeforeEach
    public void setUp() {
        playerRegistry = mock(PlayerRegistry.class);
        openModeGameKey = GameKey.ofOpenMode();
        player = mock(Player.class);
        translator = mock(Translator.class);

        issuer = mock(BukkitCommandIssuer.class);
        when(issuer.getPlayer()).thenReturn(player);

        conditionContext = mock();
        when(conditionContext.getIssuer()).thenReturn(issuer);

        contextProvider = mock(GameContextProvider.class);
        when(contextProvider.getComponent(openModeGameKey, PlayerRegistry.class)).thenReturn(playerRegistry);
    }

    @Test
    public void shouldPassWhenPlayerIsInOpenMode() {
        when(playerRegistry.isRegistered(player)).thenReturn(true);

        OpenModePresenceCondition condition = new OpenModePresenceCondition(contextProvider, openModeGameKey, translator);
        condition.validateCondition(conditionContext);
    }

    @Test
    public void shouldNotPassWhenPlayerIsNotInOpenMode() {
        when(playerRegistry.isRegistered(player)).thenReturn(false);
        when(translator.translate(TranslationKey.NOT_IN_TRAINING_MODE.getPath())).thenReturn(new TextTemplate("message"));

        OpenModePresenceCondition condition = new OpenModePresenceCondition(contextProvider, openModeGameKey, translator);

        assertThrows(ConditionFailedException.class, () -> condition.validateCondition(conditionContext));
    }
}
