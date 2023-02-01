package com.github.matsgemmeke.battlegrounds.item;

import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.api.item.Weapon;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractWeapon extends AbstractBattleItem implements Weapon {

    public AbstractWeapon(
            @NotNull String id,
            @NotNull String name,
            @NotNull String description,
            @NotNull BattleContext context
    ) {
        super(id, name, description, context);
    }
}
