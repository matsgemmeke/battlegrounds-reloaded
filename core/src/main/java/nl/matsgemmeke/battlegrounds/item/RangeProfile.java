package nl.matsgemmeke.battlegrounds.item;

public record RangeProfile(double shortRangeDamage, double shortRangeDistance, double mediumRangeDamage,
                           double mediumRangeDistance, double longRangeDamage, double longRangeDistance) {

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
