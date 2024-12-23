package nl.matsgemmeke.battlegrounds.item.effect.flash;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class FlashEffect implements ItemEffect {

    @NotNull
    private FlashProperties properties;
    @NotNull
    private TargetFinder targetFinder;

    public FlashEffect(@NotNull FlashProperties properties, @NotNull TargetFinder targetFinder) {
        this.properties = properties;
        this.targetFinder = targetFinder;
    }

    public void activate(@NotNull ItemEffectContext context) {
        ItemHolder holder = context.getHolder();
        EffectSource source = context.getSource();

        this.createExplosionEffect(holder, source);
        this.applyPotionEffectToTargets(holder, source.getLocation());

        source.remove();
    }

    private void createExplosionEffect(@NotNull ItemHolder holder, @NotNull EffectSource source) {
        float power = properties.explosionPower();
        boolean setFire = properties.explosionSetFire();
        boolean breakBlocks = properties.explosionBreakBlocks();

        World world = source.getWorld();
        Location location = source.getLocation();
        Entity damageSource = holder.getEntity();

        world.createExplosion(location, power, setFire, breakBlocks, damageSource);
    }

    private void applyPotionEffectToTargets(@NotNull ItemHolder holder, @NotNull Location location) {
        for (GameEntity target : targetFinder.findTargets(holder, location, properties.range())) {
            PotionEffectType potionEffectType = PotionEffectType.BLINDNESS;
            int duration = properties.potionEffect().duration();
            int amplifier = properties.potionEffect().amplifier();
            boolean ambient = properties.potionEffect().ambient();
            boolean particles = properties.potionEffect().particles();
            boolean icon = properties.potionEffect().icon();

            PotionEffect potionEffect = new PotionEffect(potionEffectType, duration, amplifier, ambient, particles, icon);

            target.getEntity().addPotionEffect(potionEffect);
        }
    }
}
