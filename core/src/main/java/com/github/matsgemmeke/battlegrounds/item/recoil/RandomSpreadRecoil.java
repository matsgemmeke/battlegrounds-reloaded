package com.github.matsgemmeke.battlegrounds.item.recoil;

import com.github.matsgemmeke.battlegrounds.api.entity.BattleItemHolder;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class RandomSpreadRecoil implements RecoilSystem {

    private float horizontalRecoil;
    private float verticalRecoil;
    @NotNull
    private Random random;

    public RandomSpreadRecoil() {
        this.random = new Random();
    }

    public float getHorizontalRecoil() {
        return horizontalRecoil;
    }

    public void setHorizontalRecoil(float horizontalRecoil) {
        this.horizontalRecoil = horizontalRecoil;
    }

    public float getVerticalRecoil() {
        return verticalRecoil;
    }

    public void setVerticalRecoil(float verticalRecoil) {
        this.verticalRecoil = verticalRecoil;
    }

    @NotNull
    public Location produceRecoil(@NotNull BattleItemHolder holder, @NotNull Location direction, double relativeAccuracy) {
        double horizontalRecoilRange = horizontalRecoil / relativeAccuracy;
        double verticalRecoilRange = verticalRecoil / relativeAccuracy;

        double yaw = random.nextDouble() * verticalRecoilRange - verticalRecoilRange / 2;
        double pitch = random.nextDouble() * horizontalRecoilRange - horizontalRecoilRange / 2;

        direction.setYaw(direction.getYaw() + (float) yaw);
        direction.setPitch(direction.getPitch() + (float) pitch);

        return direction;
    }
}
