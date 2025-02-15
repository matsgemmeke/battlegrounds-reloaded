package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EntityDamageByEntityEventHandlerTest {

    private double damage;
    private Entity damager;
    private Entity entity;
    private GameContextProvider contextProvider;
    private UUID damagerUUID;
    private UUID entityUUID;

    @BeforeEach
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
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, entity, DamageCause.CUSTOM, damage);

        when(contextProvider.getContext(entityUUID)).thenReturn(null);
        when(contextProvider.getContext(damagerUUID)).thenReturn(null);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(contextProvider);
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
        assertEquals(damage, event.getDamage(), 0.0);
    }

    @Test
    public void shouldCancelEventIfDamagerContextDoesNotAllowDamageToEntityContext() {
        GameKey damagerGameKey = GameKey.ofSession(1);
        when(contextProvider.getGameKey(damagerUUID)).thenReturn(damagerGameKey);

        GameKey entityGameKey = GameKey.ofSession(2);
        when(contextProvider.getGameKey(entityUUID)).thenReturn(entityGameKey);

        DamageProcessor damageProcessor = mock(DamageProcessor.class);
        when(damageProcessor.isDamageAllowed(entityGameKey)).thenReturn(false);

        when(contextProvider.getComponent(damagerGameKey, DamageProcessor.class)).thenReturn(damageProcessor);

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, entity, DamageCause.ENTITY_ATTACK, damage);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(contextProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());
        assertEquals(damage, event.getDamage(), 0.0);
    }

    @Test
    public void shouldCancelEventIfEntityContextDoesNotAllowDamageFromDamagerContext() {
        GameKey entityGameKey = GameKey.ofTrainingMode();
        when(contextProvider.getGameKey(entityUUID)).thenReturn(entityGameKey);
        when(contextProvider.getGameKey(damagerUUID)).thenReturn(null);

        DamageProcessor damageProcessor = mock(DamageProcessor.class);
        when(damageProcessor.isDamageAllowed(null)).thenReturn(false);

        when(contextProvider.getComponent(entityGameKey, DamageProcessor.class)).thenReturn(damageProcessor);

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, entity, DamageCause.ENTITY_ATTACK, damage);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(contextProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());
        assertEquals(damage, event.getDamage(), 0.0);
    }

    @Test
    public void shouldNotHandleEventIfDamageCauseDoesNotMap() {
        GameKey gameKey = GameKey.ofTrainingMode();
        when(contextProvider.getGameKey(entityUUID)).thenReturn(gameKey);
        when(contextProvider.getGameKey(damagerUUID)).thenReturn(gameKey);

        DamageProcessor damageProcessor = mock(DamageProcessor.class);
        when(damageProcessor.isDamageAllowed(gameKey)).thenReturn(true);

        when(contextProvider.getComponent(gameKey, DamageProcessor.class)).thenReturn(damageProcessor);

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, entity, DamageCause.CUSTOM, damage);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(contextProvider);
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
        assertEquals(damage, event.getDamage(), 0.0);
    }

    @Test
    public void handlingEventSetsEventDamageBasedOnResultOfDamageProcessor() {
        GameKey gameKey =  GameKey.ofTrainingMode();
        when(contextProvider.getGameKey(damagerUUID)).thenReturn(gameKey);
        when(contextProvider.getGameKey(entityUUID)).thenReturn(gameKey);

        double modifiedDamage = 20.0;

        DamageEvent damageEvent = new DamageEvent(damager, gameKey, entity, gameKey, DamageType.ATTACK_DAMAGE, modifiedDamage);

        DamageProcessor damageProcessor = mock(DamageProcessor.class);
        when(damageProcessor.isDamageAllowed(gameKey)).thenReturn(true);
        when(damageProcessor.processDamage(any(DamageEvent.class))).thenReturn(damageEvent);

        when(contextProvider.getComponent(gameKey, DamageProcessor.class)).thenReturn(damageProcessor);

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, entity, DamageCause.ENTITY_ATTACK, damage);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(contextProvider);
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
        assertEquals(modifiedDamage, event.getDamage(), 0.0);
    }
}
