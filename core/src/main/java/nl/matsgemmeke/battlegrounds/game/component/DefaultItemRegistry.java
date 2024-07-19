package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.entity.DefaultGameItem;
import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.game.Game;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;

public class DefaultItemRegistry implements EntityRegistry<Item, GameItem> {

    @NotNull
    private Game game;

    public DefaultItemRegistry(@NotNull Game game) {
        this.game = game;
    }

    @NotNull
    public GameItem registerEntity(Item entity) {
        GameItem gameItem = new DefaultGameItem(entity);

        game.getItemStorage().addEntity(gameItem);

        return gameItem;
    }
}
