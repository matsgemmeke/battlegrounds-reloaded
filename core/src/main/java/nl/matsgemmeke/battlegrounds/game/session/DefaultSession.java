package nl.matsgemmeke.battlegrounds.game.session;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.entity.*;
import nl.matsgemmeke.battlegrounds.game.BaseGame;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.ItemStorage;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DefaultSession extends BaseGame implements Session {

    private final int id;
    @NotNull
    private InternalsProvider internals;
    @NotNull
    private ItemStorage<Equipment, EquipmentHolder> equipmentStorage;
    @NotNull
    private ItemStorage<Gun, GunHolder> gunStorage;
    @NotNull
    private SessionConfiguration configuration;

    public DefaultSession(
            int id,
            @NotNull SessionConfiguration configuration,
            @NotNull InternalsProvider internals,
            @NotNull ItemStorage<Gun, GunHolder> gunStorage
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
    public GameContext getContext() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public ItemStorage<Equipment, EquipmentHolder> getEquipmentStorage() {
        return equipmentStorage;
    }

    @NotNull
    public ItemStorage<Gun, GunHolder> getGunStorage() {
        return gunStorage;
    }

    public int getId() {
        return id;
    }

    @NotNull
    public GamePlayer addPlayer(@NotNull Player player) {
        return new DefaultGamePlayer(player, internals);
    }

    public double calculateDamage(@NotNull Entity entity, @NotNull Entity damager, double damage) {
        return damage;
    }
}
