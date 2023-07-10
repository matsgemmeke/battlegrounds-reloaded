package com.github.matsgemmeke.battlegrounds.game;

import com.github.matsgemmeke.battlegrounds.api.entity.BattleEntity;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.FreemodeContext;
import com.github.matsgemmeke.battlegrounds.api.item.BattleItem;
import com.github.matsgemmeke.battlegrounds.entity.DefaultBattlePlayer;
import com.github.matsgemmeke.battlegrounds.entity.FreemodeEntity;
import com.github.matsgemmeke.battlegrounds.item.BlockCollisionChecker;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DefaultFreemodeContext extends AbstractBattleContext implements FreemodeContext {

    @NotNull
    private List<BattleItem> droppedItems;
    @NotNull
    private List<BattlePlayer> players;

    public DefaultFreemodeContext(@NotNull BlockCollisionChecker collisionChecker) {
        super(collisionChecker);
        this.droppedItems = new ArrayList<>();
        this.players = new ArrayList<>();
    }

    @NotNull
    public BattlePlayer addPlayer(@NotNull Player player) {
        BattlePlayer battlePlayer = new DefaultBattlePlayer(player);

        players.add(battlePlayer);

        return battlePlayer;
    }

    @NotNull
    public List<BattlePlayer> getPlayers() {
        return players;
    }

    @NotNull
    public Collection<BattleEntity> getTargets(@NotNull BattleEntity battleEntity, @NotNull Location location, double range) {
        if (location.getWorld() == null) {
            return Collections.emptyList();
        }

        List<BattleEntity> entities = new ArrayList<>();

        for (Entity entity : location.getWorld().getNearbyEntities(location, range, range, range)) {
            if (entity != battleEntity.getEntity() && entity instanceof LivingEntity) {
                entities.add(new FreemodeEntity((LivingEntity) entity));
            }
        }

        return entities;
    }

    public boolean onInteract(@NotNull BattlePlayer battlePlayer, @NotNull PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();

        if (itemStack == null) {
            return false;
        }

        BattleItem item = battlePlayer.getBattleItem(itemStack);

        if (item == null) {
            return false;
        }

        Action action = event.getAction();

        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            item.onLeftClick(battlePlayer);
        } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            item.onRightClick(battlePlayer);
        }

        event.setCancelled(true);
        return true;
    }

    public boolean onItemDrop(@NotNull BattlePlayer battlePlayer, @NotNull PlayerDropItemEvent event) {
        ItemStack itemStack = event.getItemDrop().getItemStack();
        BattleItem item = battlePlayer.getBattleItem(itemStack);

        if (item == null) {
            return false;
        }

        droppedItems.add(item);
        item.onDrop(battlePlayer);
        return true;
    }

    public boolean onItemHeld(@NotNull BattlePlayer battlePlayer, @NotNull PlayerItemHeldEvent event) {
        ItemStack itemStack = battlePlayer.getEntity().getInventory().getItemInMainHand();
        BattleItem item = battlePlayer.getBattleItem(itemStack);

        if (item == null) {
            return false;
        }

        item.onChangeHeldItem(battlePlayer);
        return true;
    }

    public boolean onPickupItem(@NotNull BattlePlayer battlePlayer, @NotNull EntityPickupItemEvent event) {
        ItemStack itemStack = event.getItem().getItemStack();
        BattleItem item = this.getBattleItem(itemStack);

        if (item == null) {
            return false;
        }

        item.setHolder(battlePlayer);
        battlePlayer.addItem(item);

        droppedItems.remove(item);
        return true;
    }

    @Nullable
    private BattleItem getBattleItem(@NotNull ItemStack itemStack) {
        for (BattleItem item : droppedItems) {
            if (item.getItemStack() != null && item.getItemStack().isSimilar(itemStack)) {
                return item;
            }
        }
        return null;
    }
}
