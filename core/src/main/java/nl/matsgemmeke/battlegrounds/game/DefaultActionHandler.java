package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.item.ItemBehavior;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DefaultActionHandler implements ActionHandler {

    @NotNull
    private Game game;

    public DefaultActionHandler(@NotNull Game game) {
        this.game = game;
    }

    public boolean handleItemLeftClick(@NotNull Player player, @NotNull ItemStack clickedItem) {
        GamePlayer gamePlayer = game.getGamePlayer(player);

        if (gamePlayer == null) {
            return true;
        }

        boolean performAction = true;

        for (ItemBehavior behavior : game.getItemBehaviors()) {
            performAction = performAction & behavior.handleLeftClickAction(gamePlayer, clickedItem);
        }

        return performAction;
    }

    public boolean handleItemRightClick(@NotNull Player player, @NotNull ItemStack clickedItem) {
        GamePlayer gamePlayer = game.getGamePlayer(player);

        if (gamePlayer == null) {
            return true;
        }

        boolean performAction = true;

        for (ItemBehavior behavior : game.getItemBehaviors()) {
            performAction = performAction & behavior.handleRightClickAction(gamePlayer, clickedItem);
        }

        return performAction;
    }
}
