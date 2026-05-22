package nl.matsgemmeke.battlegrounds.game.damage;

public record DamageContext(
        DamageSource source,
        DamageTarget target,
        Damage damage,
        double distance
) {
}
