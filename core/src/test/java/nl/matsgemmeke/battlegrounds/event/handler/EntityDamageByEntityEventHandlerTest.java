package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.DamageCause;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class EntityDamageByEntityEventHandlerTest {

    private double damage;
    private Entity damager;
    private Entity entity;
    private GameContextProvider contextProvider;
    private UUID damagerUUID;
    private UUID entityUUID;

    @Before
    public void setUp() {
        contextProvider = mock(GameContextProvider.class);
        damage = 10.0;
        damagerUUID = UUID.randomUUID();
        entityUUID = UUID.randomUUID();

        damager = mock(Entity.class);
        when(damager.getUniqueId()).thenReturn(damagerUUID);

        entity = mock(Entity.class);
        when(entity.getUniqueId()).thenReturn(entityUUID);
    }

    @Test
    public void shouldNotProcessEventIfEntityAndDamagerAreNotInGames() {
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, entity, EntityDamageEvent.DamageCause.CUSTOM, damage);

        when(contextProvider.getContext(entityUUID)).thenReturn(null);
        when(contextProvider.getContext(damagerUUID)).thenReturn(null);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(contextProvider);
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
        assertEquals(damage, event.getDamage(), 0.0);
    }

    @Test
    public void shouldCancelEventIfDamagerContextDoesNotAllowDamageToEntityContext() {
        GameContext damagerContext = mock(GameContext.class);
        when(contextProvider.getContext(damagerUUID)).thenReturn(damagerContext);

        GameContext entityContext = mock(GameContext.class);
        when(contextProvider.getContext(entityUUID)).thenReturn(entityContext);

        DamageProcessor damageProcessor = mock(DamageProcessor.class);
        when(damageProcessor.isDamageAllowed(entityContext)).thenReturn(false);
        when(damagerContext.getDamageProcessor()).thenReturn(damageProcessor);

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, entity, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(contextProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());
        assertEquals(damage, event.getDamage(), 0.0);
    }

    @Test
    public void shouldCancelEventIfEntityContextDoesNotAllowDamageFromDamagerContext() {
        GameContext entityContext = mock(GameContext.class);
        when(contextProvider.getContext(entityUUID)).thenReturn(entityContext);

        GameContext damagerContext = null;
        when(contextProvider.getContext(damagerUUID)).thenReturn(damagerContext);

        DamageProcessor damageProcessor = mock(DamageProcessor.class);
        when(damageProcessor.isDamageAllowed(damagerContext)).thenReturn(false);
        when(entityContext.getDamageProcessor()).thenReturn(damageProcessor);

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, entity, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(contextProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());
        assertEquals(damage, event.getDamage(), 0.0);
    }

    @Test
    public void shouldNotHandleEventIfDamageCauseDoesNotMap() {
        GameContext context = mock(GameContext.class);
        when(contextProvider.getContext(entityUUID)).thenReturn(context);
        when(contextProvider.getContext(damagerUUID)).thenReturn(context);

        DamageProcessor damageProcessor = mock(DamageProcessor.class);
        when(damageProcessor.isDamageAllowed(context)).thenReturn(true);
        when(context.getDamageProcessor()).thenReturn(damageProcessor);

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, entity, EntityDamageEvent.DamageCause.CUSTOM, damage);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(contextProvider);
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
        assertEquals(damage, event.getDamage(), 0.0);
    }

    @Test
    public void handlingEventSetsEventDamageBasedOnResultOfDamageProcessor() {
        GameContext context = mock(GameContext.class);
        when(contextProvider.getContext(damagerUUID)).thenReturn(context);
        when(contextProvider.getContext(entityUUID)).thenReturn(context);

        double modifiedDamage = 20.0;

        DamageEvent damageEvent = new DamageEvent(damager, context, entity, context, DamageCause.ENTITY_ATTACK, modifiedDamage);

        DamageProcessor damageProcessor = mock(DamageProcessor.class);
        when(damageProcessor.isDamageAllowed(context)).thenReturn(true);
        when(damageProcessor.processDamage(any(DamageEvent.class))).thenReturn(damageEvent);
        when(context.getDamageProcessor()).thenReturn(damageProcessor);

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, entity, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(contextProvider);
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
        assertEquals(modifiedDamage, event.getDamage(), 0.0);
    }
}
