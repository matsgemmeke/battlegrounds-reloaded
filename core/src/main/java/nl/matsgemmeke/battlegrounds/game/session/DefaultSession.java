package nl.matsgemmeke.battlegrounds.game.session;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.entity.DefaultGamePlayer;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.entity.GunHolder;
import nl.matsgemmeke.battlegrounds.game.BaseGame;
import nl.matsgemmeke.battlegrounds.item.ItemRegister;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class DefaultSession extends BaseGame implements Session {

    private final int id;
    @NotNull
    private InternalsProvider internals;
    @NotNull
    private ItemRegister<Equipment, EquipmentHolder> equipmentRegister;
    @NotNull
    private ItemRegister<Gun, GunHolder> gunRegister;
    @NotNull
    private SessionConfiguration configuration;

    public DefaultSession(
            int id,
            @NotNull SessionConfiguration configuration,
            @NotNull InternalsProvider internals,
            @NotNull ItemRegister<Gun, GunHolder> gunRegister
    ) {
        this.id = id;
        this.configuration = configuration;
        this.internals = internals;
    }

    @NotNull
    public SessionConfiguration getConfiguration() {
        return configuration;
    }

    @NotNull
    public ItemRegister<Equipment, EquipmentHolder> getEquipmentRegister() {
        return equipmentRegister;
    }

    @NotNull
    public ItemRegister<Gun, GunHolder> getGunRegister() {
        return gunRegister;
    }

    public int getId() {
        return id;
    }

    @NotNull
    public GamePlayer addPlayer(@NotNull Player player) {
        return new DefaultGamePlayer(player, internals);
    }

    @NotNull
    public Iterable<GamePlayer> getPlayers() {
        return Collections.emptyList();
    }

    public boolean handleItemChange(@NotNull GamePlayer gamePlayer, @Nullable ItemStack changeFrom, @Nullable ItemStack changeTo) {
        return false;
    }

    public boolean handleItemDrop(@NotNull GamePlayer gamePlayer, @NotNull ItemStack droppedItem) {
        return true;
    }

    public boolean handleItemLeftClick(@NotNull GamePlayer gamePlayer, @NotNull ItemStack clickedItem) {
        return true;
    }

    public boolean handleItemPickup(@NotNull GamePlayer gamePlayer, @NotNull ItemStack pickupItem) {
        return false;
    }

    public boolean handleItemRightClick(@NotNull GamePlayer gamePlayer, @NotNull ItemStack clickedItem) {
        return true;
    }

    public boolean handleItemSwap(@NotNull GamePlayer gamePlayer, @Nullable ItemStack swapFrom, @Nullable ItemStack swapTo) {
        return false;
    }
}
