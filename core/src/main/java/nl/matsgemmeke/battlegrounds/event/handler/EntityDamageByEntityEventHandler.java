package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class EntityDamageByEntityEventHandler implements EventHandler<EntityDamageByEntityEvent> {

    @NotNull
    private GameContextProvider contextProvider;

    @Inject
    public EntityDamageByEntityEventHandler(@NotNull GameContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public void handle(@NotNull EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        GameContext entityContext = contextProvider.getContext(entity.getUniqueId());
        GameContext damagerContext = contextProvider.getContext(damager.getUniqueId());

        if (entityContext == null && damagerContext == null) {
            // Do not handle events outside of game instances
            return;
        }

        DamageProcessor damageProcessor;
        GameContext otherContext = null;

        if (damagerContext != null) {
            damageProcessor = damagerContext.getDamageProcessor();
            otherContext = entityContext;
        } else {
            damageProcessor = entityContext.getDamageProcessor();
        }

        if (!damageProcessor.isDamageAllowed(otherContext)) {
            event.setCancelled(true);
            return;
        }

        DamageType type = DamageType.map(event.getCause());

        if (type == null) {
            // Do not handle events whose damage cause does not map to damage causes the plugin handles
            return;
        }

        DamageEvent damageEvent = new DamageEvent(damager, damagerContext, entity, entityContext, type, event.getDamage());
        DamageEvent result = damageProcessor.processDamage(damageEvent);

        // Only set the event damage so the damage animation and physics are kept
        event.setDamage(result.getDamage());
    }
}
