package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.GameContextProvider;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.damage.DamageCause;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class EntityDamageByEntityEventHandler implements EventHandler<EntityDamageByEntityEvent> {

    @NotNull
    private GameContextProvider contextProvider;

    public EntityDamageByEntityEventHandler(@NotNull GameContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public void handle(@NotNull EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        GameContext context = contextProvider.getContext(entity.getUniqueId());
        GameContext damagerContext = contextProvider.getContext(damager.getUniqueId());

        if (context == null || damagerContext == null) {
            // Do not handle damage events outside game instances
            return;
        }

        if (context != damagerContext) {
            // Cancel damage events that happen between entities not present in the same game
            event.setCancelled(true);
            return;
        }

        DamageCause cause = DamageCause.map(event.getCause());

        if (cause == null) {
            return;
        }

        DamageEvent damageEvent = context.getDamageProcessor().processDamage(new DamageEvent(damager, entity, cause, event.getDamage()));

        // Only set the event damage. Cancelling stops the animation and physics which we don't want.
        event.setDamage(damageEvent.getDamage());
    }
}
