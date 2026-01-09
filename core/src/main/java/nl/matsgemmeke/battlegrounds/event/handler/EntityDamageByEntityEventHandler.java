package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
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

import java.util.UUID;

public class EntityDamageByEntityEventHandler implements EventHandler<EntityDamageByEntityEvent> {

    private final GameContextProvider gameContextProvider;
    private final GameScope gameScope;
    private final Provider<EventDamageAdapter> eventDamageAdapterProvider;
    private final Provider<ProjectileRegistry> projectileRegistryProvider;

    @Inject
    public EntityDamageByEntityEventHandler(
            GameContextProvider gameContextProvider,
            GameScope gameScope,
            Provider<EventDamageAdapter> eventDamageAdapterProvider,
            Provider<ProjectileRegistry> projectileRegistryProvider
    ) {
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.eventDamageAdapterProvider = eventDamageAdapterProvider;
        this.projectileRegistryProvider = projectileRegistryProvider;
    }

    public void handle(EntityDamageByEntityEvent event) {
        Entity damager = this.resolveEntityDamager(event.getDamager());
        GameKey gameKey = gameContextProvider.getGameKeyByEntityId(damager.getUniqueId()).orElse(null);

        if (gameKey == null) {
            return;
        }

        GameContext gameContext = gameContextProvider.getGameContext(gameKey)
                .orElseThrow(() -> new EventHandlingException("Unable to process EntityDamageByEntityEvent for game key %s, no corresponding game context was found".formatted(gameKey)));

        DamageCause cause = event.getCause();

        if (cause == DamageCause.ENTITY_ATTACK) {
            this.handleMeleeDamage(gameContext, event);
        } else if (cause == DamageCause.PROJECTILE) {
            this.handleProjectileDamage(gameContext, event);
        }
    }

    private Entity resolveEntityDamager(Entity eventDamager) {
        if (eventDamager instanceof Projectile projectile && projectile.getShooter() instanceof Entity projectileShooter) {
            return projectileShooter;
        }

        return eventDamager;
    }

    private void handleMeleeDamage(GameContext gameContext, EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = this.resolveEntityDamager(event.getDamager());
        double damageAmount = event.getDamage();

        EventDamageAdapter eventDamageAdapter = gameScope.supplyInScope(gameContext, eventDamageAdapterProvider::get);
        EventDamageResult eventDamageResult = eventDamageAdapter.processMeleeDamage(damager, entity, damageAmount);

        event.setDamage(eventDamageResult.damageAmount());
    }

    private void handleProjectileDamage(GameContext gameContext, EntityDamageByEntityEvent event) {
        UUID projectileUniqueId = event.getDamager().getUniqueId();
        ProjectileRegistry projectileRegistry = gameScope.supplyInScope(gameContext, projectileRegistryProvider::get);

        if (!projectileRegistry.isRegistered(projectileUniqueId)) {
            // The damaging projectile does originate from any custom item, so we ignore it
            return;
        }

        projectileRegistry.unregister(projectileUniqueId);
        event.setDamage(0.0);
    }
}
