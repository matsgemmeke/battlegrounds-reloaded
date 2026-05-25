package nl.matsgemmeke.battlegrounds.game.damage;

public record DamageContext(
        DamageSource source,
        DamageTarget target,
        String itemName,
        Damage damage,
        double distance
) {
}
