package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.event.EventHandlingException;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class EntityDamageByEntityEventHandlerTest {

    private static final double DAMAGE = 10.0;
    private static final UUID DAMAGER_ID = UUID.randomUUID();
    private static final UUID ENTITY_ID = UUID.randomUUID();

    private Entity damager;
    private Entity entity;
    private GameContextProvider gameContextProvider;
    private GameScope gameScope;
    private Provider<DamageProcessor> damageProcessorProvider;

    @BeforeEach
    public void setUp() {
        gameContextProvider = mock(GameContextProvider.class);
        gameScope = mock(GameScope.class);
        damageProcessorProvider = mock();

        damager = mock(Entity.class);
        when(damager.getUniqueId()).thenReturn(DAMAGER_ID);

        entity = mock(Entity.class);
        when(entity.getUniqueId()).thenReturn(ENTITY_ID);
    }

    @Test
    public void handleDoesNothingWhenEntityAndDamagerAreNotInGameContexts() {
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, entity, DamageCause.CUSTOM, DAMAGE);

        when(gameContextProvider.getGameKeyByEntityId(ENTITY_ID)).thenReturn(Optional.empty());
        when(gameContextProvider.getGameKeyByEntityId(DAMAGER_ID)).thenReturn(Optional.empty());

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(gameContextProvider, gameScope, damageProcessorProvider);
        eventHandler.handle(event);

        assertThat(event.isCancelled()).isFalse();
        assertThat(event.getDamage()).isEqualTo(DAMAGE);

        verifyNoInteractions(gameScope);
    }

    @Test
    public void handleDoesNothingWhenDamageCauseDoesNotMapToDamageType() {
        GameKey damagerGameKey = GameKey.ofSession(1);
        GameKey entityGameKey = GameKey.ofSession(2);

        when(gameContextProvider.getGameKeyByEntityId(DAMAGER_ID)).thenReturn(Optional.of(damagerGameKey));
        when(gameContextProvider.getGameKeyByEntityId(ENTITY_ID)).thenReturn(Optional.of(entityGameKey));

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, entity, DamageCause.SONIC_BOOM, DAMAGE);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(gameContextProvider, gameScope, damageProcessorProvider);
        eventHandler.handle(event);

        assertThat(event.isCancelled()).isFalse();
        assertThat(event.getDamage()).isEqualTo(DAMAGE);

        verifyNoInteractions(gameScope);
    }

    @Test
    public void handleThrowsEventHandlingExceptionWhenUnableToFindGameContextOfSubjectGameKey() {
        GameKey damagerGameKey = GameKey.ofSession(1);
        GameKey entityGameKey = GameKey.ofSession(2);

        when(gameContextProvider.getGameKeyByEntityId(DAMAGER_ID)).thenReturn(Optional.of(damagerGameKey));
        when(gameContextProvider.getGameKeyByEntityId(ENTITY_ID)).thenReturn(Optional.of(entityGameKey));
        when(gameContextProvider.getGameContext(damagerGameKey)).thenReturn(Optional.empty());

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, entity, DamageCause.ENTITY_ATTACK, DAMAGE);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(gameContextProvider, gameScope, damageProcessorProvider);

        assertThatThrownBy(() -> eventHandler.handle(event))
                .isInstanceOf(EventHandlingException.class)
                .hasMessage("Unable to process EntityDamageByEntityEvent for game key SESSION-1, no corresponding game context was found");

        verifyNoInteractions(gameScope);
    }

    @Test
    public void handleCancelsEventWhenDamageProcessorFromDamagerContextDoesNotAllowDamageToEntityContext() {
        GameContext gameContext = mock(GameContext.class);
        GameKey damagerGameKey = GameKey.ofSession(1);
        GameKey entityGameKey = GameKey.ofSession(2);

        DamageProcessor damageProcessor = mock(DamageProcessor.class);
        when(damageProcessor.isDamageAllowed(entityGameKey)).thenReturn(false);

        when(gameContextProvider.getGameKeyByEntityId(DAMAGER_ID)).thenReturn(Optional.of(damagerGameKey));
        when(gameContextProvider.getGameKeyByEntityId(ENTITY_ID)).thenReturn(Optional.of(entityGameKey));
        when(gameContextProvider.getGameContext(damagerGameKey)).thenReturn(Optional.of(gameContext));
        when(damageProcessorProvider.get()).thenReturn(damageProcessor);

        when(gameScope.supplyInScope(eq(gameContext), any())).thenAnswer(invocation -> {
            Supplier<DamageProcessor> damageProcessorSupplier = invocation.getArgument(1);
            return damageProcessorSupplier.get();
        });

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, entity, DamageCause.ENTITY_ATTACK, DAMAGE);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(gameContextProvider, gameScope, damageProcessorProvider);
        eventHandler.handle(event);

        assertThat(event.isCancelled()).isTrue();
        assertThat(event.getDamage()).isEqualTo(DAMAGE);
    }

    @Test
    public void handleCancelsEventWhenDamageProcessorFromDamagerContextAllowsDamageToEntityWithoutContext() {
        GameContext gameContext = mock(GameContext.class);
        GameKey damagerGameKey = GameKey.ofSession(1);
        DamageEvent damageEvent = new DamageEvent(damager, damagerGameKey, entity, null, DamageType.EXPLOSIVE_DAMAGE, 1.0);

        DamageProcessor damageProcessor = mock(DamageProcessor.class);
        when(damageProcessor.isDamageAllowedWithoutContext()).thenReturn(true);
        when(damageProcessor.processDamage(any(DamageEvent.class))).thenReturn(damageEvent);

        when(gameContextProvider.getGameKeyByEntityId(DAMAGER_ID)).thenReturn(Optional.of(damagerGameKey));
        when(gameContextProvider.getGameKeyByEntityId(ENTITY_ID)).thenReturn(Optional.empty());
        when(gameContextProvider.getGameContext(damagerGameKey)).thenReturn(Optional.of(gameContext));
        when(damageProcessorProvider.get()).thenReturn(damageProcessor);

        when(gameScope.supplyInScope(eq(gameContext), any())).thenAnswer(invocation -> {
            Supplier<DamageProcessor> damageProcessorSupplier = invocation.getArgument(1);
            return damageProcessorSupplier.get();
        });

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, entity, DamageCause.ENTITY_ATTACK, DAMAGE);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(gameContextProvider, gameScope, damageProcessorProvider);
        eventHandler.handle(event);

        assertThat(event.isCancelled()).isFalse();
        assertThat(event.getDamage()).isEqualTo(1.0);
    }

    @Test
    public void handleCancelsEventWhenDamageProcessorFromEntityContextDoesNotAllowDamageFromEntityWithoutContext() {
        GameContext gameContext = mock(GameContext.class);
        GameKey entityGameKey = GameKey.ofSession(1);

        DamageProcessor damageProcessor = mock(DamageProcessor.class);
        when(damageProcessor.isDamageAllowedWithoutContext()).thenReturn(false);

        when(gameContextProvider.getGameKeyByEntityId(DAMAGER_ID)).thenReturn(Optional.empty());
        when(gameContextProvider.getGameKeyByEntityId(ENTITY_ID)).thenReturn(Optional.of(entityGameKey));
        when(gameContextProvider.getGameContext(entityGameKey)).thenReturn(Optional.of(gameContext));
        when(damageProcessorProvider.get()).thenReturn(damageProcessor);

        when(gameScope.supplyInScope(eq(gameContext), any())).thenAnswer(invocation -> {
            Supplier<DamageProcessor> damageProcessorSupplier = invocation.getArgument(1);
            return damageProcessorSupplier.get();
        });

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, entity, DamageCause.ENTITY_ATTACK, DAMAGE);

        EntityDamageByEntityEventHandler eventHandler = new EntityDamageByEntityEventHandler(gameContextProvider, gameScope, damageProcessorProvider);
        eventHandler.handle(event);

        assertThat(event.isCancelled()).isTrue();
        assertThat(event.getDamage()).isEqualTo(DAMAGE);
    }
}
