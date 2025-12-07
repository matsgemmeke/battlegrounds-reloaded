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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntityEventHandler implements EventHandler<EntityDamageByEntityEvent> {

    private final GameContextProvider gameContextProvider;
    private final GameScope gameScope;
    private final Provider<EventDamageAdapter> eventDamageAdapterProvider;

    @Inject
    public EntityDamageByEntityEventHandler(GameContextProvider gameContextProvider, GameScope gameScope, Provider<EventDamageAdapter> eventDamageAdapterProvider) {
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.eventDamageAdapterProvider = eventDamageAdapterProvider;
    }

    public void handle(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = this.resolveEntityDamager(event.getDamager());
        double damageAmount = event.getDamage();

        GameKey gameKey = gameContextProvider.getGameKeyByEntityId(damager.getUniqueId()).orElse(null);

        if (gameKey == null) {
            return;
        }

        GameContext gameContext = gameContextProvider.getGameContext(gameKey)
                .orElseThrow(() -> new EventHandlingException("Unable to process EntityDamageByEntityEvent for game key %s, no corresponding game context was found".formatted(gameKey)));
        EventDamageAdapter eventDamageAdapter = gameScope.supplyInScope(gameContext, eventDamageAdapterProvider::get);

        EventDamageResult eventDamageResult = eventDamageAdapter.processMeleeDamage(entity, damager, damageAmount);

        System.out.println("processMeleeDamage called with result " + eventDamageResult);

        event.setDamage(eventDamageResult.damageAmount());
    }

    private Entity resolveEntityDamager(Entity eventDamager) {
        if (eventDamager instanceof Projectile projectile && projectile.getShooter() instanceof Entity projectileShooter) {
            return projectileShooter;
        }

        return eventDamager;
    }
}
