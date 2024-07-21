package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.entity.GunHolder;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;

/**
 * Entry point for provision of segregated interfaces for a game instance.
 */
public interface GameContext {

    @NotNull
    AudioEmitter getAudioEmitter();

    @NotNull
    CollisionDetector getCollisionDetector();

    @NotNull
    ItemRegistry<Equipment, EquipmentHolder> getEquipmentRegistry();

    @NotNull
    EntityRegistry<Item, GameItem> getItemRegistry();

    @NotNull
    ItemRegistry<Gun, GunHolder> getGunRegistry();
}
