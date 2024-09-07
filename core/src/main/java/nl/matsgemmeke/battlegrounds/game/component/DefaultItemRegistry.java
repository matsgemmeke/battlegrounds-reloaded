package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.entity.DefaultGameItem;
import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import org.bukkit.entity.Entity;
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
    public GameItem findByEntity(@NotNull Entity entity) {
        for (GameItem gameItem : itemStorage.getEntities()) {
            if (gameItem.getEntity() == entity) {
                return gameItem;
            }
        }
        return null;
    }

    @Nullable
    public GameItem findByUUID(@NotNull UUID uuid) {
        return itemStorage.getEntity(uuid);
    }

    public boolean isRegistered(@NotNull Entity entity) {
        return itemStorage.getEntity(entity) != null;
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
