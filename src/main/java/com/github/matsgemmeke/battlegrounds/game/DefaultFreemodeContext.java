package com.github.matsgemmeke.battlegrounds.game;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.entity.BattleEntity;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.FreemodeContext;
import com.github.matsgemmeke.battlegrounds.api.item.BattleItem;
import com.github.matsgemmeke.battlegrounds.entity.GeneralBattleEntity;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultFreemodeContext extends AbstractBattleContext implements FreemodeContext {

    public DefaultFreemodeContext(@NotNull TaskRunner taskRunner) {
        super(taskRunner);
    }

    @NotNull
    public Iterable<BattleEntity> getTargets(@NotNull BattleEntity battleEntity, @NotNull Location location, double range) {
        if (location.getWorld() == null) {
            return Collections.emptyList();
        }

        List<BattleEntity> entities = new ArrayList<>();

        for (Entity entity : location.getWorld().getNearbyEntities(location, range, range, range)) {
            if (entity != battleEntity.getEntity() && entity instanceof LivingEntity) {
                entities.add(new GeneralBattleEntity((LivingEntity) entity));
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
}
