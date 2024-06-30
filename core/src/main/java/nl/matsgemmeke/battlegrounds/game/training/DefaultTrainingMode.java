package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.entity.DefaultGamePlayer;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.entity.GunHolder;
import nl.matsgemmeke.battlegrounds.game.BaseGame;
import nl.matsgemmeke.battlegrounds.item.ItemBehavior;
import nl.matsgemmeke.battlegrounds.item.ItemRegister;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DefaultTrainingMode extends BaseGame implements TrainingMode {

    @NotNull
    private InternalsProvider internals;
    @NotNull
    private ItemRegister<Equipment, EquipmentHolder> equipmentRegister;
    @NotNull
    private ItemRegister<Gun, GunHolder> gunRegister;
    @NotNull
    private List<GamePlayer> players;

    public DefaultTrainingMode(
            @NotNull InternalsProvider internals,
            @NotNull ItemRegister<Equipment, EquipmentHolder> equipmentRegister,
            @NotNull ItemRegister<Gun, GunHolder> gunRegister
    ) {
        this.internals = internals;
        this.equipmentRegister = equipmentRegister;
        this.gunRegister = gunRegister;
        this.players = new ArrayList<>();
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
    public GamePlayer addPlayer(@NotNull Player player) {
        GamePlayer gamePlayer = new DefaultGamePlayer(player, internals);

        players.add(gamePlayer);

        return gamePlayer;
    }

    public double calculateDamage(@NotNull Entity damager, @NotNull Entity entity, double damage) {
        return damage;
    }

    @NotNull
    public List<GamePlayer> getPlayers() {
        return players;
    }

    public boolean handleItemChange(@NotNull GamePlayer gamePlayer, @Nullable ItemStack changeFrom, @Nullable ItemStack changeTo) {
        boolean performAction = true;

        for (ItemBehavior behavior : behaviors) {
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

        for (ItemBehavior behavior : behaviors) {
            performAction = performAction & behavior.handleDropItemAction(gamePlayer, droppedItem);
        }

        return performAction;
    }

    public boolean handleItemLeftClick(@NotNull GamePlayer gamePlayer, @NotNull ItemStack clickedItem) {
        boolean performAction = true;

        for (ItemBehavior behavior : behaviors) {
            performAction = performAction & behavior.handleLeftClickAction(gamePlayer, clickedItem);
        }

        return performAction;
    }

    public boolean handleItemPickup(@NotNull GamePlayer gamePlayer, @NotNull ItemStack pickupItem) {
        boolean performAction = true;

        for (ItemBehavior behavior : behaviors) {
            performAction = performAction & behavior.handlePickupItemAction(gamePlayer, pickupItem);
        }

        return performAction;
    }

    public boolean handleItemRightClick(@NotNull GamePlayer gamePlayer, @NotNull ItemStack clickedItem) {
        boolean performAction = true;

        for (ItemBehavior behavior : behaviors) {
            performAction = performAction & behavior.handleRightClickAction(gamePlayer, clickedItem);
        }

        return performAction;
    }

    public boolean handleItemSwap(@NotNull GamePlayer gamePlayer, @Nullable ItemStack swapFrom, @Nullable ItemStack swapTo) {
        boolean performAction = true;

        for (ItemBehavior behavior : behaviors) {
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
