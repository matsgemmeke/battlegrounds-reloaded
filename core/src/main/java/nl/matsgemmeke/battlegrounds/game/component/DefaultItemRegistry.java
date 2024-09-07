package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.entity.DefaultGameItem;
import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DefaultItemRegistry implements EntityRegistry<GameItem, Item> {

    @NotNull
    private EntityStorage<GameItem> itemStorage;

    public DefaultItemRegistry(@NotNull EntityStorage<GameItem> itemStorage) {
        this.itemStorage = itemStorage;
    }

    @Nullable
    public GameItem findByEntity(@NotNull Item itemEntity) {
        for (GameItem gameItem : itemStorage.getEntities()) {
            if (gameItem.getEntity() == itemEntity) {
                return gameItem;
            }
        }
        return null;
    }

    @Nullable
    public GameItem findByUUID(@NotNull UUID uuid) {
        return itemStorage.getEntity(uuid);
    }

    public boolean isRegistered(@NotNull Item item) {
        return itemStorage.getEntity(item) != null;
    }

    public boolean isRegistered(@NotNull UUID uuid) {
        return itemStorage.getEntity(uuid) != null;
    }

    @NotNull
    public GameItem registerEntity(@NotNull Item item) {
        GameItem gameItem = new DefaultGameItem(item);

        itemStorage.addEntity(gameItem);

        return gameItem;
    }
}
