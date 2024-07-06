package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.entity.*;
import nl.matsgemmeke.battlegrounds.game.BaseGame;
import nl.matsgemmeke.battlegrounds.item.ItemBehavior;
import nl.matsgemmeke.battlegrounds.item.ItemRegister;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultTrainingMode extends BaseGame implements TrainingMode {

    @NotNull
    private InternalsProvider internals;
    @NotNull
    private ItemRegister<Equipment, EquipmentHolder> equipmentRegister;
    @NotNull
    private ItemRegister<Gun, GunHolder> gunRegister;

    public DefaultTrainingMode(
            @NotNull InternalsProvider internals,
            @NotNull ItemRegister<Equipment, EquipmentHolder> equipmentRegister,
            @NotNull ItemRegister<Gun, GunHolder> gunRegister
    ) {
        this.internals = internals;
        this.equipmentRegister = equipmentRegister;
        this.gunRegister = gunRegister;
    }

    @NotNull
    public ItemRegister<Equipment, EquipmentHolder> getEquipmentRegister() {
        return equipmentRegister;
    }

    @NotNull
    public ItemRegister<Gun, GunHolder> getGunRegister() {
        return gunRegister;
    }

    @NotNull
    public GameItem addItem(@NotNull Item item) {
        GameItem gameItem = new DefaultGameItem(item);

        itemEntityRegister.addEntity(gameItem);

        return gameItem;
    }

    @NotNull
    public GamePlayer addPlayer(@NotNull Player player) {
        GamePlayer gamePlayer = new DefaultGamePlayer(player, internals);

        playerRegister.addEntity(gamePlayer);

        return gamePlayer;
    }

    public double calculateDamage(@NotNull Entity damager, @NotNull Entity entity, double damage) {
        return damage;
    }

    public boolean handleItemChange(@NotNull GamePlayer gamePlayer, @Nullable ItemStack changeFrom, @Nullable ItemStack changeTo) {
        boolean performAction = true;

        for (ItemBehavior behavior : itemBehaviors) {
            if (changeFrom != null) {
                performAction = performAction & behavior.handleChangeFromAction(gamePlayer, changeFrom);
            }

            if (changeTo != null) {
                performAction = performAction & behavior.handleChangeToAction(gamePlayer, changeTo);
            }
        }

        return performAction;
    }

    public boolean handleItemDrop(@NotNull GamePlayer gamePlayer, @NotNull ItemStack droppedItem) {
        boolean performAction = true;

        for (ItemBehavior behavior : itemBehaviors) {
            performAction = performAction & behavior.handleDropItemAction(gamePlayer, droppedItem);
        }

        return performAction;
    }

    public boolean handleItemPickup(@NotNull GamePlayer gamePlayer, @NotNull ItemStack pickupItem) {
        boolean performAction = true;

        for (ItemBehavior behavior : itemBehaviors) {
            performAction = performAction & behavior.handlePickupItemAction(gamePlayer, pickupItem);
        }

        return performAction;
    }

    public boolean handleItemSwap(@NotNull GamePlayer gamePlayer, @Nullable ItemStack swapFrom, @Nullable ItemStack swapTo) {
        boolean performAction = true;

        for (ItemBehavior behavior : itemBehaviors) {
            if (swapFrom != null) {
                performAction = performAction & behavior.handleSwapFromAction(gamePlayer, swapFrom);
            }

            if (swapTo != null) {
                performAction = performAction & behavior.handleSwapToAction(gamePlayer, swapTo);
            }
        }

        return performAction;
    }
}
