package nl.matsgemmeke.battlegrounds.item.recoil;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class RandomSpreadRecoil implements RecoilProducer {

    private Float[] horizontalRecoilValues;
    private Float[] verticalRecoilValues;
    @NotNull
    private Random random;

    public RandomSpreadRecoil() {
        this.random = new Random();
    }

    public Float[] getHorizontalRecoilValues() {
        return horizontalRecoilValues;
    }

    public void setHorizontalRecoilValues(Float[] horizontalRecoilValues) {
        this.horizontalRecoilValues = horizontalRecoilValues;
    }

    public Float[] getVerticalRecoilValues() {
        return verticalRecoilValues;
    }

    public void setVerticalRecoilValues(Float[] verticalRecoilValues) {
        this.verticalRecoilValues = verticalRecoilValues;
    }

    @NotNull
    public Location produceRecoil(@NotNull RecoilReceiver receiver, @NotNull Location direction) {
        float relAccuracy = receiver.getRelativeAccuracy();

        float minHorRecoil = horizontalRecoilValues[0] / relAccuracy;
        float maxHorRecoil = horizontalRecoilValues[1] / relAccuracy;
        float minVerRecoil = verticalRecoilValues[0] / relAccuracy;
        float maxVerRecoil = verticalRecoilValues[1] / relAccuracy;

        float horRange = maxHorRecoil - minHorRecoil;
        float verRange = maxVerRecoil - minVerRecoil;

        float randomHorRecoil = random.nextFloat() * horRange + minHorRecoil;
        float randomVerRecoil = random.nextFloat() * verRange + minVerRecoil;

        // Invert the values by a 50 percent chance so the recoil spreads around the original direction
        if (random.nextBoolean()) {
            randomHorRecoil *= -1;
        }
        if (random.nextBoolean()) {
            randomVerRecoil *= -1;
        }

        direction.setYaw(direction.getYaw() + randomHorRecoil);
        direction.setPitch(direction.getPitch() + randomVerRecoil);

        return direction;
    }
}
