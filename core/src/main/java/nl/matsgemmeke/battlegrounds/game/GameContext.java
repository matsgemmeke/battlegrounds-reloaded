package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Entry point for provision of segregated interfaces for a game instance.
 */
public interface GameContext {

    @NotNull
    ActionHandler getActionHandler();

    @NotNull
    AudioEmitter getAudioEmitter();

    @NotNull
    CollisionDetector getCollisionDetector();

    @NotNull
    DamageProcessor getDamageProcessor();

    @NotNull
    ItemRegistry<Equipment, EquipmentHolder> getEquipmentRegistry();

    @NotNull
    EntityRegistry<GameItem, Item> getItemRegistry();

    @NotNull
    ItemRegistry<Gun, GunHolder> getGunRegistry();

    @NotNull
    EntityRegistry<GamePlayer, Player> getPlayerRegistry();
}
