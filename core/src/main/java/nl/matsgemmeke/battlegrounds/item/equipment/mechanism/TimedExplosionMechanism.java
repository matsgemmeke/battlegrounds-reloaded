package nl.matsgemmeke.battlegrounds.item.equipment.mechanism;

public class TimedExplosionMechanism implements EquipmentMechanism {

    private boolean primed;

    public TimedExplosionMechanism() {
        this.primed = false;
    }

    public boolean isPrimed() {
        return primed;
    }

    public void prime() {
        primed = true;
    }
}
