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

    private Entity damager;
    private Entity entity;
    private GameContextProvider contextProvider;
    private UUID damagerUUID;
    private UUID entityUUID;

    @Before
    public void setUp() {
        contextProvider = mock(GameContextProvider.class);
        damagerUUID = UUID.randomUUID();
        entityUUID = UUID.randomUUID();

        damager = mock(Entity.class);
        when(damager.getUniqueId()).thenReturn(damagerUUID);

        entity = mock(Entity.class);
        when(entity.getUniqueId()).thenReturn(entityUUID);
    }

    @Test
    public void shouldNotProcessEventIfEntityAndDamagerAreNotInGames() {
        double damage = 10.0;

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, entity, EntityDamageEvent.DamageCause.CUSTOM, damage);

        when(contextProvider.getContext(entityUUID)).thenReturn(null);
        when(contextProvider.getContext(damagerUUID)).thenReturn(null);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(contextProvider);
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
        assertEquals(damage, event.getDamage(), 0.0);
    }

    @Test
    public void shouldCancelEventIfDamagerIsInDifferentGame() {
        double damage = 10.0;

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, entity, EntityDamageEvent.DamageCause.CUSTOM, damage);

        GameContext context = mock(GameContext.class);
        when(contextProvider.getContext(entityUUID)).thenReturn(context);

        GameContext otherContext = mock(GameContext.class);
        when(contextProvider.getContext(damagerUUID)).thenReturn(otherContext);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(contextProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());
        assertEquals(damage, event.getDamage(), 0.0);

        verifyNoInteractions(context);
    }

    @Test
    public void shouldNotHandleEventIfDamageCauseDoesNotMap() {
        double damage = 10.0;

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, entity, EntityDamageEvent.DamageCause.CUSTOM, damage);

        GameContext context = mock(GameContext.class);
        when(contextProvider.getContext(entityUUID)).thenReturn(context);
        when(contextProvider.getContext(damagerUUID)).thenReturn(context);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(contextProvider);
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
        assertEquals(damage, event.getDamage(), 0.0);

        verifyNoInteractions(context);
    }

    @Test
    public void shouldHandleEventAndSetDamageAccordingToDamageProcessorResult() {
        double damage = 10.0;
        double finalDamage = 15.0;

        DamageEvent result = new DamageEvent(damager, entity, DamageCause.DEFAULT_EXPLOSION, finalDamage);
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, entity, EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, damage);

        GameContext context = mock(GameContext.class);
        when(contextProvider.getContext(entityUUID)).thenReturn(context);
        when(contextProvider.getContext(damagerUUID)).thenReturn(context);

        DamageProcessor damageProcessor = mock(DamageProcessor.class);
        when(context.getDamageProcessor()).thenReturn(damageProcessor);
        when(damageProcessor.processDamage(any(DamageEvent.class))).thenReturn(result);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(contextProvider);
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
        assertEquals(finalDamage, event.getDamage(), 0.0);
    }
}
