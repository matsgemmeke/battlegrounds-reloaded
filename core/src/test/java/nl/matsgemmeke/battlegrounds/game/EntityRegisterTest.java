package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EntityRegisterTest {

    private GamePlayer gamePlayer;
    private Player player;

    @Before
    public void setUp() {
        player = mock(Player.class);

        gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);
    }

    @Test
    public void shouldAddEntityToRegister() {
        EntityRegister<GamePlayer> register = new EntityRegister<>();
        register.addEntity(gamePlayer);

        assertEquals(gamePlayer, register.getEntity(player));
    }

    @Test
    public void shouldReturnNullIfThereIsNoCorrespondingEntity() {
        EntityRegister<GamePlayer> register = new EntityRegister<>();
        register.addEntity(mock(GamePlayer.class));

        assertNull(register.getEntity(player));
    }

    @Test
    public void shouldRemoveEntityFromRegister() {
        EntityRegister<GamePlayer> register = new EntityRegister<>();
        register.addEntity(gamePlayer);
        register.removeEntity(gamePlayer);

        assertNull(register.getEntity(player));
    }
}
