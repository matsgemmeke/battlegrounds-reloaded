package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.entity.DefaultGameItem;
import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultItemRegistry implements EntityRegistry<Item, GameItem> {

    @NotNull
    private EntityStorage<GameItem> itemStorage;

    public DefaultItemRegistry(@NotNull EntityStorage<GameItem> itemStorage) {
        this.itemStorage = itemStorage;
    }

    @Nullable
    public GameItem findByEntity(Item itemEntity) {
        for (GameItem gameItem : itemStorage.getEntities()) {
            if (gameItem.getEntity() == itemEntity) {
                return gameItem;
            }
        }
        return null;
    }

    public boolean isRegistered(Item item) {
        return itemStorage.getEntity(item) != null;
    }

    @NotNull
    public GameItem registerEntity(Item item) {
        GameItem gameItem = new DefaultGameItem(item);

        itemStorage.addEntity(gameItem);

        return gameItem;
    }
}
