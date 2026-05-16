package nl.matsgemmeke.battlegrounds.game.damage;

public enum DamageType {

    /**
     * Damage caused by direct impact from a bullet projectile.
     */
    BULLET_DAMAGE,
    /**
     * Damage caused by external factors in the world, such as lava, cactus etc.
     */
    ENVIRONMENTAL_DAMAGE,
    /**
     * Explosion damage caused by a normal world explosion (TNT, creeper, custom explosion etc.)
     */
    EXPLOSIVE_DAMAGE,
    /**
     * Explosion damage caused by an explosion originating from an item inside a game.
     */
    EXPLOSIVE_ITEM_DAMAGE,
    /**
     * Damages caused by fire created by items.
     */
    FIRE_DAMAGE,
    /**
     * Damage caused by an entity physically attacking another
     */
    MELEE_DAMAGE,
    /**
     * Damage caused by a projectile.
     */
    PROJECTILE_DAMAGE;
}
