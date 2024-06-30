package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.GameProvider;
import nl.matsgemmeke.battlegrounds.game.Game;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class EntityDamageByEntityEventHandlerTest {

    private Entity damager;
    private Entity entity;
    private EntityDamageByEntityEvent event;
    private GameProvider gameProvider;

    @Before
    public void setUp() {
        damager = mock(Entity.class);
        entity = mock(Entity.class);
        gameProvider = mock(GameProvider.class);

        event = spy(new EntityDamageByEntityEvent(damager, entity, DamageCause.ENTITY_ATTACK, 10.0));
    }

    @Test
    public void shouldNotProcessEventIfEntityAndDamagerAreNotInGames() {
        when(gameProvider.getGame(entity)).thenReturn(null);
        when(gameProvider.getGame(damager)).thenReturn(null);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(gameProvider);
        eventHandler.handle(event);

        assertEquals(10.0, event.getDamage(), 0.0);
    }

    @Test
    public void shouldCancelEventIfDamagerIsInDifferentGame() {
        Game game = mock(Game.class);
        Game otherGame = mock(Game.class);

        when(gameProvider.getGame(entity)).thenReturn(game);
        when(gameProvider.getGame(damager)).thenReturn(otherGame);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(gameProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());
    }

    @Test
    public void shouldSetEventDamageBasedOnResultFromGame() {
        double damage = 100.0;

        Game game = mock(Game.class);
        when(game.calculateDamage(damager, entity, 10.0)).thenReturn(damage);

        when(gameProvider.getGame(entity)).thenReturn(game);
        when(gameProvider.getGame(damager)).thenReturn(game);

        when(event.getDamage()).thenReturn(10.0);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(gameProvider);
        eventHandler.handle(event);

        verify(game).calculateDamage(damager, entity, 10.0);
        verify(event).setDamage(damage);
    }
}
