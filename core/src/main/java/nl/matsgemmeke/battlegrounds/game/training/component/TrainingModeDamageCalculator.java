package nl.matsgemmeke.battlegrounds.game.training.component;

import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.game.component.DamageCalculator;
import nl.matsgemmeke.battlegrounds.game.component.EntityRegistry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;

public class TrainingModeDamageCalculator implements DamageCalculator {

    @NotNull
    private EntityRegistry<GameItem, Item> itemRegistry;

    public TrainingModeDamageCalculator(@NotNull EntityRegistry<GameItem, Item> itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    public double calculateDamage(@NotNull Entity damager, @NotNull Entity entity, double damage) {
        if (itemRegistry.isRegistered(damager.getUniqueId())) {
            return 0.0;
        }

        return damage;
    }
}
