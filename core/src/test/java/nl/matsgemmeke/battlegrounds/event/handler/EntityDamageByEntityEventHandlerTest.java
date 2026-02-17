package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.event.EventHandlingException;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.damage.EventDamageAdapter;
import nl.matsgemmeke.battlegrounds.game.component.damage.EventDamageResult;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileRegistry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.projectiles.ProjectileSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntityDamageByEntityEventHandlerTest {

    private static final GameKey DAMAGER_GAME_KEY = GameKey.ofSession(1);
    private static final double EVENT_DAMAGE = 10.0;
    private static final double ADAPTER_DAMAGE = 50.0;
    private static final UUID DAMAGER_UNIQUE_ID = UUID.randomUUID();
    private static final UUID PROJECTILE_UNIQUE_ID = UUID.randomUUID();

    @Mock(extraInterfaces = ProjectileSource.class)
    private Entity damager;
    @Mock
    private Entity entity;
    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private GameScope gameScope;
    @Mock
    private Provider<EventDamageAdapter> eventDamageAdapterProvider;
    @Mock
    private Provider<ProjectileRegistry> projectileRegistryProvider;

    private EntityDamageByEntityEventHandler eventHandler;

    @BeforeEach
    void setUp() {
        when(damager.getUniqueId()).thenReturn(DAMAGER_UNIQUE_ID);

        eventHandler = new EntityDamageByEntityEventHandler(gameContextProvider, gameScope, eventDamageAdapterProvider, projectileRegistryProvider);
    }

    @Test
    void handleDoesNothingWhenEntityAndDamagerAreNotInGameContexts() {
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, entity, DamageCause.CUSTOM, EVENT_DAMAGE);

        when(gameContextProvider.getGameKeyByEntityId(DAMAGER_UNIQUE_ID)).thenReturn(Optional.empty());

        eventHandler.handle(event);

        assertThat(event.isCancelled()).isFalse();
        assertThat(event.getDamage()).isEqualTo(EVENT_DAMAGE);

        verifyNoInteractions(gameScope);
    }

    @Test
    void handleThrowsEventHandlingExceptionWhenUnableToFindGameContextOfSubjectGameKey() {
        when(gameContextProvider.getGameKeyByEntityId(DAMAGER_UNIQUE_ID)).thenReturn(Optional.of(DAMAGER_GAME_KEY));
        when(gameContextProvider.getGameContext(DAMAGER_GAME_KEY)).thenReturn(Optional.empty());

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, entity, DamageCause.ENTITY_ATTACK, EVENT_DAMAGE);

        assertThatThrownBy(() -> eventHandler.handle(event))
                .isInstanceOf(EventHandlingException.class)
                .hasMessage("Unable to process EntityDamageByEntityEvent for game key SESSION-1, no corresponding game context was found");

        verifyNoInteractions(gameScope);
    }

    @Test
    void handleSetsEventDamageBasedOnResultFromEventDamageAdapterWhenDamageCauseEqualsEntityAttack() {
        GameContext gameContext = mock(GameContext.class);
        EventDamageResult eventDamageResult = new EventDamageResult(ADAPTER_DAMAGE);

        EventDamageAdapter eventDamageAdapter = mock(EventDamageAdapter.class);
        when(eventDamageAdapter.processMeleeDamage(damager, entity, EVENT_DAMAGE)).thenReturn(eventDamageResult);

        when(gameContextProvider.getGameKeyByEntityId(DAMAGER_UNIQUE_ID)).thenReturn(Optional.of(DAMAGER_GAME_KEY));
        when(gameContextProvider.getGameContext(DAMAGER_GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(eventDamageAdapterProvider.get()).thenReturn(eventDamageAdapter);

        when(gameScope.supplyInScope(eq(gameContext), any())).thenAnswer(invocation -> {
            Supplier<EventDamageAdapter> eventDamageAdapterSupplier = invocation.getArgument(1);
            return eventDamageAdapterSupplier.get();
        });

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, entity, DamageCause.ENTITY_ATTACK, EVENT_DAMAGE);

        eventHandler.handle(event);

        assertThat(event.isCancelled()).isFalse();
        assertThat(event.getDamage()).isEqualTo(ADAPTER_DAMAGE);
    }

    @Test
    void handleDoesNothingWhenEventDamageCauseIsProjectileAndProjectileEntityIsNotRegistered() {
        GameContext gameContext = mock(GameContext.class);

        Projectile projectile = mock(Projectile.class);
        when(projectile.getShooter()).thenReturn((ProjectileSource) damager);
        when(projectile.getUniqueId()).thenReturn(PROJECTILE_UNIQUE_ID);

        ProjectileRegistry projectileRegistry = mock(ProjectileRegistry.class);
        when(projectileRegistry.isRegistered(PROJECTILE_UNIQUE_ID)).thenReturn(false);

        when(gameContextProvider.getGameKeyByEntityId(DAMAGER_UNIQUE_ID)).thenReturn(Optional.of(DAMAGER_GAME_KEY));
        when(gameContextProvider.getGameContext(DAMAGER_GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(gameScope.supplyInScope(eq(gameContext), any())).thenAnswer(invocation -> {
            Supplier<ProjectileRegistry> projectileRegistrySupplier = invocation.getArgument(1);
            return projectileRegistrySupplier.get();
        });
        when(projectileRegistryProvider.get()).thenReturn(projectileRegistry);

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(projectile, entity, DamageCause.PROJECTILE, EVENT_DAMAGE);

        eventHandler.handle(event);

        assertThat(event.getDamage()).isEqualTo(EVENT_DAMAGE);

        verify(projectileRegistry, never()).unregister(any(UUID.class));
    }

    @Test
    void handleSetsDamageToZeroAndRemovesProjectileFromRegistry() {
        GameContext gameContext = mock(GameContext.class);

        Projectile projectile = mock(Projectile.class);
        when(projectile.getShooter()).thenReturn((ProjectileSource) damager);
        when(projectile.getUniqueId()).thenReturn(PROJECTILE_UNIQUE_ID);

        ProjectileRegistry projectileRegistry = mock(ProjectileRegistry.class);
        when(projectileRegistry.isRegistered(PROJECTILE_UNIQUE_ID)).thenReturn(true);

        when(gameContextProvider.getGameKeyByEntityId(DAMAGER_UNIQUE_ID)).thenReturn(Optional.of(DAMAGER_GAME_KEY));
        when(gameContextProvider.getGameContext(DAMAGER_GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(gameScope.supplyInScope(eq(gameContext), any())).thenAnswer(invocation -> {
            Supplier<ProjectileRegistry> projectileRegistrySupplier = invocation.getArgument(1);
            return projectileRegistrySupplier.get();
        });
        when(projectileRegistryProvider.get()).thenReturn(projectileRegistry);

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(projectile, entity, DamageCause.PROJECTILE, EVENT_DAMAGE);

        eventHandler.handle(event);

        assertThat(event.getDamage()).isZero();

        verify(projectileRegistry).unregister(PROJECTILE_UNIQUE_ID);
    }
}
