package nl.matsgemmeke.battlegrounds.item;

import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.Game;
import nl.matsgemmeke.battlegrounds.game.component.GameContext;
import org.jetbrains.annotations.NotNull;

/**
 * Responsible for creating and setting up new weapon instances.
 */
public interface WeaponFactory {

    /**
     * Makes a new weapon based on the given id and adds it to a game instance. The given weapon id should match the
     * one from the weapon configuration files.
     *
     * @param configuration the configuration of the item file
     * @param context the context of the game instance where the item should be registered
     * @return a new instance of the corresponding item
     */
    @NotNull
    Weapon make(@NotNull ItemConfiguration configuration, @NotNull Game game, @NotNull GameContext context);

    /**
     * Makes a new weapon based on the given id and adds it to a game instance while also assigning it to a player. The
     * given weapon id should match the one from the weapon configuration files.
     *
     * @param configuration the configuration of the item file
     * @param context the context of the game instance where the item should be registered
     * @param gamePlayer the player to register the weapon to
     * @return a new instance of the corresponding item
     */
    @NotNull
    Weapon make(@NotNull ItemConfiguration configuration, @NotNull Game game, @NotNull GameContext context, @NotNull GamePlayer gamePlayer);
}
