package nl.matsgemmeke.battlegrounds.item;

import org.jetbrains.annotations.NotNull;

public abstract class BaseWeapon extends BaseItem implements Weapon {

    public BaseWeapon(@NotNull String id) {
        super(id);
    }
}
