package com.github.matsgemmeke.battlegrounds.item;

import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.api.item.OperatingMode;
import com.github.matsgemmeke.battlegrounds.api.item.Weapon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractWeapon extends AbstractBattleItem implements Weapon {

    @Nullable
    protected OperatingMode currentOperatingMode;

    public AbstractWeapon(
            @NotNull String id,
            @NotNull String name,
            @NotNull BattleContext context
    ) {
        super(id, name, context);
    }

    @Nullable
    public OperatingMode getCurrentOperatingMode() {
        return currentOperatingMode;
    }

    public void setCurrentOperatingMode(@Nullable OperatingMode operatingMode) {
        this.currentOperatingMode = operatingMode;
    }
}
