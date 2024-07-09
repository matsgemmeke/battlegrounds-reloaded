package nl.matsgemmeke.battlegrounds.game.session;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.entity.*;
import nl.matsgemmeke.battlegrounds.game.BaseGame;
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
    public GameItem addItem(@NotNull Item item) {
        return new DefaultGameItem(item);
    }

    @NotNull
    public GamePlayer addPlayer(@NotNull Player player) {
        return new DefaultGamePlayer(player, internals);
    }

    public double calculateDamage(@NotNull Entity entity, @NotNull Entity damager, double damage) {
        return damage;
    }

    public boolean handleItemSwap(@NotNull GamePlayer gamePlayer, @Nullable ItemStack swapFrom, @Nullable ItemStack swapTo) {
        return false;
    }
}
