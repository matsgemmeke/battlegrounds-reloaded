package com.github.matsgemmeke.battlegrounds.game;

import com.github.matsgemmeke.battlegrounds.api.entity.GamePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.TrainingMode;
import com.github.matsgemmeke.battlegrounds.api.item.Item;
import com.github.matsgemmeke.battlegrounds.entity.DefaultGamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DefaultTrainingMode extends AbstractGame implements TrainingMode {

    @NotNull
    private List<Item> droppedItems;
    @NotNull
    private List<GamePlayer> players;

    public DefaultTrainingMode() {
        this.droppedItems = new ArrayList<>();
        this.players = new ArrayList<>();
    }

    @NotNull
    public GamePlayer addPlayer(@NotNull Player player) {
        GamePlayer gamePlayer = new DefaultGamePlayer(player);

        players.add(gamePlayer);

        return gamePlayer;
    }

    @NotNull
    public List<GamePlayer> getPlayers() {
        return players;
    }

    public boolean onInteract(@NotNull GamePlayer gamePlayer, @NotNull PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();

        if (itemStack == null) {
            return false;
        }

        Item item = gamePlayer.getItem(itemStack);

        if (item == null) {
            return false;
        }

        Action action = event.getAction();

        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            item.onLeftClick(gamePlayer);
        } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            item.onRightClick(gamePlayer);
        }

        event.setCancelled(true);
        return true;
    }

    public boolean onItemDrop(@NotNull GamePlayer gamePlayer, @NotNull PlayerDropItemEvent event) {
        ItemStack itemStack = event.getItemDrop().getItemStack();
        Item item = gamePlayer.getItem(itemStack);

        if (item == null) {
            return false;
        }

        droppedItems.add(item);
        item.onDrop(gamePlayer);
        return true;
    }

    public boolean onItemHeld(@NotNull GamePlayer gamePlayer, @NotNull PlayerItemHeldEvent event) {
        ItemStack itemStack = gamePlayer.getEntity().getInventory().getItemInMainHand();
        Item item = gamePlayer.getItem(itemStack);

        if (item == null) {
            return false;
        }

        item.onChangeHeldItem(gamePlayer);
        return true;
    }

    public boolean onPickupItem(@NotNull GamePlayer gamePlayer, @NotNull EntityPickupItemEvent event) {
        ItemStack itemStack = event.getItem().getItemStack();
        Item item = this.getItem(itemStack);

        if (item == null) {
            return false;
        }

        item.setHolder(gamePlayer);
        gamePlayer.addItem(item);

        droppedItems.remove(item);
        return true;
    }

    @Nullable
    private Item getItem(@NotNull ItemStack itemStack) {
        for (Item item : droppedItems) {
            if (item.getItemStack() != null && item.getItemStack().isSimilar(itemStack)) {
                return item;
            }
        }
        return null;
    }
}
