package nl.matsgemmeke.battlegrounds.item;

import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.Game;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import org.jetbrains.annotations.NotNull;

/**
 * Responsible for creating and setting up new weapon instances.
 */
public interface WeaponFactory {

    /**
     * Makes a new weapon based on the given id and adds it a {@link Game}'s item system. The given weapon id should
     * match the one from the weapon configuration files.
     *
     * @param configuration the configuration of the item file
     * @param game the game instance where the weapon will be registered in
     * @param context the context instance of the game
     * @return a new instance of the corresponding item
     */
    @NotNull
    Weapon make(@NotNull ItemConfiguration configuration, @NotNull Game game, @NotNull GameContext context);

    /**
     * Makes a new weapon based on the given id and adds it a {@link Game}'s item system. This method also assigns it to
     * a {@link GamePlayer}. The given weapon id should match the one from the weapon configuration files.
     *
     * @param configuration the configuration of the item file
     * @param game the game instance where the weapon will be registered in
     * @param context the context instance of the game
     * @return a new instance of the corresponding item
     */
    @NotNull
    Weapon make(@NotNull ItemConfiguration configuration, @NotNull Game game, @NotNull GameContext context, @NotNull GamePlayer gamePlayer);
}
