package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.GameProvider;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.Game;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class EntityDamageByEntityEventHandler implements EventHandler<EntityDamageByEntityEvent> {

    @NotNull
    private GameProvider gameProvider;

    public EntityDamageByEntityEventHandler(@NotNull GameProvider gameProvider) {
        this.gameProvider = gameProvider;
    }

    public void handle(@NotNull EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        Game game = gameProvider.getGame(entity);
        Game damagerGame = gameProvider.getGame(damager);

        if (game == null && damagerGame == null) {
            // Do not handle damage events outside game instances
            return;
        }

        if (game != damagerGame) {
            // Cancel damage events that happen between entities not present in the same game
            event.setCancelled(true);
            return;
        }

        double damage = game.calculateDamage(damager, entity, event.getDamage());

        // Only set the event damage. Cancelling stops the animation and physics which we don't want.
        event.setDamage(damage);
    }
}
