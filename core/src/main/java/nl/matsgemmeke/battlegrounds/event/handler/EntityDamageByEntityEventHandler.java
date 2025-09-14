package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
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
import org.jetbrains.annotations.NotNull;

public class EntityDamageByEntityEventHandler implements EventHandler<EntityDamageByEntityEvent> {

    @NotNull
    private final GameContextProvider gameContextProvider;
    @NotNull
    private final GameScope gameScope;
    @NotNull
    private final Provider<DamageProcessor> damageProcessorProvider;

    @Inject
    public EntityDamageByEntityEventHandler(@NotNull GameContextProvider gameContextProvider, @NotNull GameScope gameScope, @NotNull Provider<DamageProcessor> damageProcessorProvider) {
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.damageProcessorProvider = damageProcessorProvider;
    }

    public void handle(@NotNull EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        GameKey entityGameKey = gameContextProvider.getGameKeyByEntityId(entity.getUniqueId()).orElse(null);
        GameKey damagerGameKey = gameContextProvider.getGameKeyByEntityId(damager.getUniqueId()).orElse(null);

        if (entityGameKey == null && damagerGameKey == null) {
            // Do not handle events outside of game instances
            return;
        }

        DamageType type = DamageType.map(event.getCause()).orElse(null);

        if (type == null) {
            // Do not handle events whose damage cause does not map to damage causes the plugin handles
            return;
        }

        GameKey subjectGameKey;
        GameKey targetGameKey;

        if (damagerGameKey != null) {
            subjectGameKey = damagerGameKey;
            targetGameKey = entityGameKey;
        } else {
            subjectGameKey = entityGameKey;
            targetGameKey = null;
        }

        GameContext gameContext = gameContextProvider.getGameContext(subjectGameKey)
                .orElseThrow(() -> new EventHandlingException("Unable to process EntityDamageByEntityEvent for game key %s, no corresponding game context was found".formatted(subjectGameKey)));
        DamageProcessor damageProcessor = gameScope.supplyInScope(gameContext, damageProcessorProvider::get);

        if (targetGameKey != null && !damageProcessor.isDamageAllowed(targetGameKey)
                || targetGameKey == null && !damageProcessor.isDamageAllowedWithoutContext()) {
            event.setCancelled(true);
            return;
        }

        DamageEvent damageEvent = new DamageEvent(damager, damagerGameKey, entity, entityGameKey, type, event.getDamage());
        DamageEvent result = damageProcessor.processDamage(damageEvent);

        // Only set the event damage so the damage animation and physics are kept
        event.setDamage(result.getDamage());
    }
}
