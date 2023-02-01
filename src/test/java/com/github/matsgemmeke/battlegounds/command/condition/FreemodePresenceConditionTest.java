package com.github.matsgemmeke.battlegounds.command.condition;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import com.github.matsgemmeke.battlegrounds.api.game.FreemodeContext;
import com.github.matsgemmeke.battlegrounds.command.condition.FreemodePresenceCondition;
import com.github.matsgemmeke.battlegrounds.locale.Translator;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class FreemodePresenceConditionTest {

    private BukkitCommandIssuer issuer;
    private ConditionContext<BukkitCommandIssuer> conditionContext;
    private FreemodeContext freemodeContext;
    private Player player;
    private Translator translator;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        this.issuer = mock(BukkitCommandIssuer.class);
        this.conditionContext = (ConditionContext<BukkitCommandIssuer>) mock(ConditionContext.class);
        this.freemodeContext = mock(FreemodeContext.class);
        this.player = mock(Player.class);
        this.translator = mock(Translator.class);

        when(issuer.getPlayer()).thenReturn(player);
        when(conditionContext.getIssuer()).thenReturn(issuer);
    }

    @Test
    public void shouldPassWhenPlayerIsInFreemode() {
        when(freemodeContext.hasPlayer(player)).thenReturn(true);

        FreemodePresenceCondition condition = new FreemodePresenceCondition(freemodeContext, translator);
        condition.validateCondition(conditionContext);
    }

    @Test(expected = ConditionFailedException.class)
    public void shouldNotPassWhenPlayerIsNotInFreemode() {
        when(freemodeContext.hasPlayer(player)).thenReturn(false);

        FreemodePresenceCondition condition = new FreemodePresenceCondition(freemodeContext, translator);
        condition.validateCondition(conditionContext);
    }
}
