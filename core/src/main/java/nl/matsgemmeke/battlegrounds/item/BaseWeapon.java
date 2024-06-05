package nl.matsgemmeke.battlegrounds.item;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import org.jetbrains.annotations.NotNull;

public abstract class BaseWeapon extends BaseItem implements Weapon {

    public BaseWeapon(@NotNull GameContext context) {
        super(context);
    }
}
