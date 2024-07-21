package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.game.component.*;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;

/**
 * Entry point for provision of segregated interfaces for a game instance.
 */
public interface GameContext {

    @NotNull
    AudioEmitter getAudioEmitter();

    @NotNull
    CollisionDetector getCollisionDetector();

    @NotNull
    EntityRegistry<Item, GameItem> getItemRegistry();
}
