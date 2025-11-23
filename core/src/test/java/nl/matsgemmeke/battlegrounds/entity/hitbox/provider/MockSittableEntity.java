package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Sittable;

/**
 * Workaround class. Currently, no entities exist which implement Sittable but not implement Ageable.
 */
abstract class MockSittableEntity implements Entity, Sittable {
}
