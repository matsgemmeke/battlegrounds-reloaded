package nl.matsgemmeke.battlegrounds.game.damage;

public record DamageContext(DamageSource source, DamageTarget target, Damage damage) {

    public DamageContext modifyDamageAmount(double amount) {
        return new DamageContext(source, target, new Damage(amount, damage.type()));
    }
}
