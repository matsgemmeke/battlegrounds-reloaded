package com.github.matsgemmeke.battlegrounds.game;

import com.github.matsgemmeke.battlegrounds.InternalsProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.GamePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.TrainingMode;
import com.github.matsgemmeke.battlegrounds.api.item.Weapon;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultTrainingMode extends AbstractGame implements TrainingMode {

    @NotNull
    private InternalsProvider internals;
    @NotNull
    private List<GamePlayer> players;
    @NotNull
    private Set<Weapon> droppedWeapons;

    public DefaultTrainingMode(@NotNull InternalsProvider internals) {
        this.internals = internals;
        this.players = new ArrayList<>();
        this.droppedWeapons = new HashSet<>();
    }

    @NotNull
    public GamePlayer addPlayer(@NotNull Player player) {
        GamePlayer gamePlayer = new DefaultGamePlayer(player, internals);

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

        Weapon weapon = gamePlayer.getWeapon(itemStack);

        if (weapon == null) {
            return false;
        }

        Action action = event.getAction();

        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            weapon.onLeftClick(gamePlayer);
        } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            weapon.onRightClick(gamePlayer);
        }

        event.setCancelled(true);
        return true;
    }

    public boolean onItemDrop(@NotNull GamePlayer gamePlayer, @NotNull PlayerDropItemEvent event) {
        ItemStack itemStack = event.getItemDrop().getItemStack();
        Weapon weapon = gamePlayer.getWeapon(itemStack);

        if (weapon == null) {
            return false;
        }

        droppedWeapons.add(weapon);
        weapon.onDrop(gamePlayer);
        return true;
    }

    public boolean onItemHeld(@NotNull GamePlayer gamePlayer, @NotNull PlayerItemHeldEvent event) {
        ItemStack itemStack = gamePlayer.getEntity().getInventory().getItemInMainHand();
        Weapon weapon = gamePlayer.getWeapon(itemStack);

        if (weapon == null) {
            return false;
        }

        weapon.onChangeHeldItem(gamePlayer);
        return true;
    }

    public boolean onPickupItem(@NotNull GamePlayer gamePlayer, @NotNull EntityPickupItemEvent event) {
        ItemStack itemStack = event.getItem().getItemStack();
        Weapon weapon = this.getWeapon(itemStack);

        if (weapon == null) {
            return false;
        }

        weapon.setHolder(gamePlayer);
        gamePlayer.addWeapon(weapon);

        droppedWeapons.remove(weapon);
        return true;
    }

    @Nullable
    private Weapon getWeapon(@NotNull ItemStack itemStack) {
        for (Weapon weapon : droppedWeapons) {
            if (weapon.isMatching(itemStack)) {
                return weapon;
            }
        }
        return null;
    }
}
