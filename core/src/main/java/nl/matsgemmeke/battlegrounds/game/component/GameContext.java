package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.entity.GameItem;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;

public interface GameContext {

    @NotNull
    AudioEmitter getAudioEmitter();

    @NotNull
    CollisionDetector getCollisionDetector();

    @NotNull
    EntityRegistry<Item, GameItem> getItemRegistry();
}
