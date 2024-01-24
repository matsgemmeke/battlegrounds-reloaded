package com.github.matsgemmeke.battlegrounds.item;

import com.github.matsgemmeke.battlegrounds.api.entity.WeaponHolder;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import com.github.matsgemmeke.battlegrounds.api.item.OperatingMode;
import com.github.matsgemmeke.battlegrounds.api.item.Weapon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractWeapon extends AbstractItem implements Weapon {

    @Nullable
    protected OperatingMode operatingMode;
    @Nullable
    protected WeaponHolder holder;

    public AbstractWeapon(@NotNull GameContext context) {
        super(context);
    }

    @Nullable
    public WeaponHolder getHolder() {
        return holder;
    }

    public void setHolder(@Nullable WeaponHolder holder) {
        this.holder = holder;
    }

    @Nullable
    public OperatingMode getOperatingMode() {
        return operatingMode;
    }

    public void setOperatingMode(@Nullable OperatingMode operatingMode) {
        this.operatingMode = operatingMode;
    }
}
