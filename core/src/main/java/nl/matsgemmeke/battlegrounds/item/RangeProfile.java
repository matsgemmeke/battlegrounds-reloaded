package nl.matsgemmeke.battlegrounds.item;

public class RangeProfile {

    private double longRangeDamage;
    private double longRangeDistance;
    private double mediumRangeDamage;
    private double mediumRangeDistance;
    private double shortRangeDamage;
    private double shortRangeDistance;

    public RangeProfile(
            double longRangeDamage,
            double longRangeDistance,
            double mediumRangeDamage,
            double mediumRangeDistance,
            double shortRangeDamage,
            double shortRangeDistance
    ) {
        this.longRangeDamage = longRangeDamage;
        this.longRangeDistance = longRangeDistance;
        this.mediumRangeDamage = mediumRangeDamage;
        this.mediumRangeDistance = mediumRangeDistance;
        this.shortRangeDamage = shortRangeDamage;
        this.shortRangeDistance = shortRangeDistance;
    }

    public double getLongRangeDamage() {
        return longRangeDamage;
    }

    public double getLongRangeDistance() {
        return longRangeDistance;
    }

    public double getMediumRangeDamage() {
        return mediumRangeDamage;
    }

    public double getMediumRangeDistance() {
        return mediumRangeDistance;
    }

    public double getShortRangeDamage() {
        return shortRangeDamage;
    }

    public double getShortRangeDistance() {
        return shortRangeDistance;
    }

    public double getDamageByDistance(double distance) {
        if (distance <= shortRangeDistance) {
            return shortRangeDamage;
        } else if (distance <= mediumRangeDistance) {
            return mediumRangeDamage;
        } else if (distance <= longRangeDistance) {
            return longRangeDamage;
        }
        return 0.0;
    }
}
