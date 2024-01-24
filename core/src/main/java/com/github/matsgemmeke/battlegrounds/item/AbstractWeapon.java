package com.github.matsgemmeke.battlegrounds.item;

import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import com.github.matsgemmeke.battlegrounds.api.item.OperatingMode;
import com.github.matsgemmeke.battlegrounds.api.item.Weapon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractWeapon extends AbstractItem implements Weapon {

    @Nullable
    protected OperatingMode operatingMode;

    public AbstractWeapon(@NotNull GameContext context) {
        super(context);
    }

    @Nullable
    public OperatingMode getOperatingMode() {
        return operatingMode;
    }

    public void setOperatingMode(@Nullable OperatingMode operatingMode) {
        this.operatingMode = operatingMode;
    }
}
