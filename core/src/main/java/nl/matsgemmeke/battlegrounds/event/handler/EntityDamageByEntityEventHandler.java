package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
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
        GameKey entityGameKey = contextProvider.getGameKey(entity.getUniqueId());
        GameKey damagerGameKey = contextProvider.getGameKey(damager.getUniqueId());

        if (entityGameKey == null && damagerGameKey == null) {
            // Do not handle events outside of game instances
            return;
        }

        DamageProcessor damageProcessor;
        GameKey otherKey = null;

        if (damagerGameKey != null) {
            damageProcessor = contextProvider.getComponent(damagerGameKey, DamageProcessor.class);
            otherKey = entityGameKey;
        } else {
            damageProcessor = contextProvider.getComponent(entityGameKey, DamageProcessor.class);
        }

        if (!damageProcessor.isDamageAllowed(otherKey)) {
            event.setCancelled(true);
            return;
        }

        DamageType type = DamageType.map(event.getCause());

        if (type == null) {
            // Do not handle events whose damage cause does not map to damage causes the plugin handles
            return;
        }

        DamageEvent damageEvent = new DamageEvent(damager, damagerGameKey, entity, entityGameKey, type, event.getDamage());
        DamageEvent result = damageProcessor.processDamage(damageEvent);

        // Only set the event damage so the damage animation and physics are kept
        event.setDamage(result.getDamage());
    }
}
