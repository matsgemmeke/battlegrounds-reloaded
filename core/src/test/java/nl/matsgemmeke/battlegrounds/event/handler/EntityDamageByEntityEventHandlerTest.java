package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.DamageCalculator;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class EntityDamageByEntityEventHandlerTest {

    private DamageCause damageCause;
    private Entity damager;
    private Entity entity;
    private EntityDamageByEntityEvent event;
    private GameContextProvider contextProvider;
    private UUID damagerUUID;
    private UUID entityUUID;

    @Before
    public void setUp() {
        damageCause = DamageCause.ENTITY_ATTACK;
        contextProvider = mock(GameContextProvider.class);
        damagerUUID = UUID.randomUUID();
        entityUUID = UUID.randomUUID();

        damager = mock(Entity.class);
        when(damager.getUniqueId()).thenReturn(damagerUUID);

        entity = mock(Entity.class);
        when(entity.getUniqueId()).thenReturn(entityUUID);

        event = spy(new EntityDamageByEntityEvent(damager, entity, damageCause, 10.0));
    }

    @Test
    public void shouldNotProcessEventIfEntityAndDamagerAreNotInGames() {
        when(contextProvider.getContext(entityUUID)).thenReturn(null);
        when(contextProvider.getContext(damagerUUID)).thenReturn(null);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(contextProvider);
        eventHandler.handle(event);

        assertEquals(10.0, event.getDamage(), 0.0);
    }

    @Test
    public void shouldCancelEventIfDamagerIsInDifferentGame() {
        GameContext game = mock(GameContext.class);
        GameContext otherContext = mock(GameContext.class);

        when(contextProvider.getContext(entityUUID)).thenReturn(game);
        when(contextProvider.getContext(damagerUUID)).thenReturn(otherContext);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(contextProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());
    }

    @Test
    public void shouldSetEventDamageBasedOnResultFromDamageCalculator() {
        double originalDamage = 10.0;
        double finalDamage = 100.0;

        DamageCalculator damageCalculator = mock(DamageCalculator.class);
        when(damageCalculator.calculateDamage(damager, entity, damageCause, originalDamage)).thenReturn(finalDamage);

        GameContext context = mock(GameContext.class);
        when(context.getDamageCalculator()).thenReturn(damageCalculator);

        when(contextProvider.getContext(entityUUID)).thenReturn(context);
        when(contextProvider.getContext(damagerUUID)).thenReturn(context);

        when(event.getDamage()).thenReturn(originalDamage);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(contextProvider);
        eventHandler.handle(event);

        verify(damageCalculator).calculateDamage(damager, entity, damageCause, originalDamage);
        verify(event).setDamage(finalDamage);
    }
}
